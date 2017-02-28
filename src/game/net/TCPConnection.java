package game.net;

import game.exception.InvalidMessageException;
import game.exception.NameException;
import game.exception.ProtocolException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
	private final CharsetDecoder decoder = StandardCharsets.UTF_8
		.newDecoder()
		.onMalformedInput(CodingErrorAction.REPORT)
		.onUnmappableCharacter(CodingErrorAction.REPORT);
	
	private final Object sendLock = new Object();
	private final Object recvLock = new Object();
	
	private final Socket tcpSocket;
	private final byte[] tcpSendIntTemp = new byte[4];
	private final byte[] tcpRecvIntTemp = new byte[4];
	private final byte[] tcpRecvTemp = new byte[1400];
	
	public TCPConnection(Socket socket) throws ProtocolException {
		try {
			this.tcpSocket = socket;
			this.tcpSocket.setReuseAddress(true);
			this.tcpSocket.setKeepAlive(true);
			this.tcpSocket.setTcpNoDelay(true);
			
			this.tcpSocket.getInputStream();
			this.tcpSocket.getOutputStream();
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public TCPConnection(InetAddress address, int port) throws ProtocolException {
		try {
			this.tcpSocket = new Socket(address, port);
			this.tcpSocket.setReuseAddress(true);
			this.tcpSocket.setKeepAlive(true);
			this.tcpSocket.setTcpNoDelay(true);
			
			this.tcpSocket.getInputStream();
			this.tcpSocket.getOutputStream();
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public Socket getSocket() {
		return tcpSocket;
	}
	
	public void close() {
		try {
			if (!isClosed())
				this.sendString(Protocol.TCP_EXIT);
		} catch (ProtocolException e) {
			// We don't care about this
		} finally {
			try {
				tcpSocket.close();
			} catch (IOException e1) {
				// We don't care about this
			}
		}
	}
	
	public boolean isClosed() {
		return tcpSocket.isClosed();
	}
	
	public void sendString(String msg) throws ProtocolException {
		try {
			synchronized (sendLock) {
				byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
				int len = bytes.length;
				ByteBuffer.wrap(tcpSendIntTemp).order(ByteOrder.BIG_ENDIAN).putInt(len);
				
				// Send
				OutputStream out = tcpSocket.getOutputStream();
				out.write(tcpSendIntTemp);
				out.write(bytes);
				out.flush();
			}
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	/**
	 * Read exactly the amount specified into the buffer specified
	 */
	private static void readExact(Socket socket, byte[] buf, int length) throws IOException {
		InputStream in = socket.getInputStream();
		int totRead = 0;
		while (totRead < length) {
			// Read into buffer
			int read = in.read(buf, totRead, length - totRead);
			if (read == -1) {
				// EOF
				throw new EOFException();
			}
			totRead += read;
		}
	}
	
	public String recvString() throws ProtocolException {
		try {
			int len;
			synchronized (recvLock) {
				// First read the int
				readExact(tcpSocket, tcpRecvIntTemp, 4);
				len = ByteBuffer.wrap(tcpRecvIntTemp).order(ByteOrder.BIG_ENDIAN).getInt();
				
				// Then read len amount of bytes from stream
				readExact(tcpSocket, tcpRecvTemp, len);
			}
			
			// Decode the string
			ByteBuffer bytes = ByteBuffer.wrap(tcpRecvTemp);
			bytes.limit(len);
			return decoder.decode(bytes).toString();
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	/**
	 * Sends a connection request. The name of the client is sent,
	 * @param name The name
	 * @param port The port
	 */
	public void sendConnectionRequest(String name, int port) throws ProtocolException {
		this.sendString(Protocol.sendTcpConnectionRequest(name, port));
	}
	
	/**
	 * Receives a connection request
	 * @return The name of the client + the SocketAddress of the client
	 */
	public Tuple<String, SocketAddress> recvConnectionRequest() throws ProtocolException {
		String msg = this.recvString();
		if (!Protocol.isTcpConnectionRequest(msg))
			throw new ProtocolException("Not TCP Connection Request: " + msg);
		
		Tuple<String, Integer> pair = Protocol.parseTcpConnectionRequest(msg);
		String name = pair.getFirst();
		int port = pair.getSecond();
		return new Tuple<>(name, new InetSocketAddress(tcpSocket.getInetAddress(), port));
	}
	
	/**
	 * Send back a successful connection response
	 */
	public void sendConnectionResponseSuccess() throws ProtocolException {
		this.sendString(Protocol.sendTcpConnectionResponseAccept());
	}
	
	/**
	 * Send back a connection rejection
	 * @param reason The reason why the client was rejected
	 */
	public void sendConnectionResponseReject(String reason) throws ProtocolException {
		this.sendString(Protocol.sendTcpConnectionResponseReject(reason));
	}
	
	/**
	 * Recieves a connection response from the server.
	 * @throws NameException if another client is already connected to the server, or if the name given was invalid.
	 */
	public void recvConnectionResponse() throws NameException, ProtocolException {
		String response = this.recvString();
		if (Protocol.isTcpConnectionResponse(response)) {
			if (Protocol.isTcpConnectionResponseAccept(response)) {
				// Accepted - everything is fine
				return;
			} else {
				// Rejected - throw NameException
				throw new NameException(Protocol.parseTcpConnectionResponseReject(response));
			}
		} else {
			throw new InvalidMessageException(response);
		}
	}
}

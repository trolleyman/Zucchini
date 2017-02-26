package game.net;

import game.exception.InvalidMessageException;
import game.exception.NameException;
import game.exception.ProtocolException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
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
	
	private final Socket tcpSocket;
	private final byte[] tcpSendIntTemp = new byte[4];
	private final byte[] tcpRecvIntTemp = new byte[4];
	private final byte[] tcpRecvTemp = new byte[1400];
	
	public TCPConnection(Socket socket) throws ProtocolException {
		try {
			this.tcpSocket = socket;
			socket.getInputStream();
			socket.getOutputStream();
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public TCPConnection(InetAddress address, int port) throws ProtocolException {
		try {
			tcpSocket = new Socket(address, port);
			tcpSocket.setReuseAddress(true);
			tcpSocket.setKeepAlive(true);
			tcpSocket.setTcpNoDelay(true);
			
			tcpSocket.getInputStream();
			tcpSocket.getOutputStream();
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public Socket getSocket() {
		return tcpSocket;
	}
	
	public synchronized void close() {
		try {
			this.sendString(Protocol.TCP_EXIT);
			tcpSocket.close();
		} catch (IOException | ProtocolException e) {
			// We don't care about this
		}
	}
	
	public synchronized void sendString(String msg) throws ProtocolException {
		try {
			byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
			int len = bytes.length;
			ByteBuffer.wrap(tcpSendIntTemp).order(ByteOrder.BIG_ENDIAN).putInt(len);
			
			// Send
			OutputStream out = tcpSocket.getOutputStream();
			out.write(tcpSendIntTemp);
			out.write(bytes);
			out.flush();
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
	
	public synchronized String recvString() throws ProtocolException {
		try {
			// First read the int
			readExact(tcpSocket, tcpRecvIntTemp, 4);
			int len = ByteBuffer.wrap(tcpRecvIntTemp).order(ByteOrder.BIG_ENDIAN).getInt();
			
			// Then read len amount of bytes from stream
			readExact(tcpSocket, tcpRecvTemp, len);
			
			// Decode the string
			ByteBuffer bytes = ByteBuffer.wrap(tcpRecvTemp);
			bytes.limit(len);
			return decoder.decode(bytes).toString();
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	/**
	 * Sends a connection request with the specified name
	 * @param name The name
	 */
	public void sendConnectionRequest(String name) throws ProtocolException {
		this.sendString(Protocol.TCP_CONNECT_REQUEST + name);
	}
	
	/**
	 * Recieves a conenction response from the server.
	 * @throws NameException if another client is already connected to the server, or if the name given was invalid.
	 */
	public void recvConnectionResponse() throws NameException, ProtocolException {
		String response = this.recvString();
		if (response.startsWith(Protocol.TCP_CONNECT_RESPONSE_ACC)) {
			// Accepted - everything is fine
			return;
		} else if (response.startsWith(Protocol.TCP_CONNECT_RESPONSE_REJ)) {
			// Rejected - throw NameException
			throw new NameException(response.substring(Protocol.TCP_CONNECT_RESPONSE_REJ.length()));
		} else {
			throw new InvalidMessageException(response);
		}
	}
}

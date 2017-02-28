package game.net;

import game.exception.ProtocolException;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class UDPConnection {
	private final CharsetDecoder decoder = StandardCharsets.UTF_8
		.newDecoder()
		.onMalformedInput(CodingErrorAction.REPORT)
		.onUnmappableCharacter(CodingErrorAction.REPORT);
	
	private final Object sendLock = new Object();
	private final Object recvLock = new Object();
	
	private final DatagramSocket udpSocket;
	private final byte[] udpRecvTemp = new byte[1400];
	
	public UDPConnection() throws ProtocolException {
		try {
			udpSocket = new DatagramSocket(null);
			setSocketVars();
			udpSocket.bind(null); // Bind to ephemeral port
		} catch (SocketException e) {
			throw new ProtocolException(e);
		}
	}
	
	public UDPConnection(int port) throws ProtocolException {
		try {
			udpSocket = new DatagramSocket(null);
			setSocketVars();
			//udpSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
			udpSocket.bind(new InetSocketAddress(port));
		} catch (SocketException e) {
			throw new ProtocolException(e);
		}
	}
	
	private void setSocketVars() throws SocketException {
		udpSocket.setBroadcast(true);
		udpSocket.setReuseAddress(true);
	}
	
	public void connect(SocketAddress address) throws ProtocolException {
		try {
			this.udpSocket.connect(address);
		} catch (SocketException e) {
			throw new ProtocolException(e);
		}
	}
	
	public void sendString(String msg, SocketAddress address) throws ProtocolException {
		try {
			// Encode + send string
			byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
			synchronized (sendLock) {
				udpSocket.send(packet);
			}
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public void sendString(String msg) throws ProtocolException {
		try {
			// Encode + send string
			byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, udpSocket.getInetAddress(), udpSocket.getPort());
			synchronized (sendLock) {
				udpSocket.send(packet);
			}
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public synchronized DatagramPacket recv() throws ProtocolException {
		try {
			// Recv packet
			DatagramPacket packet = new DatagramPacket(udpRecvTemp, udpRecvTemp.length);
			synchronized (recvLock) {
				udpSocket.receive(packet);
			}
			
			// Return packet with new buffer
			int len = packet.getData().length;
			byte[] newBytes = new byte[len];
			System.arraycopy(packet.getData(), 0, newBytes, 0, len);
			packet.setData(newBytes);
			return packet;
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public String decode(DatagramPacket recv) throws ProtocolException {
		try {
			synchronized (decoder) {
				return decoder.decode(ByteBuffer.wrap(recv.getData())).toString();
			}
		} catch (CharacterCodingException e) {
			throw new ProtocolException(e);
		}
	}
	
	public String recvString() throws ProtocolException {
		try {
			// Recv packet
			DatagramPacket packet = new DatagramPacket(udpRecvTemp, udpRecvTemp.length);
			synchronized (recvLock) {
				udpSocket.receive(packet);
			}
			
			// Decode bytes
			return this.decode(packet);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public void close() {
		try {
			this.sendString(Protocol.UDP_EXIT);
		} catch (ProtocolException e) {
			// We don't care about this
		}
		udpSocket.close();
	}
	
	public DatagramSocket getSocket() {
		return udpSocket;
	}
}

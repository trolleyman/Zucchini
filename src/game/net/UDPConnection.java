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
	
	private final DatagramSocket udpSocket;
	private final byte[] udpRecvTemp = new byte[1400];
	
	public UDPConnection() throws ProtocolException {
		try {
			this.udpSocket = new DatagramSocket();
			this.udpSocket.setBroadcast(true);
		} catch (SocketException e) {
			throw new ProtocolException(e);
		}
	}
	
	public UDPConnection(DatagramSocket socket) throws ProtocolException {
		try {
			this.udpSocket = socket;
			this.udpSocket.setBroadcast(true);
		} catch (SocketException e) {
			throw new ProtocolException(e);
		}
	}
	
	public void bind(InetSocketAddress address) throws ProtocolException {
		try {
			this.udpSocket.bind(address);
		} catch (SocketException e) {
			throw new ProtocolException(e);
		}
	}
	
	public synchronized void sendString(String msg, InetSocketAddress address) throws ProtocolException {
		try {
			// Encode + send string
			byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
			udpSocket.send(packet);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public synchronized void sendString(String msg) throws ProtocolException {
		try {
			// Encode + send string
			byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, udpSocket.getInetAddress(), udpSocket.getPort());
			udpSocket.send(packet);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public synchronized DatagramPacket recv() throws ProtocolException {
		try {
			// Recv packet
			DatagramPacket packet = new DatagramPacket(udpRecvTemp, udpRecvTemp.length);
			udpSocket.receive(packet);
			
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
			return decoder.decode(ByteBuffer.wrap(recv.getData())).toString();
		} catch (CharacterCodingException e) {
			throw new ProtocolException(e);
		}
	}
	
	public synchronized String recvString() throws ProtocolException {
		try {
			// Recv packet
			DatagramPacket packet = new DatagramPacket(udpRecvTemp, udpRecvTemp.length);
			udpSocket.receive(packet);
			
			// Decode bytes
			return this.decode(packet);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}
	
	public synchronized void close() {
		try {
			if (this.udpSocket.isBound())
				this.sendString(Protocol.UDP_EXIT);
		} catch (ProtocolException e) {
			// We don't care about this
		}
		udpSocket.close();
	}
}

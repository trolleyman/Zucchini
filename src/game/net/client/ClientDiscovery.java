package game.net.client;

import java.net.*;

import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.net.TCPConnection;
import game.net.UDPConnection;

public class ClientDiscovery {
	
	private String name;
	
	private TCPConnection tcpConn;
	private UDPConnection udpConn;
	
	public ClientDiscovery(String name) {
		this.name = name;
	}
	
	private void out(String msg) {
		System.out.println("[UDP Discovery] " + name + ": " + msg);
	}
	
	public void tryDiscover() throws NameException, ProtocolException {
		// Broadcast discovery packet
		UDPConnection broadcastConn = new UDPConnection();
		InetSocketAddress broadcastAddress = new InetSocketAddress("255.255.255.255", Protocol.UDP_DISCOVERY_PORT);
		broadcastConn.sendString(Protocol.sendDiscoveryRequest(), broadcastAddress);
		out("Sent discovery packet to " + broadcastAddress + ", waiting for response...");
		
		// Wait for a response
		while (true) {
			// Recieve packet on port
			DatagramPacket recv = broadcastConn.recv();
			
			// We have a response
			InetAddress serverAddress = recv.getAddress();
			out("Recv broadcast response from " + serverAddress.getHostAddress());
			
			// Decode packet
			String msg = broadcastConn.decode(recv);
			out("Message: " + msg);
			
			if (!Protocol.isDiscoveryResponse(msg)) {
				out("Invalid response.");
			} else {
				out("Server found! Creating TCP connection...");
				
				tcpConn = new TCPConnection(serverAddress, Protocol.TCP_SERVER_PORT);
				
				// Set UDP connection
				udpConn = new UDPConnection();
				udpConn.connect(new InetSocketAddress(serverAddress, Protocol.UDP_SERVER_PORT));
				int localPort = udpConn.getSocket().getLocalPort();
				
				// Now to actually connect - send request with local port to connect to.
				tcpConn.sendConnectionRequest(name, localPort);
				
				// Recieve response
				try {
					tcpConn.recvConnectionResponse();
				} catch (NameException e) {
					// Name is already in use / name is invalid
					throw e;
				}
				
				// Name is valid
				break;
			}
		}
	}
	
	public TCPConnection getTCP() {
		return tcpConn;
	}
	
	public UDPConnection getUDP() {
		return udpConn;
	}
}

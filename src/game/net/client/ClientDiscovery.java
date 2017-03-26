package game.net.client;

import java.net.*;
import java.util.Enumeration;
import java.util.List;

import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.net.TCPConnection;
import game.net.UDPConnection;
import sun.nio.ch.Net;

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
	
	private void broadcastDiscoveryPacket(UDPConnection broadcastConn) throws ProtocolException {
		// Broadcast discovery packet
		InetSocketAddress broadcastAddress = new InetSocketAddress("255.255.255.255", Protocol.UDP_DISCOVERY_PORT);
		broadcastConn.sendString(Protocol.sendDiscoveryRequest(), broadcastAddress);
		out("Sent discovery packet to " + broadcastAddress + "...");
		
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				List<InterfaceAddress> ints = ni.getInterfaceAddresses();
				for (InterfaceAddress addr : ints) {
					InetAddress inetAddr = addr.getBroadcast();
					if (inetAddr == null)
						continue;
					broadcastAddress = new InetSocketAddress(inetAddr, Protocol.UDP_DISCOVERY_PORT);
					broadcastConn.sendString(Protocol.sendDiscoveryRequest(), broadcastAddress);
					out("Sent discovery packet to " + broadcastAddress + "...");
				}
			}
		} catch (SocketException e) {
			throw new ProtocolException("Could not open NetworkInterface", e);
		}
	}
	
	/**
	 * Try infinitely to discover the server.
	 */
	public void tryDiscover() throws NameException, ProtocolException {
		tryDiscover(-1);
	}
	
	/**
	 * Try to discover the server a specified number of times. If the number of tries is exceeded, throws a ProtocolException
	 * @param tries The number of tries
	 */
	public void tryDiscover(int tries) throws NameException, ProtocolException {
		UDPConnection broadcastConn = new UDPConnection();
		
		// Wait for a response
		while (true) {
			if (tries != -1) {
				if (tries == 0)
					throw new ProtocolException("Tries exceeded");
				tries--;
			}
			
			broadcastDiscoveryPacket(broadcastConn);
			
			// Recieve packet on port
			DatagramPacket recv = broadcastConn.recv(1500);
			
			if (recv == null) {
				out("Recv timed out. Trying again...");
				continue;
			}
			
			// We have a response
			InetAddress serverAddress = recv.getAddress();
			out("Recv broadcast response from " + recv.getSocketAddress());
			
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

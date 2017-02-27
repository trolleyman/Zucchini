package game.net.server;

import game.exception.ProtocolException;
import game.net.Protocol;
import game.net.TCPConnection;
import game.net.Tuple;
import game.net.UDPConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class Server implements Runnable {
	private volatile boolean running;
	
	private Thread udpDiscoveryServer;
	private Thread tcpServer;
	
	
	private final Object clientsLock = new Object();
	private ArrayList<ClientInfo> clients = new ArrayList<>();
	
	public Server() {
		
	}
	
	@Override
	public void run() {
		running = true;
		
		udpDiscoveryServer = new Thread(this::runUdpDiscoveryServer, "UDP Discovery Server");
		udpDiscoveryServer.start();
		
		tcpServer = new Thread(this::runTcpServer, "TCP Connection Server");
		tcpServer.start();
	}
	
	private void outUDP(String msg) {
		System.out.println("[UDP]: " + msg);
	}
	private void outTCP(String msg) {
		System.out.println("[TCP]: " + msg);
	}
	
	private void runUdpDiscoveryServer() {
		outUDP("[Discovery]: Listening on port " + Protocol.UDP_DISCOVERY_PORT + "...");
		
		UDPConnection conn;
		try {
			conn = new UDPConnection(Protocol.UDP_DISCOVERY_PORT);
		} catch (ProtocolException e) {
			// Could not open socket - exit
			throw new RuntimeException(e);
		}
		
		while (running) {
			try {
				DatagramPacket packet = conn.recv();
				String msg = conn.decode(packet);
				if (Protocol.isDiscoveryRequest(msg)) {
					// Respond
					outUDP("[Discovery]: Received discovery request from " + packet.getSocketAddress());
					conn.sendString(Protocol.sendDiscoveryResponse(), packet.getSocketAddress());
					outUDP("[Discovery]: Sent discovery response to " + packet.getSocketAddress());
				} else {
					outUDP("[Discovery]: Unknown message received: " + msg);
				}
			} catch (ProtocolException e) {
				System.err.println("[UDP]: [Discovery]: Error: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	private void runTcpServer() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Protocol.TCP_SERVER_PORT);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			System.err.println("[TCP]: Error: Unable to open TCP Socket");
			e.printStackTrace();
			System.exit(1);
		}
		
		outTCP("[Accept]: Listening on port " + Protocol.TCP_SERVER_PORT + "...");
		while (running) {
			Socket sock;
			try {
				// Accept TCP connection
				sock = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Error: Unable to accept TCP Socket");
				e.printStackTrace();
				System.exit(1);
				return;
			}
			
			Thread t = new Thread(() -> acceptClient(sock), "TCP Handler: " + sock.getRemoteSocketAddress());
			t.start();
		}
	}
	
	private void acceptClient(Socket sock) {
		try {
			outTCP("[Accept]: " + sock.getRemoteSocketAddress() + ": Accepting client...");
			TCPConnection tcpConn = new TCPConnection(sock);
			
			Tuple<String, SocketAddress> pair = tcpConn.recvConnectionRequest();
			String name = pair.getFirst();
			SocketAddress address = pair.getSecond();
			
			outTCP("[Accept]: " + sock.getRemoteSocketAddress() + ": name='" + name + "', UDP address='" + address + "'");
			
			UDPConnection udpConn = new UDPConnection(Protocol.UDP_SERVER_PORT);
			udpConn.connect(address);
			
			String error = null;
			synchronized (clientsLock) {
				for (ClientInfo ci : clients) {
					if (ci.name.equals(name)) {
						// Error
						error = name + " is already connected.";
						break;
					}
				}
				
				if (error == null) {
					// Send connection success back
					tcpConn.sendConnectionResponseSuccess();
					
					// Then add client info, as if there is an exception when sending the response,
					// this will not accept the client
					ClientInfo ci = new ClientInfo(name, tcpConn, udpConn);
					this.clients.add(ci);
					System.out.println(
							"[Net]: Client Connected: '" + name + "'"
									+ " @ TCP: " + tcpConn.getSocket().getRemoteSocketAddress()
									+ ", UDP: " + udpConn.getSocket().getRemoteSocketAddress()
					);
				}
			}
			
			if (error != null) {
				// Send error response back
				tcpConn.sendConnectionResponseReject(error);
				System.out.println("[Net]: Error while accepting client: " + error);
			}
			
		} catch (ProtocolException e) {
			outTCP("Exception while accepting client: " + e.toString());
			e.printStackTrace();
		}
	}
}

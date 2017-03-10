package game.net.server;

import game.LobbyInfo;
import game.Util;
import game.exception.ProtocolException;
import game.net.*;
import game.world.map.Map;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Runnable
{
	private volatile boolean running;

	private final Object lock = new Object();
	/** Name -> Handler */
	private final HashMap<String, ClientHandler> clients = new HashMap<>();
	/** Lobby Name -> Lobby */
	private final HashMap<String, Lobby> lobbies = new HashMap<>();

	/** Global UDPSource */
	private UDPSource udpSource;

	public Server()
	{

	}

	@Override
	public void run()
	{
		running = true;

		Thread udpDiscoveryServer = new Thread(this::runUdpDiscoveryServer, "UDP Discovery Server");
		udpDiscoveryServer.start();

		Thread tcpServer = new Thread(this::runTcpServer, "TCP Connection Server");
		tcpServer.start();

		try
		{
			udpSource = new UDPSource(new UDPConnection(Protocol.UDP_SERVER_PORT));
		} catch (ProtocolException e)
		{
			System.err.println("Error while opening UDP socket: " + e);
			e.printStackTrace();
			return;
		}


		createLobby("TestLobbyT100", 1, 4);
		createLobby("TestLobbyT101", 1, 4);
		createLobby("TestLobbyT102", 1, 4);
		createLobby("TestLobbyT103", 1, 4);
		createLobby("TestLobbyT104", 1, 4);
		createLobby("TestLobbyT105", 1, 4);

	}

	private void outUDP(String msg)
	{
		System.out.println("[UDP]: " + msg);
	}

	private void outTCP(String msg)
	{
		System.out.println("[TCP]: " + msg);
	}

	private void runUdpDiscoveryServer()
	{
		outUDP("[Discovery]: Listening on port " + Protocol.UDP_DISCOVERY_PORT + "...");

		UDPConnection conn;
		try
		{
			conn = new UDPConnection(Protocol.UDP_DISCOVERY_PORT);
		} catch (ProtocolException e)
		{
			// Could not open socket - exit
			throw new RuntimeException(e);
		}

		while (running)
		{
			try
			{
				DatagramPacket packet = conn.recv();
				String msg = conn.decode(packet);
				if (Protocol.isDiscoveryRequest(msg))
				{
					// Respond
					outUDP("[Discovery]: Received discovery request from " + packet.getSocketAddress());
					conn.sendString(Protocol.sendDiscoveryResponse(), packet.getSocketAddress());
					outUDP("[Discovery]: Sent discovery response to " + packet.getSocketAddress());
				} else
				{
					outUDP("[Discovery]: Unknown message received: " + msg);
				}
			} catch (ProtocolException e)
			{
				System.err.println("[UDP]: [Discovery]: Error: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	private void runTcpServer()
	{
		ServerSocket serverSocket = null;
		try
		{
			serverSocket = new ServerSocket(Protocol.TCP_SERVER_PORT);
			serverSocket.setReuseAddress(true);
		} catch (IOException e)
		{
			System.err.println("[TCP]: Error: Unable to open TCP Socket");
			e.printStackTrace();
			System.exit(1);
		}

		outTCP("[Accept]: Listening on port " + Protocol.TCP_SERVER_PORT + "...");
		while (running)
		{
			Socket sock;
			try
			{
				// Accept TCP connection
				sock = serverSocket.accept();
			} catch (IOException e)
			{
				System.err.println("Error: Unable to accept TCP Socket");
				e.printStackTrace();
				System.exit(1);
				return;
			}

			Thread t = new Thread(() -> acceptSocket(sock), "TCP Handler: " + sock.getRemoteSocketAddress());
			t.start();
		}
	}

	private void acceptSocket(Socket sock)
	{
		try
		{
			outTCP("[Accept]: " + sock.getRemoteSocketAddress() + ": Accepting client...");
			TCPConnection tcpConn = new TCPConnection(sock);

			Tuple<String, InetSocketAddress> pair = tcpConn.recvConnectionRequest();
			String name = pair.getFirst();
			InetSocketAddress address = pair.getSecond();

			outTCP("[Accept]: " + sock.getRemoteSocketAddress() + ": name='" + name + "', UDP address='" + address + "'");

			UDPRelay udpRelay = new UDPRelay(udpSource, address);

			String error = null;
			synchronized (lock)
			{
				if (clients.containsKey(name))
				{
					error = name + " is already connected.";
				} else
				{
					// Send connection success back
					tcpConn.sendConnectionResponseSuccess();

					// Then add client info, as if there is an exception when
					// sending the response,
					// this will not accept the client
					ClientInfo ci = new ClientInfo(name, tcpConn, udpRelay);
					this.onClientAccept(ci);
				}
			}

			if (error != null)
			{
				// Send error response back
				tcpConn.sendConnectionResponseReject(error);
				System.out.println("[Net]: Error while accepting client: " + error);
			}

		} catch (ProtocolException e)
		{
			outTCP("Exception while accepting client: " + e.toString());
			e.printStackTrace();
		}
	}

	private void onClientAccept(ClientInfo info)
	{
		synchronized (lock)
		{
			ClientHandler ch = new ClientHandler(info);
			ch.onError((e) -> onClientError(info.name, e));
			ch.onClose(() -> onClientClose(info.name));
			ch.onTcpMessage((msg) -> handleTcpMessage(info.name, msg));
			ch.onUdpMessage((msg) -> handleUdpMessage(info.name, msg));
			ch.start();
			this.clients.put(info.name, ch);

			// Output debug infos
			InetAddress addr = info.udpConn.getSocketAddress().getAddress();
			int udpPort = info.udpConn.getSocketAddress().getPort();
			int tcpPort = info.tcpConn.getSocket().getPort();
			System.out.println("[Net]: " + info.name + " connected. " + "(TCP: " + addr + ":" + tcpPort + " & UDP: " + addr + ":" + udpPort + ")");
		}
	}

	private void onClientError(String name, ProtocolException e)
	{
		System.err.println("[Net]: " + name + " disconnected: " + e.getMessage());
		// e.printStackTrace();
	}

	private void onClientClose(String name)
	{
		synchronized (lock)
		{
			ClientHandler ch = clients.get(name);
			if (ch != null)
			{
				// Remove client from all lobbies
				Lobby lobby = getLobby(ch);
				if (lobby != null)
					leaveLobby(ch, lobby);

				// Remove client from main list
				clients.remove(name);

				System.out.println("[Net]: " + name + " disconnected.");
			}
		}
	}

	private Lobby getLobby(ClientHandler ch)
	{
		synchronized (lock)
		{
			return getLobby(ch.getClientInfo());
		}
	}

	private Lobby getLobby(ClientInfo info)
	{
		synchronized (lock)
		{
			return lobbies.get(info.lobby);
		}
	}

	private void createLobby(String lobbyName, int minPlayers, int maxPlayers)
	{
		synchronized (lock)
		{
			lobbies.put(lobbyName, new Lobby(lobbyName, minPlayers, maxPlayers, Map.createTestMap()));
			System.out.println("[Net]: Lobby " + lobbyName + " created. (min=" + minPlayers + ", max=" + maxPlayers + ")");
		}
	}

	private void destroyLobby(Lobby lobby)
	{
		synchronized (lock)
		{
			lobbies.remove(lobby.lobbyName);
			lobby.destroy();
			System.out.println("[Net]: Lobby " + lobby.lobbyName + " destroyed.");
		}
	}

	private void joinLobby(ClientHandler handler, Lobby lobby)
	{
		synchronized (lock)
		{
			lobby.addPlayer(handler);
			handler.getClientInfo().lobby = lobby.lobbyName;
		}
	}

	private void leaveLobby(ClientHandler handler, Lobby lobby)
	{
		synchronized (lock)
		{
			lobby.removePlayer(handler.getClientInfo().name);
			handler.getClientInfo().lobby = null;
			if (lobby.size() == 0)
				destroyLobby(lobby);
		}
	}

	private void handleTcpMessage(String name, String msg)
	{
		ClientHandler handler = clients.get(name);
		ClientInfo info = handler.getClientInfo();
		if (Protocol.isLobbiesRequest(msg))
		{
			ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();
			synchronized (lock)
			{
				for (Lobby lobby : lobbies.values())
				{
					lobbyInfos.add(lobby.toLobbyInfo());
				}
			}
			try
			{
				handler.sendStringTcp(Protocol.sendLobbiesResponse(lobbyInfos));
			} catch (ProtocolException e)
			{
				// This is fine as the handler takes care of exceptions
			}
			return;
		} else if (Protocol.isLobbyCreateRequest(msg)) {
			LobbyInfo newLobby = null;
			try {
				newLobby = Protocol.parseLobbyCreateRequest(msg);
				synchronized (lock) {
					String response;
					if (!Util.isValidLobbyName(newLobby.lobbyName)) {
						// Reject
						response = Protocol.sendLobbyCreateReject("Invalid lobby name.");
					} else if (lobbies.containsKey(newLobby.lobbyName)) {
						// Reject
						response = Protocol.sendLobbyCreateReject("Lobby already exists.");
					} else {
						// Create lobby
						response = Protocol.sendLobbyCreateAccept();
						createLobby(newLobby.lobbyName, newLobby.minPlayers, newLobby.maxPlayers);
						
						// Leave current lobby
						Lobby currentLobby = getLobby(handler);
						if (currentLobby != null)
							leaveLobby(handler, currentLobby);
						
						// Join newly created lobby
						joinLobby(handler, lobbies.get(newLobby.lobbyName));
					}
					handler.sendStringTcp(response);
				}
				return;
			} catch (ProtocolException e) {
				handler.error(e);
			}
		}

		// Get lobby
		try
		{
			Lobby currentLobby = getLobby(info);
			if (currentLobby == null)
			{
				// Handle any other messages
				if (Protocol.isLobbyJoinRequest(msg))
				{
					// Handle lobby join request
					Lobby lobby;
					String errorReason = null;
					synchronized (lock)
					{
						lobby = lobbies.get(Protocol.parseLobbyJoinRequest(msg));
						if (lobby != null)
						{
							// If lobby is full, error
							if (lobby.isFull())
							{
								errorReason = "Lobby is full";
							} else
							{
								// No error - join
								joinLobby(handler, lobby);
							}
						} else
						{
							errorReason = "Lobby does not exist.";
						}
					}

					// Send accept/reject to client
					if (errorReason == null)
						handler.sendStringTcp(Protocol.sendLobbyJoinAccept());
					else
						handler.sendStringTcp(Protocol.sendLobbyJoinReject(errorReason));

				} else
				{
					System.err.println("[TCP]: Warning: Unknown message from " + name + ": " + msg);
				}
			} else
			{
				// Handle lobby messages
				if (Protocol.isLobbyLeaveRequest(msg))
				{
					// Leave lobby & notify client
					leaveLobby(handler, currentLobby);
					handler.sendStringTcp(Protocol.sendLobbyLeaveNotify());
				} else
				{
					currentLobby.handleTcpMessage(handler, msg);
				}
			}
		} catch (ProtocolException e)
		{
			handler.error(e);
		}
	}

	private void handleUdpMessage(String name, String msg)
	{
		synchronized (lock)
		{
			ClientHandler handler = clients.get(name);
			ClientInfo info = handler.getClientInfo();

			// Get lobby
			Lobby lobby = getLobby(info);

			if (lobby == null)
			{
				// Handle messages for server
				System.err.println("[UDP]: Warning: Unknown message from " + name + ": " + msg);
			} else
			{
				// Handle lobby messages
				try
				{
					lobby.handleUdpMessage(handler, msg);
				} catch (ProtocolException e)
				{
					handler.error(e);
				}
			}
		}
	}
}

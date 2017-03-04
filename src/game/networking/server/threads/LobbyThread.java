package game.networking.server.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.bind.v2.runtime.Name;

import game.networking.server.SmallGameLobby;
import game.networking.server.threads.tcp.TCPConThread;
import game.networking.server.threads.tcp.TCPConnectionStarter;
import game.networking.util.ConnectionDetails;
import game.networking.util.Protocol;
import game.networking.util.Tuple;
import game.networking.util.UtilityCode;
import game.networking.util.interfaces.IConnectionHandler;
import game.networking.util.interfaces.ITCPConnection;

public class LobbyThread implements Runnable, IConnectionHandler
{
	private DatagramSocket datagramSocket;

	// Client accept
	private Map<String, ConnectionDetails> clients;
	private LinkedList<String> acceptQueue;
	private List<String> acceptedClients;
	private LobbyConnectionThread lobbyConnection;

	// TCP
	private Map<String, Socket> clientSockets;
	private boolean updated = false;
	private List<String> updatedList;
	private Map<String, ITCPConnection> tcpConn;

	private TCPConThread tcpConTh;

	private Map<String, LinkedList<String>> sendMessages;
	private LinkedList<Tuple<String, String>> receivedMessages;

	// Game Lobbies
	private Map<String, SmallGameLobby> gameLobbies;
	private Map<String, String> userGameLobby;
	private Map<String, LinkedList<Tuple<String, String>>> udp_receivedMessages;
	private Map<String, LinkedList<Tuple<String, String>>> udp_sendMessages;

	public LobbyThread(Map<String, ConnectionDetails> _clients, List<String> _acceptedClients, LinkedList<Tuple<String, String>> _receivedMessages, Map<String, LinkedList<String>> _sendMessages)
	{
		clients = _clients;
		acceptedClients = _acceptedClients;
		sendMessages = _sendMessages;
		receivedMessages = _receivedMessages;

		udp_receivedMessages = new LinkedHashMap<>();
		udp_sendMessages = new LinkedHashMap<>();

		gameLobbies = new LinkedHashMap<>();
		userGameLobby = new LinkedHashMap<>();

		acceptQueue = new LinkedList<String>();
		clientSockets = new LinkedHashMap<>();
		updatedList = new LinkedList<String>();

		tcpConn = new LinkedHashMap<>();

		lobbyConnection = new LobbyConnectionThread(acceptQueue, this, acceptedClients);

		Thread lobbyConThread = new Thread(lobbyConnection);
		lobbyConThread.start();

		tcpConTh = new TCPConThread(clientSockets, this, updatedList);

		Thread TCPconLobby = new Thread(tcpConTh);
		TCPconLobby.start();

		// TODO: see if this is good
		int socketInt = UtilityCode.getNextAvailabePort();
		if (socketInt != -1)
		{
			try
			{
				datagramSocket = new DatagramSocket(socketInt, InetAddress.getByName("0.0.0.0"));
			} catch (SocketException | UnknownHostException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run()
	{
		boolean run = true;
		while (run)
		{
			// create TCP listener and sender for all newly accepted clients
			// FAST create UDP listener and sender for clients
			if (updated == true)
			{

				synchronized (updatedList)
				{

					for (String name : updatedList)
					{
						// Thread thread = new Thread(new
						// TCPListenerLobbyThread(clientSockets.get(string),
						// string, actions));
						// thread.start();

						// FIXME: instantiate send and receive messages maps

						sendMessages.put(name, new LinkedList<>());
						tcpConn.put(name, new TCPConnectionStarter(clientSockets.get(name), name, sendMessages.get(name), receivedMessages, this));

						System.out.println("Creadted TCPStuff for: " + name);
					}
					updatedList.clear();
				}
			}

			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		datagramSocket.close();
	}

	public void addToAcceptQueue(String clientName)
	{
		synchronized (this)
		{
			acceptQueue.add(clientName);
		}
	}

	public synchronized void sendAccept(String name)
	{
		byte[] buffer = (Protocol.StoC_DiscoveryAccept + name + Protocol.TCPSocketTag + (new Integer(getTCPServerSocket())).toString()).getBytes();
		ConnectionDetails conn = clients.get(name);
		DatagramPacket acceptPacket = new DatagramPacket(buffer, buffer.length, conn.address, conn.port);
		try
		{
			datagramSocket.send(acceptPacket);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void newClient()
	{
		updated = true;
	}

	public synchronized int getTCPServerSocket()
	{
		return tcpConTh.getTCPServerPort();
	}

	@Override
	public void TCPListenerUserDisconnect(String name)
	{
		synchronized (this)
		{
			while (clients.containsKey(name))
			{
				clients.remove(name);
			}
			while (acceptedClients.contains(name))
			{
				acceptedClients.remove(name);
			}
			while (clientSockets.containsKey(name))
			{
				clientSockets.remove(name);
			}
			while (sendMessages.containsKey(name))
			{
				sendMessages.remove(name);
			}
			while (tcpConn.containsKey(name))
			{
				tcpConn.get(name).closeConnection();
				tcpConn.remove(name);
			}
			while (userGameLobby.containsKey(name))
			{
				gameLobbies.get(userGameLobby.get(name)).dissconnectPlayer(name);
				userGameLobby.remove(name);
			}
			System.out.println("TCP disc for: " + name);
		}

	}

	@Override
	public void TCPSenderUserDisconnect(String name)
	{
		synchronized (this)
		{
			while (clients.containsKey(name))
			{
				clients.remove(name);
			}
			while (acceptedClients.contains(name))
			{
				acceptedClients.remove(name);
			}
			while (clientSockets.containsKey(name))
			{
				clientSockets.remove(name);
			}
			while (sendMessages.containsKey(name))
			{
				sendMessages.remove(name);
			}
			while (tcpConn.containsKey(name))
			{
				tcpConn.get(name).closeConnection();
				tcpConn.remove(name);
			}
			while (userGameLobby.containsKey(name))
			{
				String lobbyName = userGameLobby.get(name);
				SmallGameLobby lobbyaux = gameLobbies.get(lobbyName);
				lobbyaux.dissconnectPlayer(name);
				userGameLobby.remove(name);
				if (lobbyaux.IsEmpty())
				{
					gameLobbies.remove(lobbyName);
					System.out.println("deleted lobby: " + lobbyName);
				}
			}
			System.out.println("TCP disc for: " + name);

		}

	}

	@Override
	public synchronized void gameLobbyDissconnect(String name)
	{
		while (userGameLobby.containsKey(name))
		{
			String lobbyName = userGameLobby.get(name);
			SmallGameLobby lobbyaux = gameLobbies.get(lobbyName);
			lobbyaux.dissconnectPlayer(name);
			userGameLobby.remove(name);
			if (lobbyaux.IsEmpty())
			{
				gameLobbies.remove(lobbyName);
				System.out.println("deleted lobby: " + lobbyName);
			}
		}
		sendMessages.get(name).clear();
		tcpConn.get(name).setNewProcessor(sendMessages.get(name), receivedMessages);
	}

	public InetAddress getClientIP(String name)
	{
		synchronized (this)
		{
			return clients.get(name).address;
		}
	}

	/**
	 * 
	 * @param lobbyName
	 *            - String lobby name
	 * @param clientName
	 *            - String client name
	 * @param address
	 *            - InetAddress client address
	 * @param receiveport
	 *            - int receive port of the client
	 * @param sendport
	 *            - int send port of the client
	 */
	public void joinLobby(String lobbyName, String clientName, InetAddress address, int receiveport, int sendport)
	{
		if (gameLobbies.containsKey(lobbyName))
		{
			SmallGameLobby lobbyaux = gameLobbies.get(lobbyName);
			lobbyaux.addClient(clientName, address, receiveport, sendport);
			Tuple<LinkedList<String>, LinkedList<Tuple<String, String>>> lists = lobbyaux.getSendAndReceive(clientName);
			tcpConn.get(clientName).setNewProcessor(lists.getFirst(), lists.getSecond());
			userGameLobby.put(clientName, lobbyName);
		} else
		{
			userGameLobby.put(clientName, lobbyName);
			LinkedList<Tuple<String, String>> UDPreceivedList = new LinkedList<>();
			LinkedList<Tuple<String, String>> UDPsendList = new LinkedList<>();
			udp_receivedMessages.put(lobbyName, UDPreceivedList);
			udp_sendMessages.put(lobbyName, UDPsendList);
			SmallGameLobby smallGameLobbyAux = new SmallGameLobby(lobbyName, UDPreceivedList, UDPsendList, this);
			gameLobbies.put(lobbyName, smallGameLobbyAux);
			smallGameLobbyAux.addClient(clientName, address, receiveport, sendport);
			int receive = smallGameLobbyAux.getReceivePort();
			int send = smallGameLobbyAux.getSendPort();

			Tuple<LinkedList<String>, LinkedList<Tuple<String, String>>> lists = smallGameLobbyAux.getSendAndReceive(clientName);
			tcpConn.get(clientName).setNewProcessor(lists.getFirst(), lists.getSecond());

			synchronized (this)
			{
				lists.getFirst().add("[UDPS]" + send + "[UDPR]" + receive);
			}
			smallGameLobbyAux.StartGame();
		}
	}

}

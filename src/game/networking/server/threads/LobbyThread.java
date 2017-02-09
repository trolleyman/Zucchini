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

import game.networking.server.threads.tcp.TCPConThread;
import game.networking.server.threads.tcp.TCP_Connection;
import game.networking.util.ConnectionDetails;
import game.networking.util.Protocol;
import game.networking.util.Tuple;
import game.networking.util.UtilityCode;

public class LobbyThread implements Runnable
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

	private TCPConThread tcpConTh;

	private Map<String, LinkedList<String>> sendMessages;
	private LinkedList<Tuple<String, String>> receivedMessages;

	public LobbyThread(Map<String, ConnectionDetails> _clients, List<String> _acceptedClients, LinkedList<Tuple<String, String>> _receivedMessages, Map<String, LinkedList<String>> _sendMessages)
	{
		clients = _clients;
		acceptedClients = _acceptedClients;
		sendMessages = _sendMessages;
		receivedMessages = _receivedMessages;

		acceptQueue = new LinkedList<String>();
		clientSockets = new LinkedHashMap<>();
		updatedList = new LinkedList<String>();

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
						new TCP_Connection(clientSockets.get(name), name, sendMessages.get(name), receivedMessages);

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

}

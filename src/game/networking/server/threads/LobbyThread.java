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
import game.networking.server.threads.tcp.TCPListenerLobbyThread;
import game.networking.server.threads.tcp.TCP_Connection;
import game.networking.util.ConnectionDetails;
import game.networking.util.Protocol;
import game.networking.util.Tuple;

public class LobbyThread implements Runnable
{

	private int socketInt;
	private DatagramSocket datagramSocket;

	// Client accept
	private Map<String, ConnectionDetails> clients;
	private LinkedList<String> acceptQueue;
	private List<String> acceptedClients;
	private LobbyConnectionThread lobbyConnection;
	private Map<String, Socket> clientSockets;
	private boolean updated = false;
	private List<String> updatedList;

	private TCPConThread tcpConTh;

	private Map<String, LinkedList<Tuple<String, String>>> sendMessages;
	private Map<String, LinkedList<Tuple<String, String>>> receivedMessages;

	public LobbyThread(int _socketInt, Map<String, ConnectionDetails> _clients, List<String> _acceptedClients)
	{
		socketInt = _socketInt;
		clients = _clients;
		acceptedClients = _acceptedClients;

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
		try
		{
			datagramSocket = new DatagramSocket(socketInt, InetAddress.getByName("0.0.0.0"));
		} catch (SocketException | UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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

					for (String string : updatedList)
					{
						// Thread thread = new Thread(new
						// TCPListenerLobbyThread(clientSockets.get(string),
						// string, actions));
						// thread.start();
						new TCP_Connection(clientSockets.get(string), string, sendMessages.get(string), receivedMessages.get(string));

						System.out.println("Creadted TCPStuff for: " + string);
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

	public synchronized Map<String, LinkedList<Tuple<String, String>>> getSendMessage()
	{
		return sendMessages;
	}

	public synchronized Map<String, LinkedList<Tuple<String, String>>> getReceivedMessage()
	{
		return receivedMessages;
	}

	public synchronized int getTCPServerSocket()
	{
		return tcpConTh.getTCPServerPort();
	}

}

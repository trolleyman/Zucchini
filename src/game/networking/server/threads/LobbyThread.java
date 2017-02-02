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

import game.networking.util.Connection;
import game.networking.util.Protocol;

public class LobbyThread implements Runnable
{

	private Map<String, Connection> clients;
	private int socketInt;
	private LinkedList<String> acceptQueue;
	private LobbyConnectionThread lobbyConnection;
	private List<String> acceptedClients;

	private Map<String, Socket> clientSockets;
	private Map<String, String> messages;

	private DatagramSocket datagramSocket;

	private boolean updated = false;
	private List<String> updatedList;

	public LobbyThread(int _socketInt, Map<String, Connection> _clients, List<String> _acceptedClients)
	{
		socketInt = _socketInt;
		clients = _clients;
		acceptedClients = _acceptedClients;

		acceptQueue = new LinkedList<String>();

		lobbyConnection = new LobbyConnectionThread(acceptQueue, this, acceptedClients);

		clientSockets = new LinkedHashMap<>();

		updatedList = new LinkedList<String>();

		messages = new LinkedHashMap<>();

		Thread lobbyConThread = new Thread(lobbyConnection);
		lobbyConThread.start();

		Thread TCPconLobby = new Thread(new TCPConLobbyThread(_socketInt, clientSockets, this, updatedList));
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
			if (updated == true)
			{

				synchronized (updatedList)
				{

					for (String string : updatedList)
					{
						Thread thread = new Thread(new TCPListenerLobbyThread(clientSockets.get(string), string, messages));
						thread.start();
					}
					updatedList = new LinkedList<>();
				}
			}

			// FIXME: messages do not work as map does not have a next field

			try
			{
				Thread.sleep(10);
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
		byte[] buffer = (Protocol.StoC_DiscoveryAccept + name).getBytes();
		Connection conn = clients.get(name);
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

	public LinkedList<String> getQueue()
	{
		return acceptQueue;
	}

	public void newClient()
	{
		updated = true;
	}

}

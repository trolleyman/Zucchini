package game.networking.server;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.sun.org.apache.xalan.internal.xslt.Process;

import game.networking.server.threads.udp.UDP_Connection;
import game.networking.test.Processor;
import game.networking.util.ConnectionDetails;
import game.networking.util.Tuple;

public class SmallGameLobby
{
	private UDP_Connection connection;
	private String name;
	private Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> clients;
	private boolean isEmpty;

	private Map<String, LinkedList<String>> sendMessages;
	private LinkedList<Tuple<String, String>> receivedMessages;

	public SmallGameLobby(String _name, LinkedList<Tuple<String, String>> _receivedActions, LinkedList<Tuple<String, String>> _sendActions)
	{
		name = _name;
		clients = new LinkedHashMap<>();
		connection = new UDP_Connection(clients, _receivedActions, _sendActions);

		sendMessages = new LinkedHashMap<>();
		receivedMessages = new LinkedList<>();

		Processor processor = new Processor(sendMessages, receivedMessages);

	}

	public void StartGame()
	{
		connection.StartGame();
	}

	public synchronized String getName()
	{
		return name;
	}

	public synchronized int getReceivePort()
	{
		return connection.getReceiverPort();
	}

	public synchronized int getSendPort()
	{
		return connection.getSenderPort();
	}

	public synchronized void addClient(String name, InetAddress address, int receiveport, int sendport)
	{
		clients.put(name, new Tuple<>(address, new Tuple<>(receiveport, sendport)));
		sendMessages.put(name, new LinkedList<>());
		isEmpty = false;
	}

	@Override
	public boolean equals(Object obj)
	{
		SmallGameLobby gameLobby = (SmallGameLobby) obj;
		return name.equals(gameLobby.getName());
	}

	public synchronized Tuple<LinkedList<String>, LinkedList<Tuple<String, String>>> getSendAndReceive(String name)
	{
		return (new Tuple<LinkedList<String>, LinkedList<Tuple<String, String>>>(sendMessages.get(name), receivedMessages));
	}

	public synchronized void dissconnectPlayer(String name)
	{
		clients.remove(name);
		System.out.println("UDP disc for: " + name);
		if (clients.size() == 0)
		{
			connection.StopGame();
			isEmpty = true;
		}
	}

	public synchronized boolean IsEmpty()
	{
		return isEmpty;
	}

}

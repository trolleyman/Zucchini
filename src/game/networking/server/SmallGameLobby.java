package game.networking.server;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import game.networking.server.threads.udp.UDP_Connection;
import game.networking.util.ConnectionDetails;
import game.networking.util.Tuple;

public class SmallGameLobby
{
	private UDP_Connection connection;
	private String name;
	private Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> clients;

	public SmallGameLobby(String _name, LinkedList<Tuple<String, String>> _receivedActions, LinkedList<Tuple<String, String>> _sendActions)
	{
		name = _name;
		clients = new LinkedHashMap<>();
		connection = new UDP_Connection(clients, _receivedActions, _sendActions);
	}

	public void StartConnections()
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
	}

	@Override
	public boolean equals(Object obj)
	{
		SmallGameLobby gameLobby = (SmallGameLobby) obj;
		return name.equals(gameLobby.getName());
	}

}

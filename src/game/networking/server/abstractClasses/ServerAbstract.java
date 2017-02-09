package game.networking.server.abstractClasses;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import game.networking.server.threads.DiscoveryThread;
import game.networking.server.threads.LobbyThread;
import game.networking.util.ConnectionDetails;
import game.networking.util.ServerMainable;
import game.networking.util.Tuple;

public abstract class ServerAbstract implements ServerMainable
{

	// private Thread discoveryThread;
	private LobbyThread lobby;
	private Map<String, ConnectionDetails> clients;
	private List<String> acceptedClients;
	private Map<String, LinkedList<Tuple<String, String>>> receivedMess;
	private Map<String, LinkedList<Tuple<String, String>>> sendMess;

	public ServerAbstract()
	{
		clients = new LinkedHashMap<String, ConnectionDetails>();
		acceptedClients = new LinkedList<String>();
		receivedMess = new LinkedHashMap<>();
		sendMess = new LinkedHashMap<>();
	}

	public void startServer()
	{
		Thread discoveryThread = null;
		Thread lobbyThread = null;

		if (lobby == null)
		{
			lobby = new LobbyThread(clients, acceptedClients, receivedMess, sendMess);
			lobbyThread = new Thread(lobby);
			lobbyThread.start();
		}

		if (discoveryThread == null)
		{
			discoveryThread = new Thread(new DiscoveryThread(clients, this, acceptedClients));
			discoveryThread.start();

		}
	}

	@Override
	public void acceptClientConnection(String ClientName)
	{
		lobby.addToAcceptQueue(ClientName);

	}

	public synchronized Map<String, LinkedList<Tuple<String, String>>> getSendMess()
	{
		return sendMess;
	}

	public synchronized Map<String, LinkedList<Tuple<String, String>>> getReceivedMess()
	{
		return receivedMess;
	}

}

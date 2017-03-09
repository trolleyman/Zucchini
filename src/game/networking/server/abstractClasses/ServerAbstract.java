package game.networking.server.abstractClasses;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import game.networking.server.threads.DiscoveryThread;
import game.networking.server.threads.LobbyThread;
import game.networking.util.ConnectionDetails;
import game.networking.util.Tuple;
import game.networking.util.interfaces.IServerMainable;

public abstract class ServerAbstract implements IServerMainable
{

	// private Thread discoveryThread;
	protected LobbyThread lobby;
	private Map<String, ConnectionDetails> clients;
	private List<String> acceptedClients;
	private LinkedList<Tuple<String, String>> receivedMess;
	private Map<String, LinkedList<String>> sendMess;

	public ServerAbstract()
	{
		clients = new LinkedHashMap<String, ConnectionDetails>();
		acceptedClients = new LinkedList<String>();
		receivedMess = new LinkedList<>();
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

	public synchronized Map<String, LinkedList<String>> getSendMess()
	{
		return sendMess;
	}

	public synchronized LinkedList<Tuple<String, String>> getReceivedMess()
	{
		return receivedMess;
	}

}

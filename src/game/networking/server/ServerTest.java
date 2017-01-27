package networking.server;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import networking.server.threads.DiscoveryThread;
import networking.server.threads.LobbyThread;
import networking.util.Connection;
import networking.util.ServerMainable;

public class ServerTest implements ServerMainable
{
	private int socket;
	// private Thread discoveryThread;
	private LobbyThread lobby;
	private Map<String, Connection> clients;
	private List<String> acceptedClients;

	public ServerTest(int _socket)
	{
		socket = _socket;
		clients = new LinkedHashMap<String, Connection>();
		acceptedClients = new LinkedList<String>();
	}

	public void startServer()
	{
		// TODO: see if these need to be members
		Thread discoveryThread = null;
		Thread lobbyThread = null;

		if (lobby == null)
		{
			lobby = new LobbyThread(socket + 1, clients, acceptedClients);
			lobbyThread = new Thread(lobby);
			lobbyThread.start();
		}

		if (discoveryThread == null)
		{
			discoveryThread = new Thread(new DiscoveryThread(socket, clients, this, acceptedClients));
			discoveryThread.start();

		}
	}

	@Override
	public void acceptClientConnection(String ClientName)
	{
		lobby.addToAcceptQueue(ClientName);

	}

	@Override
	public LinkedList<String> getAccQueue()
	{
		return lobby.getQueue();
	}

}

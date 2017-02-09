package game.networking.server;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import game.networking.server.abstractClasses.ServerAbstract;
import game.networking.server.threads.DiscoveryThread;
import game.networking.server.threads.LobbyThread;
import game.networking.util.ConnectionDetails;
import game.networking.util.ServerMainable;
import game.networking.util.Tuple;

public class ServerTest extends ServerAbstract
{
	// // private Thread discoveryThread;
	// private LobbyThread lobby;
	// private Map<String, ConnectionDetails> clients;
	// private List<String> acceptedClients;
	//
	// public ServerTest()
	// {
	// clients = new LinkedHashMap<String, ConnectionDetails>();
	// acceptedClients = new LinkedList<String>();
	// }
	//
	// public void startServer()
	// {
	// // TODO: see if these need to be members
	// Thread discoveryThread = null;
	// Thread lobbyThread = null;
	//
	// if (lobby == null)
	// {
	// lobby = new LobbyThread(clients, acceptedClients, null, null);
	// lobbyThread = new Thread(lobby);
	// lobbyThread.start();
	// }
	//
	// if (discoveryThread == null)
	// {
	// discoveryThread = new Thread(new DiscoveryThread(clients, this,
	// acceptedClients));
	// discoveryThread.start();
	//
	// }
	// }
	//
	// @Override
	// public void acceptClientConnection(String ClientName)
	// {
	// lobby.addToAcceptQueue(ClientName);
	//
	// }

}

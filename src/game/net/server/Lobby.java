package game.net.server;

import game.LobbyInfo;
import game.PlayerInfo;
import game.Util;
import game.action.Action;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.world.EntityBank;
import game.world.ServerWorld;
import game.world.Team;
import game.world.map.Map;

import java.util.ArrayList;

public class Lobby {
	public String lobbyName;
	public int maxPlayers;
	private final Object clientsLock = new Object();
	private ArrayList<LobbyClient> clients;
	
	private Map map;
	
	private ServerWorld world;
	
	private boolean running;
	
	public Lobby(String lobbyName, int maxPlayers, Map map) {
		this.lobbyName = lobbyName;
		this.maxPlayers = maxPlayers;
		this.clients = new ArrayList<>();
		
		this.map = map;
		
		this.world = null;
		
		Thread t = new Thread(this::runLobbyHandler, "Lobby Handler: " + lobbyName);
		t.start();
	}
	
	private void runLobbyHandler() {
		this.running = true;
		
		while (running) {
			// Send lobby info to all players
			LobbyInfo info = toLobbyInfo();
			
			synchronized (clientsLock) {
				for (LobbyClient c : clients) {
					try {
						c.handler.sendStringTcp(Protocol.sendLobbyUpdate(info));
					} catch (ProtocolException e) {
						// This is ok, as the ClientHandler will take care if there is an exception
					}
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// whatever
			}
		}
	}
	
	private void runWorld() {
		this.running = true;
		
		world = new ServerWorld(map, new EntityBank());
		synchronized (clientsLock) {
			for (LobbyClient c : clients)
				world.addClient(c);
		}
		
		long prevTime = System.nanoTime();
		while (this.running) {
			long now = System.nanoTime();
			long dtNanos = now - prevTime;
			prevTime = now;
			double dt = (double)dtNanos / Util.NANOS_PER_SECOND;
			
			world.update(dt);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				System.err.println("Warning: Sleep Thread Interrupted: " + e.toString());
			}
		}
	}
	
	/**
	 * Adds a player to the lobby.
	 * @param ch The player
	 * @return The team ID of the player.
	 */
	public int addPlayer(ClientHandler ch) {
		synchronized (clientsLock) {
			int team = Team.START_FREE_TEAM + clients.size();
			LobbyClient c = new LobbyClient(ch, team);
			this.clients.add(c);
			return team;
		}
	}
	
	/**
	 * Sets whether a player is ready or not
	 * @param name The name of the player
	 * @param ready If the player is ready
	 */
	public void setPlayerReady(String name, boolean ready) {
		synchronized (clientsLock) {
			for (LobbyClient c : clients) {
				if (c.handler.getClientInfo().name.equals(name)) {
					c.ready = ready;
					break;
				}
			}
		}
	}
	
	/**
	 * Removes a player from the lobby
	 * @param name The name of the player
	 */
	public void removePlayer(String name) {
		synchronized (clientsLock) {
			this.clients.removeIf((lc) -> lc.handler.getClientInfo().name.equals(name));
		}
	}
	
	public boolean isFull() {
		synchronized (clientsLock) {
			return clients.size() == maxPlayers;
		}
	}
	
	public LobbyInfo toLobbyInfo() {
		synchronized (clientsLock) {
			// Get player infos
			PlayerInfo[] infoPlayers = new PlayerInfo[clients.size()];
			for (int i = 0; i < clients.size(); i++) {
				LobbyClient client = clients.get(i);
				String playerName = client.handler.getClientInfo().name;
				int team = client.team;
				boolean ready = client.ready;
				infoPlayers[i] = new PlayerInfo(playerName, team, ready);
			}
			
			return new LobbyInfo(lobbyName, maxPlayers, infoPlayers);
		}
	}
	
	public void handleTcpMessage(ClientHandler handler, String msg) throws ProtocolException {
		if (Protocol.isAction(msg)) {
			Action a = Protocol.parseAction(msg);
			if (world != null) {
				world.handleAction(handler.getClientInfo().name, a);
			}
		} else if (Protocol.isFullUpdateRequest(msg)) {
			if (world != null) {
				world.handleFullUpdateRequest(handler.getClientInfo().name);
			}
		} else {
			System.err.println("[TCP]: Warning: Unknown message from " + handler.getClientInfo().name + ": " + msg);
		}
	}
	
	public void handleUdpMessage(ClientHandler handler, String msg) throws ProtocolException {
		// Currently no UDP messages
		System.err.println("[UDP]: Warning: Unknown message from " + handler.getClientInfo().name + ": " + msg);
	}
}

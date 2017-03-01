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
import game.world.update.EntityUpdate;

import java.util.ArrayList;

public class Lobby {
	public String lobbyName;
	public int maxPlayers;
	public int minPlayers;
	public double countdownTime = -1.0f;
	
	private final Object clientsLock = new Object();
	private ArrayList<LobbyClient> clients;
	
	private Map map;
	
	private ServerWorld world;
	
	private Thread lobbyHandler;
	private boolean running;
	
	private boolean countingDown = false;
	
	public Lobby(String lobbyName, int minPlayers, int maxPlayers, Map map) {
		this.lobbyName = lobbyName;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.clients = new ArrayList<>();
		
		this.map = map;
		
		this.world = null;
		
		lobbyHandler = new Thread(this::runLobbyHandler, "Lobby Handler: " + lobbyName);
		lobbyHandler.start();
	}
	
	private boolean shouldCountdown() {
		synchronized (clientsLock) {
			// Check if all clients are ready
			for (LobbyClient c : clients)
				if (!c.ready)
					return false;
			
			return clients.size() >= minPlayers;
		}
	}
	
	private void runLobbyHandler() {
		this.running = true;
		
		long prevTime = System.nanoTime();
		while (running) {
			synchronized (clientsLock) {
				if (this.countingDown) {
					// Check that all clients are still ready
					if (!shouldCountdown()) {
						this.countingDown = false;
						this.countdownTime = -1.0f;
					} else {
						// Count down
						long now = System.nanoTime();
						long dtNanos = now - prevTime;
						prevTime = now;
						double dt = dtNanos / (double)Util.NANOS_PER_SECOND;
						countdownTime -= dt;
					}
				} else {
					if (shouldCountdown()) {
						// Start countdown
						prevTime = System.nanoTime();
						this.countingDown = true;
						countdownTime = 5.0;
					}
				}
				
				// Send lobby info to all players
				LobbyInfo info = toLobbyInfo();
				
				for (LobbyClient c : clients) {
					try {
						c.handler.sendStringTcp(Protocol.sendLobbyUpdate(info));
					} catch (ProtocolException e) {
						// This is ok, as the ClientHandler will take care if there is an exception
					}
				}
			}
			
			// If countdown is up, then run the world and return.
			if (this.countingDown && this.countdownTime <= 0.0f) {
				this.runWorld();
				running = false;
				return;
			}
			
			try {
				if (this.countingDown)
					Thread.sleep(200);
				else
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
			System.out.println("[Net]: " + ch.getClientInfo().name + " joined " + lobbyName + ".");
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
			for (int i = 0; i < clients.size(); i++) {
				LobbyClient c = clients.get(i);
				if (c.handler.getClientInfo().name.equals(name)) {
					// Remove from world
					if (this.world != null)
						this.world.removeClient(name);
					
					// Remove from list
					clients.remove(i);
					System.out.println("[Net]: " + name + " left " + lobbyName + ".");
					break;
				}
			}
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
			
			if (!countingDown)
				return new LobbyInfo(lobbyName, minPlayers, maxPlayers, -1.0f, infoPlayers);
			else
				return new LobbyInfo(lobbyName, minPlayers, maxPlayers, Math.max(0.0, countdownTime), infoPlayers);
		}
	}
	
	public void handleTcpMessage(ClientHandler handler, String msg) throws ProtocolException {
		if (Protocol.isFullUpdateRequest(msg)) {
			if (world != null) {
				world.handleFullUpdateRequest(handler.getClientInfo().name);
			}
		} else if (Protocol.isReadyToggle(msg)) {
			// Toggle ready status of handler
			synchronized (clientsLock) {
				for (LobbyClient c : clients) {
					if (c.handler.getClientInfo().name.equals(handler.getClientInfo().name)) {
						c.ready = !c.ready;
					}
				}
			}
			// Interrupt handler so that it sends a new update to clients
			lobbyHandler.interrupt();
		} else {
			System.err.println("[TCP]: Warning: Unknown message from " + handler.getClientInfo().name + ": " + msg);
		}
	}
	
	public void handleUdpMessage(ClientHandler handler, String msg) throws ProtocolException {
		if (Protocol.isAction(msg)) {
			// Actions are only send via UDP
			Action a = Protocol.parseAction(msg);
			if (world != null) {
				world.handleAction(handler.getClientInfo().name, a);
			}
		} else {
			System.err.println("[UDP]: Warning: Unknown message from " + handler.getClientInfo().name + ": " + msg);
		}
	}
}

package game.net.server;

import game.LobbyInfo;
import game.PlayerInfo;
import game.Util;
import game.action.Action;
import game.ai.AIPlayer;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.world.EntityBank;
import game.world.ServerWorld;
import game.world.Team;
import game.world.entity.Player;
import game.world.entity.monster.Zombie;
import game.world.entity.weapon.MachineGun;
import game.world.map.Map;
import org.joml.Vector2f;

import java.util.ArrayList;

public class Lobby {
	public String lobbyName;
	public int maxPlayers;
	public int minPlayers;
	public double countdownTime = -1.0f;
	public boolean isClosed = false;
	
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
		
		lobbyHandler = new Thread(this::run, "Lobby Handler: " + lobbyName);
		lobbyHandler.start();
	}
	
	private void lobbyOut(String msg) {
		System.out.println("[Lobby]: [" + lobbyName + "]: " + msg);
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
	
	private void run() {
		this.running = true;
		while (true) {
			if (!running) break;
			runLobbyHandler();
			if (!running) break;
			runWorld();
		}
		lobbyOut("Finished running lobby.");
	}
	
	private void runLobbyHandler() {
		lobbyOut("Started running lobby handler.");
		synchronized (clientsLock) {
			for (LobbyClient c : clients) {
				c.ready = false; // Reset for when lobby starts again
			}
		}
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
						countdownTime = Util.LOBBY_WAIT_SECS;
					}
				}
				
				// Send lobby info to all players
				LobbyInfo info = toLobbyInfo();
				
				for (LobbyClient c : clients) {
					try {
						c.handler.sendStringTcp(Protocol.sendLobbyUpdate(info));
					} catch (ProtocolException e) {
						c.handler.error(e);
					}
				}
			}
			
			// If countdown is up, then run the world.
			if (this.countingDown && this.countdownTime <= 0.0f) {
				lobbyOut("Finished running the lobby handler.");
				return;
			}
			
			try {
				if (this.countingDown)
					Thread.sleep(200);
				else
					Thread.sleep(500);
			} catch (InterruptedException e) {
				// This is fine as this is expected
			}
		}
	}
	
	private void runWorld() {
		lobbyOut("Started running the world.");
		if (world != null) {
			// Destroy previous world
			synchronized (clientsLock) {
				for (LobbyClient c : clients) {
					world.removeClient(c.handler.getClientInfo().name);
				}
			}
		}
		
		world = new ServerWorld(map, new EntityBank());
		synchronized (clientsLock) {
			isClosed = true;
			for (LobbyClient c : clients)
				world.addClient(c);
			
			int nClients = clients.size();
			int minPlayers = map.getMinPlayers();
			int numAI = minPlayers - nClients;
			if (numAI > 0) {
				for (int i = 0; i < numAI; i++) {
					int team = Team.FIRST_PLAYER_TEAM + clients.size() + i;
					Vector2f position = map.getSpawnLocation(team);
					// TODO: Generate names
					String name = "[BOT] " + (i + 1);
					world.addAI(new AIPlayer(team, new Vector2f(position), name,new MachineGun(new Vector2f(0.0f, 0.0f),256) ));
				}
			}
			
		}
		
		long prevTime = System.nanoTime();
		while (this.running) {
			long now = System.nanoTime();
			long dtNanos = now - prevTime;
			prevTime = now;
			double dt = (double)dtNanos / Util.NANOS_PER_SECOND;
			
			world.update(dt);
			
			if (world.isFinished()) {
				lobbyOut("Finished running the world.");
				return;
			}
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// This is fine
				//System.err.println("Warning: Sleep Thread Interrupted: " + e.toString());
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
			lobbyOut(ch.getClientInfo().name + " joined.");
			int team = Team.FIRST_PLAYER_TEAM + clients.size();
			LobbyClient c = new LobbyClient(ch, team);
			this.clients.add(c);
			return team;
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
					lobbyOut(name + " left.");
					break;
				}
			}
			regenerateTeams();
		}
	}
	
	/**
	 * Regenerate the teams so that they are consecutive
	 */
	private void regenerateTeams() {
		synchronized (clientsLock) {
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).team = Team.FIRST_PLAYER_TEAM + i;
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
						if (c.ready) lobbyOut(handler.getClientInfo().name + " readyed.");
						else lobbyOut(handler.getClientInfo().name + " unreadyed.");
					}
				}
			}
			// Interrupt handler so that it sends a new update to clients
			lobbyHandler.interrupt();
		} else if (Protocol.isMessageToServer(msg)) {
			String msgToServer = Protocol.parseMessageToServer(msg);
			String name = handler.getClientInfo().name;
			synchronized (clientsLock) {
				for (LobbyClient c : clients) {
					c.handler.sendStringTcp(Protocol.sendMessageToClient(name, msgToServer));
				}
			}
		} else if (Protocol.isAction(msg)) {
			// Actions can be sent via TCP
			Action a = Protocol.parseAction(msg);
			if (world != null) {
				world.handleAction(handler.getClientInfo().name, a);
			}
		} else {
			System.err.println("[TCP]: Warning: Unknown message from " + handler.getClientInfo().name + ": " + msg);
		}
	}
	
	public void handleUdpMessage(ClientHandler handler, String msg) throws ProtocolException {
		if (Protocol.isAction(msg)) {
			// Actions can be sent via UDP
			Action a = Protocol.parseAction(msg);
			if (world != null) {
				world.handleAction(handler.getClientInfo().name, a);
			}
		} else {
			System.err.println("[UDP]: Warning: Unknown message from " + handler.getClientInfo().name + ": " + msg);
		}
	}
	
	/**
	 * Returns the number of players currently in the lobby
	 */
	public int size() {
		synchronized (clientsLock) {
			return clients.size();
		}
	}
	
	/**
	 * Destroys the lobby
	 */
	public void destroy() {
		running = false;
		lobbyHandler.interrupt();
	}
}

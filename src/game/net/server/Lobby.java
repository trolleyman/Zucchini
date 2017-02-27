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
	private int nextTeamID = Team.START_FREE_TEAM;
	
	public String lobbyName;
	public int maxPlayers;
	private final Object playersLock = new Object();
	private ArrayList<ClientHandler> players;
	
	private Map map;
	
	private ServerWorld world;
	
	private boolean running;
	
	public Lobby(String lobbyName, int maxPlayers, Map map) {
		this.lobbyName = lobbyName;
		this.maxPlayers = maxPlayers;
		this.players = new ArrayList<>();
		
		this.map = map;
		
		this.world = null;
	}
	
	public void runWorld() {
		this.running = true;
		
		world = new ServerWorld(map, new EntityBank());
		synchronized (playersLock) {
			for (ClientHandler c : players)
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
		synchronized (playersLock) {
			int team = nextTeamID++;
			ch.getClientInfo().team = team;
			this.players.add(ch);
			return team;
		}
	}
	
	/**
	 * Removes a player from the lobby
	 * @param name The name of the player
	 */
	public void removePlayer(String name) {
		synchronized (playersLock) {
			this.players.removeIf((ch) -> ch.getClientInfo().name.equals(name));
		}
	}
	
	public LobbyInfo toLobbyInfo() {
		synchronized (playersLock) {
			// Get player infos
			PlayerInfo[] infoPlayers = new PlayerInfo[players.size()];
			for (int i = 0; i < players.size(); i++) {
				String playerName = players.get(i).getClientInfo().name;
				int team = players.get(i).getClientInfo().team;
				infoPlayers[i] = new PlayerInfo(playerName, team);
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

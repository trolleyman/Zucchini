package game.net.server;

import game.Util;
import game.world.Team;
import game.world.map.Map;

import java.util.ArrayList;

public class Lobby {
	private int nextTeamID = Team.START_FREE_TEAM;
	
	public String name;
	public int maxPlayers;
	private ArrayList<ClientInfo> players;
	
	private Map map;
	
	public Lobby(String name, int maxPlayers, Map map) {
		this.name = name;
		this.maxPlayers = maxPlayers;
		this.players = new ArrayList<>();
		
		this.map = map;
	}
	
	public void run() {
		this.running = true;
		
		long prevTime = System.nanoTime();
		while (this.running) {
			long now = System.nanoTime();
			long dtNanos = now - prevTime;
			prevTime = now;
			double dt = (double)dtNanos / Util.NANOS_PER_SECOND;
			
			this.world.update(dt);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				System.err.println("Warning: Sleep Thread Interrupted: " + e.toString());
			}
		}
	}
	
	/**
	 * Adds a player to the lobby.
	 * @param info The player
	 * @return The team ID of the player.
	 */
	public int addPlayer(ClientInfo info) {
		int team = nextTeamID++;
		info.team = team;
		this.players.add(info);
		return team;
	}
	
	/**
	 * Removes a player from the lobby
	 * @param name The name of the player
	 */
	public void removePlayer(String name) {
		this.players.removeIf((info) -> info.name.equals(name));
	}
	
	public ArrayList<ClientInfo> getPlayers() {
		return this.players;
	}
}

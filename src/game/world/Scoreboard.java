package game.world;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Keeps track of the current scores for each of the teams, and each of the players
 */
public class Scoreboard {
	public HashMap<String, PlayerScoreboardInfo> players;
	public transient boolean dirty;
	
	public Scoreboard() {
		players = new HashMap<>();
		dirty = true;
	}
	
	public void update(double dt) {
		for (PlayerScoreboardInfo p : players.values()) {
			p.survivalTime += dt;
		}
	}
	
	public void addPlayer(String name) {
		players.put(name, new PlayerScoreboardInfo(name));
		dirty = true;
	}
	
	public void removePlayer(String name) {
		players.remove(name);
		dirty = true;
	}
	
	public void killPlayer(String name) {
		players.get(name).dead = true;
		dirty = true;
	}
	
	public void addMonsterKill(String name) {
		players.get(name).monsterKills++;
		dirty = true;
	}
	
	public void addPlayerKill(String name) {
		players.get(name).playerKills++;
		dirty = true;
	}
	
	public void addPlayerSuicide(String name) {
		players.get(name).playerKills--;
		dirty = true;
	}
	
	@Override
	public String toString() {
		return "[" + String.join(", ", players.values().stream().map(Object::toString).collect(Collectors.toList())) + "]";
	}
}

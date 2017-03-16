package game.world;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Keeps track of the current scores for each of the teams, and each of the players
 */
public class Scoreboard {
	private ArrayList<PlayerScoreboardInfo> players;
	public transient boolean dirty;
	private transient boolean toSort;
	
	public Scoreboard() {
		players = new ArrayList<>();
		dirty = true;
		toSort = false;
	}
	
	private void sortPlayers() {
		players.sort((l, r) -> {
			if (l.dead && r.dead && r.survivalTime != l.survivalTime)
				return Double.compare(r.survivalTime, l.survivalTime);
			else if (l.dead != r.dead)
				return Boolean.compare(l.dead, r.dead);
			if (l.playerKills != r.playerKills)
				return Integer.compare(r.playerKills, l.playerKills);
			if (l.monsterKills != r.monsterKills)
				return Integer.compare(r.monsterKills, l.monsterKills);
			return l.name.compareTo(r.name);
		});
	}
	
	public ArrayList<PlayerScoreboardInfo> getPlayers() {
		if (toSort) {
			sortPlayers();
			toSort = false;
		}
		return players;
	}
	
	public void update(double dt) {
		for (PlayerScoreboardInfo p : players) {
			if (!p.dead)
				p.survivalTime += dt;
		}
		toSort = true;
	}
	
	private void setPlayer(PlayerScoreboardInfo info) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).name.equals(info.name)) {
				players.set(i, info);
				toSort = true;
				return;
			}
		}
		players.add(info);
		toSort = true;
	}
	
	public PlayerScoreboardInfo getPlayer(String name) {
		for (PlayerScoreboardInfo p : players) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	private PlayerScoreboardInfo getPlayerOrDefault(String name) {
		PlayerScoreboardInfo p = getPlayer(name);
		if (p != null)
			return p;
		p = new PlayerScoreboardInfo(name);
		players.add(p);
		toSort = true;
		return p;
	}
	
	public void addPlayer(String name) {
		setPlayer(new PlayerScoreboardInfo(name));
		dirty = true;
	}
	
	public void removePlayer(String name) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).name.equals(name)) {
				players.remove(i);
				dirty = true;
				return;
			}
		}
	}
	
	public void killPlayer(String name) {
		getPlayerOrDefault(name).dead = true;
		toSort = true;
		dirty = true;
	}
	
	public void addMonsterKill(String name) {
		getPlayerOrDefault(name).monsterKills++;
		toSort = true;
		dirty = true;
	}
	
	public void addPlayerKill(String name) {
		getPlayerOrDefault(name).playerKills++;
		toSort = true;
		dirty = true;
	}
	
	public void addPlayerSuicide(String name) {
		getPlayerOrDefault(name).playerKills--;
		toSort = true;
		dirty = true;
	}
	
	@Override
	public String toString() {
		return "[" + String.join(", ", getPlayers().stream().map(Object::toString).collect(Collectors.toList())) + "]";
	}
}

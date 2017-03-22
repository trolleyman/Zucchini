package game.world;

import game.world.entity.damage.Damage;
import game.world.update.ScoreboardWorldUpdate;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Keeps track of the current scores for each of the teams, and each of the players
 */
public class Scoreboard {
	private ArrayList<PlayerScoreboardInfo> players;
	public transient boolean dirty;
	private transient boolean toSort;

	/**
	 * Constructs a scoreboard
	 */
	public Scoreboard() {
		players = new ArrayList<>();
		dirty = true;
		toSort = false;
	}

	/**
	 * Sorts the players on the scoreboard based on survival time, whether they are dead,
	 * player kills, monster kills and name (alphabetical order)
	 */
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

	/**
	 * Gets the players in the scoreboard, sorting if necessary
	 * @return The array list of players
	 */
	public ArrayList<PlayerScoreboardInfo> getPlayers() {
		if (toSort) {
			sortPlayers();
			toSort = false;
		}
		return players;
	}

	/**
	 * Update method for the scoreboard
	 * @param dt The time since th last update
	 */
	public void update(double dt) {
		for (PlayerScoreboardInfo p : players) {
			if (!p.dead)
				p.survivalTime += dt;
		}
		toSort = true;
	}

	/**
	 * Sets the scoreboard information for the player
	 * @param info The scoreboard information
	 */
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

	/**
	 * Gets the player scoreboard information for a given player
	 * @param name The name of the player
	 * @return The player scoreboard information
	 */
	public PlayerScoreboardInfo getPlayer(String name) {
		for (PlayerScoreboardInfo p : players) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the player scoreboard information for a given player,
	 * adding that player if it is not currently present
	 * @param name The player's name
	 * @return The player's scoreboard information
	 */
	private PlayerScoreboardInfo getPlayerOrDefault(String name) {
		PlayerScoreboardInfo p = getPlayer(name);
		if (p != null)
			return p;
		p = new PlayerScoreboardInfo(name);
		players.add(p);
		toSort = true;
		return p;
	}

	/**
	 * Add a player to the scoreboard
	 * @param name The name of the player
	 */
	public void addPlayer(String name) {
		setPlayer(new PlayerScoreboardInfo(name));
		dirty = true;
	}

	/**
	 * Remove a player from the scoreboard
	 * @param name The name of the player
	 */
	public void removePlayer(String name) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).name.equals(name)) {
				players.remove(i);
				dirty = true;
				return;
			}
		}
	}

	/**
	 * Called when a player dies to show them as dead
	 * @param name The name of the player
	 * @param lastDamage The last type of damage they took
	 */
	public void killPlayer(String name, Damage lastDamage) {
		PlayerScoreboardInfo p = getPlayerOrDefault(name);
		p.dead = true;
		p.lastDamage = lastDamage;
		toSort = true;
		dirty = true;
	}

	/**
	 * Adds a monster kill for a given player on the scoreboard
	 * @param name The name of the player
	 */
	public void addMonsterKill(String name) {
		getPlayerOrDefault(name).monsterKills++;
		toSort = true;
		dirty = true;
	}

	/**
	 * Add a player kill for a given player on the scoreboard
	 * @param name The name of the player
	 */
	public void addPlayerKill(String name) {
		getPlayerOrDefault(name).playerKills++;
		toSort = true;
		dirty = true;
	}

	/**
	 * Add a player suicide to the scoreboard for a given player
	 * (-1 to the player kills column
	 * @param name The name of the player
	 */
	public void addPlayerSuicide(String name) {
		getPlayerOrDefault(name).playerKills--;
		toSort = true;
		dirty = true;
	}
	
	@Override
	public String toString() {
		return "[" + String.join(", ", getPlayers().stream().map(Object::toString).collect(Collectors.toList())) + "]";
	}
	
	@Override
	public Scoreboard clone() {
		Scoreboard s = new Scoreboard();
		for (PlayerScoreboardInfo p : players) {
			s.players.add(p.clone());
		}
		return s;
	}
}

package game.world;

import game.world.entity.damage.Damage;

public class PlayerScoreboardInfo {
	/** The name of the player */
	public String name;
	/** Whether the player is dead */
	public boolean dead;
	/** This stores how long the player has lived for. It is updated on the server and client independently,
	 * until the player died at which point the server updates the client with the authoritative time. */
	public double survivalTime;
	/** How many player kills this player has. */
	public int playerKills;
	/** How many monster kills this player has. */
	public int monsterKills;
	/** What the last damage the player took was (only valid if player is dead). Can be null. */
	public Damage lastDamage;
	
	public PlayerScoreboardInfo(String name) {
		this.name = name;
		this.dead = false;
		this.survivalTime = 0.0;
		this.playerKills = 0;
		this.monsterKills = 0;
		this.lastDamage = null;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(name);
		if (dead)
			b.append(" (dead)");
		b.append(": PK=").append(playerKills);
		b.append(" : MK=").append(monsterKills);
		b.append(" : time=").append(String.format("%.2f", survivalTime));
		return b.toString();
	}
	
	@Override
	public PlayerScoreboardInfo clone() {
		PlayerScoreboardInfo s = new PlayerScoreboardInfo(name);
		s.dead = dead;
		s.survivalTime = survivalTime;
		s.playerKills = playerKills;
		s.monsterKills = monsterKills;
		s.lastDamage = lastDamage;
		return s;
	}
}

package game.world;

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
	
	public PlayerScoreboardInfo(String name) {
		this.name = name;
		this.dead = false;
		this.survivalTime = 0.0;
		this.playerKills = 0;
		this.monsterKills = 0;
	}
}

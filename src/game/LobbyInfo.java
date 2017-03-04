package game;

import com.google.gson.annotations.SerializedName;

public class LobbyInfo {
	@SerializedName("name")
	public String lobbyName;
	@SerializedName("min")
	public int minPlayers;
	@SerializedName("max")
	public int maxPlayers;
	/** The current countdown time. This is <0 if there is no countdown happening */
	@SerializedName("cdn")
	public final double countdownTime;
	@SerializedName("ps")
	public PlayerInfo[] players;
	
	public LobbyInfo(String _lobbyName, int _minPlayers, int _maxPlayers, double _countdownTime, PlayerInfo[] _players) {
		this.lobbyName = _lobbyName;
		this.minPlayers = _minPlayers;
		this.maxPlayers = _maxPlayers;
		this.countdownTime = _countdownTime;
		this.players = _players;
	}

	/**
	 * Gets the name of the lobby
	 * @return The name of th lobby
	 */
	public String getLobbyName() {
		return lobbyName;
	}

	/**
	 * Gets the max number of players for the lobby
	 * @return The max number of players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Gets the info of the players in the lobby
	 * @return The player info
	 */
	public PlayerInfo[] getPlayerInfo() {
		return players;
	}
}
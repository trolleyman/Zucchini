package game;

public class LobbyInfo {
	public String lobbyName;
	public int maxPlayers;
	public PlayerInfo[] players;
	
	public LobbyInfo(String _lobbyName, int _maxPlayers, PlayerInfo[] _players) {
		this.lobbyName = _lobbyName;
		this.maxPlayers = _maxPlayers;
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
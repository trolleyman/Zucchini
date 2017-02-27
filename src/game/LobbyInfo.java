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
}
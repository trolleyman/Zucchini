package game;

public class LobbyInfo {
	public String name;
	public int maxPlayers;
	public PlayerInfo[] players;
	
	public LobbyInfo(String _name, int _maxPlayers, PlayerInfo[] _players) {
		this.name = _name;
		this.maxPlayers = _maxPlayers;
		this.players = _players;
	}
}

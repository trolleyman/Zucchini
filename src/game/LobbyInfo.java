package game;

import game.net.server.ClientInfo;
import game.net.server.Lobby;

import java.util.ArrayList;

public class LobbyInfo {
	public String name;
	public int maxPlayers;
	public PlayerInfo[] players;
	
	public LobbyInfo(String _name, int _maxPlayers, PlayerInfo[] _players) {
		this.name = _name;
		this.maxPlayers = _maxPlayers;
		this.players = _players;
	}
	
	public LobbyInfo(Lobby lobby) {
		this.name = lobby.name;
		this.maxPlayers = lobby.maxPlayers;
		
		// Get players
		ArrayList<ClientInfo> clients = lobby.getPlayers();
		this.players = new PlayerInfo[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			String playerName = clients.get(i).name;
			int team = clients.get(i).team;
			this.players[i] = new PlayerInfo(playerName, team);
		}
	}
}
package game.net.server;

public class LobbyClient {
	public ClientHandler handler;
	public int team;
	public boolean ready;
	
	public LobbyClient(ClientHandler handler, int team) {
		this.handler = handler;
		this.team = team;
		this.ready = false;
	}
}

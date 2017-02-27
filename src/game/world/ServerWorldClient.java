package game.world;

import game.net.server.ClientHandler;

public class ServerWorldClient {
	public ClientHandler handler;
	public int playerId;
	public boolean fullUpdate;
	
	public ServerWorldClient(ClientHandler handler, int playerId) {
		this.handler = handler;
		this.playerId = playerId;
		this.fullUpdate = true;
	}
}

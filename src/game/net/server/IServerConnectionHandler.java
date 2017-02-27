package game.net.server;

import game.LobbyInfo;

import java.util.ArrayList;

/**
 * The handler for the server connection
 */
public interface IServerConnectionHandler {
	/**
	 * Returns the list of current lobbies
	 */
	ArrayList<LobbyInfo> getLobbies();
}

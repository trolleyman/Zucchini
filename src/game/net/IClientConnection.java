package game.net;

import game.action.Action;

/**
 * This is the main interface used by the client to communicate with the server.
 * It is mirrored on the server-side by {@link game.net.IServerConnection IServerConnection}.
 * <p>
 * This class assumes that the client is already connected to the server with an acceptable username.
 * <p>
 * It is currently a WIP.
 * TODO: Lobby system
 * TODO: Username system
 * TODO: Password system
 * 
 * @author Callum
 */
public interface IClientConnection {
	/**
	 * Notifies the server that an action has been performed
	 * @param a The action
	 */
	public void sendAction(Action a);
	
	/**
	 * Requests the lobbies from the server
	 * @param cb Calls this when the operation finishes
	 */
	public void getLobbies(LobbyCallback cb);
	
	/**
	 * Sets the current connection event handler
	 * @param cch The client connection handler
	 */
	public void setHandler(IClientConnectionHandler cch);
	
	/**
	 * Called when the connection to the server should be closed
	 */
	public void close();
}

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
	void sendAction(Action a);
	
	/**
	 * Notifies the server to send a full update of the world on the next snapshot
	 */
	void requestFullUpdate();
	
	/**
	 * Sets the current connection event handler
	 * @param cch The client connection handler
	 */
	void setHandler(IClientConnectionHandler cch);
	
	/**
	 * Called when the connection to the server should be closed
	 */
	void close();
}

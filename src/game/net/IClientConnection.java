package game.net;

import game.action.Action;

/**
 * This is the main interface used by the client to communicate with the server.
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
	 * Notifies the server that an action has been performed.
	 * @param a The action.
	 */
	public void sendAction(Action a);
}
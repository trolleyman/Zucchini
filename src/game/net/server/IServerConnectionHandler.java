package game.net.server;

import game.action.Action;

public interface IServerConnectionHandler {
	/**
	 * Called when an action has been performed
	 * @param a The action
	 */
	void handleAction(Action a);
	
	/**
	 * Called when the client requests a full update on the next snapshot
	 */
	void handleFullUpdateRequest();
}

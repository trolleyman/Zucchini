package game.net.server;

import game.action.Action;

/**
 * The difference between this and the IServerConnectionHandler is that all of these events
 * are for a specific game, wheras IServerConnectionHandler is for the events that are more
 * "global" - joining a specific lobby, getting the lobbies, and things like that.
 */
public interface ILobbyServerConnectionHandler {
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

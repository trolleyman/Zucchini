package game.net;

import game.action.Action;

public interface IServerConnectionHandler {
	/**
	 * Called when an action has been performed
	 * @param a The action
	 */
	public void handleAction(Action a);
}

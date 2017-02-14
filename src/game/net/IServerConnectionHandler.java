package game.net;

import game.LobbyInfo;
import game.action.Action;

import java.util.ArrayList;

public interface IServerConnectionHandler {
	/**
	 * Called when an action has been performed
	 * @param a The action
	 */
	public void handleAction(Action a);
	
	/**
	 * Called when a request for lobbies has been received.
	 * @return The list of lobbies
	 */
	public ArrayList<LobbyInfo> getLobbies();
}

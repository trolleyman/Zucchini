package game.net;

import game.action.Action;

public interface IClientConnection {
	public void sendAction(Action a);
}

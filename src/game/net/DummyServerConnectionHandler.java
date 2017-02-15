package game.net;

import game.action.Action;

public class DummyServerConnectionHandler implements IServerConnectionHandler {
	@Override
	public void handleAction(Action a) {
		System.err.println("Warning: Server client connection handler called.");
	}
}

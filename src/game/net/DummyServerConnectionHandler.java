package game.net;

import game.action.Action;

public class DummyServerConnectionHandler implements IServerConnectionHandler {
	@Override
	public void handleAction(Action a) {
		System.err.println("Warning: Dummy server connection handler called.");
	}
	
	@Override
	public void handleFullUpdateRequest() {
		System.err.println("Warning: Dummy server connection handler called.");
	}
}

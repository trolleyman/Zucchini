package game.net.server;

import game.action.Action;

public class DummyLobbyServerConnectionHandler implements ILobbyServerConnectionHandler {
	@Override
	public void handleAction(Action a) {
		System.err.println("Warning: Dummy game server connection handler called.");
	}
	
	@Override
	public void handleFullUpdateRequest() {
		System.err.println("Warning: Dummy game server connection handler called.");
	}
}

package game.net;

import game.LobbyInfo;
import game.action.Action;

import java.util.ArrayList;

public class DummyServerConnectionHandler implements IServerConnectionHandler {
	@Override
	public void handleAction(Action a) {
		System.err.println("Warning: Dummy server connection handler called.");
	}
	
	@Override
	public ArrayList<LobbyInfo> getLobbies() {
		System.err.println("Warning: Dummy server connection handler called.");
		return new ArrayList<>();
	}
}

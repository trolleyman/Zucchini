package game.net.server;

import game.LobbyInfo;

import java.util.ArrayList;

public class DummyServerConnectionHandler implements IServerConnectionHandler {
	@Override
	public ArrayList<LobbyInfo> getLobbies() {
		return null;
	}
}

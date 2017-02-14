package game.net;

import game.LobbyInfo;

import java.util.ArrayList;

public interface LobbyCallback {
	public void success(ArrayList<LobbyInfo> lobbies);
	public void error(String s);
}

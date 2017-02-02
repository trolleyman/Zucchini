package game.networking.util;

import java.util.LinkedList;

public interface ServerMainable
{
	public void acceptClientConnection(String ClientName);

	public LinkedList<Touple<String, String>> getActions();

	public Touple<String, String> getAction();
}

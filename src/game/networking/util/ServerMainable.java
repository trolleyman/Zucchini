package game.networking.util;

import java.util.LinkedList;

public interface ServerMainable
{
	public void acceptClientConnection(String ClientName);

	public LinkedList<Tuple<String, String>> getActions();

	public Tuple<String, String> getAction();
}

package game.networking.util.interfaces;

import java.util.LinkedList;

import game.networking.util.Tuple;

public interface ITCPConnection
{
	public void closeConnection();

	public void setNewProcessor(LinkedList<String> send, LinkedList<Tuple<String, String>> receive);
}

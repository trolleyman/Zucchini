package game.networking.test;

import java.util.LinkedList;
import java.util.Map;

import game.networking.util.Tuple;

public class Processor implements Runnable
{
	Map<String, LinkedList<String>> sendMessages;
	LinkedList<Tuple<String, String>> receivedMessages;

	public Processor(Map<String, LinkedList<String>> _sendMessages, LinkedList<Tuple<String, String>> _receivedMessages)
	{
		sendMessages = _sendMessages;
		receivedMessages = _receivedMessages;
	}

	@Override
	public void run()
	{

	}

}

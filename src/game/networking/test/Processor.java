package game.networking.test;

import java.util.LinkedList;
import java.util.Map;

import com.sun.xml.internal.bind.v2.runtime.Name;

import game.networking.server.threads.LobbyThread;
import game.networking.util.Tuple;

public class Processor implements Runnable
{
	Map<String, LinkedList<String>> sendMessages;
	LinkedList<Tuple<String, String>> receivedMessages;
	boolean run;

	LobbyThread lobbyThread;

	public Processor(Map<String, LinkedList<String>> _sendMessages, LinkedList<Tuple<String, String>> _receivedMessages, LobbyThread _lobbyThread)
	{
		sendMessages = _sendMessages;
		receivedMessages = _receivedMessages;
		lobbyThread = _lobbyThread;
		run = true;
	}

	@Override
	public void run()
	{
		Tuple<String, String> tuple;
		String name;
		String mess;
		while (run)
		{
			tuple = null;
			synchronized (receivedMessages)
			{
				tuple = receivedMessages.poll();
			}
			if (tuple != null)
			{
				name = tuple.getFirst();
				mess = tuple.getSecond();

				if (mess.equals("[udpEnd]"))
				{
					lobbyThread.gameLobbyDissconnect(name);
				}
			}

			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void stop()
	{
		run = false;
	}

}

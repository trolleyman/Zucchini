package game.networking.server.threads.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import game.networking.util.Protocol;
import game.networking.util.Tuple;

public class TCPListenerLobbyThread implements Runnable
{
	private Socket socket;
	private String name;
	private Map<String, LinkedList<Tuple<String, String>>> messages;
	private LinkedList<Tuple<String, String>> actions;

	public TCPListenerLobbyThread(Socket _socket, String _name, Map<String, LinkedList<Tuple<String, String>>> _messages, LinkedList<Tuple<String, String>> _actions)
	{
		socket = _socket;
		name = _name;
		messages = _messages;
		actions = _actions;
		synchronized (messages)
		{
			messages.put(name, new LinkedList<>());
		}
	}

	@Override
	public void run()
	{
		boolean run = true;

		try
		{
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (run)
			{
				String messageFull = fromClient.readLine().trim();

				if (messageFull == null)
				{
					run = false;
					continue;

				}
				String message = messageFull.substring(0, messageFull.length() - name.length());
				if (message.startsWith(Protocol.TCP_Message))
				{
					synchronized (messages)
					{
						message = message.substring(Protocol.TCP_Message.length());
						for (String n : messages.keySet())
						{
							messages.get(n).add(new Tuple<>(name, message));
						}
						System.out.println(name + ": " + message);
					}
				} else
				{
					synchronized (actions)
					{
						actions.add(new Tuple<>(name, message));
						System.out.println("ACTION from: " + name + " TO DO: " + message);
					}
				}

			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		System.out.println(this.getClass().getName() + name + ">> i'm out!!");

	}

}

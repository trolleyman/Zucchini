package game.networking.server.threads.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import game.networking.util.Protocol;
import game.networking.util.Touple;

public class TCPListenerLobbyThread implements Runnable
{
	private Socket socket;
	private String name;
	private Map<String, LinkedList<Touple<String, String>>> messages;
	private LinkedList<Touple<String, String>> actions;

	public TCPListenerLobbyThread(Socket _socket, String _name, Map<String, LinkedList<Touple<String, String>>> _messages, LinkedList<Touple<String, String>> _actions)
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

			(new Thread(new TCPSenderLobbyThread(socket, name, messages.get(name)))).start();

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
							if (!n.equals(name))
							{
								messages.get(n).add(new Touple<>(name, message));
							}
						}
						System.out.println(name + ": " + message);
					}
				} else
				{
					synchronized (actions)
					{
						actions.add(new Touple<>(name, message));
						System.out.println("ACTION from: " + name + " TO DO: " + message);
					}
				}

			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		System.out.println(name + " i'm out!!");

	}

}

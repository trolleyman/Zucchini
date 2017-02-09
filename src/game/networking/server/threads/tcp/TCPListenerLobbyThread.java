package game.networking.server.threads.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import game.networking.util.Tuple;

public class TCPListenerLobbyThread implements Runnable
{
	private Socket socket;
	private String name;
	private LinkedList<Tuple<String, String>> actions;

	public TCPListenerLobbyThread(Socket _socket, String _name, LinkedList<Tuple<String, String>> _actions)
	{
		socket = _socket;
		name = _name;
		actions = _actions;
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
				System.out.println(messageFull);
				/// [ACTION/MESSAGE]stuff
				if (messageFull == null)
				{
					run = false;
					continue;

				}

				// String message = messageFull.substring();
				synchronized (actions)
				{
					actions.add(new Tuple<>(name, messageFull));
					// System.out.println("ACTION from: " + name + " TO DO:
					// " + message);
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
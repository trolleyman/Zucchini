package game.networking.server.threads.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import game.networking.util.Tuple;
import game.networking.util.interfaces.IConnectionHandler;

public class TCPListenerLobbyThread implements Runnable
{
	private Socket socket;
	private String name;
	private LinkedList<Tuple<String, String>> actions;
	private IConnectionHandler conHandler;
	boolean run;

	public TCPListenerLobbyThread(Socket _socket, String _name, LinkedList<Tuple<String, String>> _actions, IConnectionHandler _conHandler)
	{
		socket = _socket;
		name = _name;
		actions = _actions;
		conHandler = _conHandler;
	}

	@Override
	public void run()
	{
		run = true;

		try
		{
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (run)
			{
				if (fromClient.ready())
				{

					String messageFull = fromClient.readLine();
					if (messageFull != null)
					{

						messageFull = messageFull.trim();

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
							// System.out.println("ACTION from: " + name + " TO
							// DO:
							// " + message);
						}

					}
				}
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					run = false;
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		conHandler.TCPListenerUserDisconnect(name);

	}

	public synchronized void Stop()
	{
		run = false;
	}

}

package game.networking.server.threads;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import game.networking.test.*;

public class TCPListenerLobbyThread implements Runnable
{
	private Socket socket;
	private String name;
	private Map<String, String> messages;

	public TCPListenerLobbyThread(Socket _socket, String _name, Map<String, String> _messages)
	{
		socket = _socket;
		name = _name;
		messages = _messages;

	}

	@Override
	public void run()
	{
		boolean run = true;

		try
		{
			DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());
			toClient.writeBytes(name + "\n");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Thread thread = new Thread(new TickClass());
		// thread.start();
		while (run)
		{
			try
			{
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = fromClient.readLine();
				System.out.println(message);
				if (message == null)
				{
					run = false;
					continue;

				}
				if (message.contains("[MESS]"))
				{
					synchronized (this)
					{
						// messages.put(name, message);
						System.out.println(name + ": " + message);
					}
				} else
				{

				}

			} catch (IOException e)
			{
				// TODO: handle exception
				// e.printStackTrace();
				System.out.println(name + "-Disconected");
				run = false;
			}

		}
		System.out.println(name + " i'm out!!");

	}

}

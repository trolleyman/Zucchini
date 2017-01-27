package networking.server.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

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
		while (run)
		{
			try
			{
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = fromClient.readLine();
				if (message.contains("[MESS]"))
				{
					synchronized (this)
					{
						messages.put(name, message);
					}
				} else
				{

				}

			} catch (IOException e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}

		}

	}

}

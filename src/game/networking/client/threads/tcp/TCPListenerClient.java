package game.networking.client.threads.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class TCPListenerClient implements Runnable
{
	private Socket socket;
	private List<String> messages;
	private boolean run;

	public TCPListenerClient(Socket _socket, List<String> _messages)
	{
		socket = _socket;
		messages = _messages;
	}

	@Override
	public void run()
	{
		boolean run = true;
		try
		{
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (run)
			{
				if (fromServer.ready())
				{
					String messageFull = fromServer.readLine();
					if (messageFull != null)
					{

						messageFull = messageFull.trim();

						// System.out.println(messageFull);
						/// [ACTION/MESSAGE]stuff
						if (messageFull == null)
						{
							run = false;
							continue;

						}

						// String message = messageFull.substring();
						synchronized (messages)
						{
							messages.add(messageFull);
						}
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
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

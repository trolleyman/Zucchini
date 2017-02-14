package game.networking.client.threads.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class TCPSenderClient implements Runnable
{
	private Socket socket;
	private LinkedList<String> messages;
	private boolean run;

	public TCPSenderClient(Socket _socket, LinkedList<String> _messages)
	{
		socket = _socket;
		messages = _messages;
	}

	@Override
	public void run()
	{
		run = true;
		try
		{
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());

			while (run)
			{
				synchronized (messages)
				{
					while (!messages.isEmpty())
					{
						String t = messages.poll();
						try
						{
							toServer.writeBytes(t + "\n");

						} catch (IOException e)
						{
							run = false;
						}
					}
					
					try {
						messages.wait(100);
					} catch (InterruptedException e) {
						// This is fine
					}
				}
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	public void close() { this.run = false; }

}

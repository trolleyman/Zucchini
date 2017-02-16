package game.networking.main.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import game.networking.client.ClientConnection;

public class UserInput implements Runnable
{
	private LinkedList<String> toServer;
	ClientConnection clientConnection;

	public UserInput(LinkedList<String> _toServer)
	{
		toServer = _toServer;
		clientConnection = null;
	}

	public UserInput(LinkedList<String> _toServer, ClientConnection _clientConnection)
	{
		toServer = _toServer;
		clientConnection = _clientConnection;
	}

	@Override
	public void run()
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				if (reader.ready())
				{
					String message = reader.readLine();
					if (clientConnection != null)
					{
						if (message.equals("[udpSt]"))
						{
							clientConnection.startUDP();
						} else
							synchronized (this)
							{
								toServer.add(message);
							}

					} else
						synchronized (this)
						{
							toServer.add(message);
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
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

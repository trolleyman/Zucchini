package game.networking.test;

import java.util.LinkedList;

import game.networking.client.ClientConnection;

public class ClientTest2 implements Runnable
{
	LinkedList<String> toServer;
	LinkedList<String> fromServer;
	String name;

	public ClientTest2(String _name)
	{
		name = _name;
		ClientConnection clientConnection = new ClientConnection(_name);
		synchronized (this)
		{
			toServer = clientConnection.getToServerOutput();
			fromServer = clientConnection.getFromServerOutput();
		}
		(new Thread(clientConnection)).start();
	}

	@Override
	public void run()
	{
		while (true)
		{
			synchronized (fromServer)
			{
				String message = fromServer.poll();
				if (message != null)
					System.out.println(name + " - " + message);
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

}

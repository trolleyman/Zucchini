package game.networking.test;

import java.util.LinkedList;

import game.networking.client.ClientConnection;
import game.networking.main.demo.UserInput;

public class ClientTest2 implements Runnable
{
	LinkedList<String> toServer;
	LinkedList<String> fromServer;
	String name;
	boolean isHuman;

	public ClientTest2(String _name, boolean _isHuman)
	{
		name = _name;
		isHuman = _isHuman;
		ClientConnection clientConnection = new ClientConnection(_name);
		synchronized (this)
		{
			toServer = clientConnection.getToServerOutput();
			fromServer = clientConnection.getFromServerOutput();
		}
		(new Thread(clientConnection)).start();
		if (isHuman)
		{
			(new Thread(new UserInput(toServer))).start();
		}
	}

	public ClientTest2(String _name)
	{
		name = _name;
		isHuman = false;
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
				if (message != null && !message.contains("[PING]"))
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

	public void startUDP()
	{

	}

}
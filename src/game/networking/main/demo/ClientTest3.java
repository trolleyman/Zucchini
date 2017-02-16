package game.networking.main.demo;

import java.util.LinkedList;

import game.networking.client.ClientConnection;

public class ClientTest3 implements Runnable
{
	LinkedList<String> toServer;
	LinkedList<String> fromServer;
	String name;
	boolean isHuman = false;
	ClientConnection clientConnection;

	public ClientTest3(String _name, boolean _isHuman)
	{
		name = _name;
		isHuman = _isHuman;
		clientConnection = new ClientConnection(_name);
		synchronized (this)
		{
			toServer = clientConnection.getToServerOutput();
			fromServer = clientConnection.getFromServerOutput();
		}
		(new Thread(clientConnection)).start();
		if (isHuman)
		{
			(new Thread(new UserInput(toServer, clientConnection))).start();
		}
	}

	public ClientTest3(String _name)
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
				if (message != null && message.contains("[UDPS]"))
				{
					int send = Integer.parseInt(message.substring(message.indexOf("[UDPS]") + "[UDPS]".length(), message.indexOf("[UDPR]")));
					int rec = Integer.parseInt(message.substring(message.indexOf("[UDPR]") + "[UDPR]".length()));
					clientConnection.setUpUDP(send, rec);

				} else if (message != null && !message.contains("[PING]"))
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

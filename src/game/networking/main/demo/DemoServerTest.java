package game.networking.main.demo;

import game.networking.server.abstractClasses.ServerAbstract;
import game.networking.util.Tuple;

public class DemoServerTest extends ServerAbstract implements Runnable
{

	@Override
	public void run()
	{
		while (true)
		{
			synchronized (this)
			{
				Tuple<String, String> tup = getReceivedMess().poll();
				if (tup != null)
				{
					if (tup.getSecond().startsWith("[UDPStart]"))
					{
						String message = tup.getSecond();
						String lobbyName = message.substring("[UDPStart]".length(), message.indexOf("[UDPS]"));
						int sendportFromClient = Integer.parseInt(message.substring(message.indexOf("[UDPS]") + "[UDPS]".length(), message.indexOf("[UDPR]")));
						int receiveportFromClient = Integer.parseInt(message.substring(message.indexOf("[UDPR]") + "[UDPR]".length()));

						System.out.println(message);
						System.out.println("lobby name: " + lobbyName);
						System.out.println("SP: " + sendportFromClient);
						System.out.println("RP: " + receiveportFromClient);

						lobby.joinLobby(lobbyName, tup.getFirst(), lobby.getClientIP(tup.getFirst()), sendportFromClient, receiveportFromClient);

					} else
						for (String name : getSendMess().keySet())
						{
							getSendMess().get(name).add(tup.getFirst() + ": " + tup.getSecond());
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

	}

}
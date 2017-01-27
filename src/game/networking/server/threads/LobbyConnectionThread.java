package networking.server.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class LobbyConnectionThread implements Runnable
{
	// TODO: replace run = true with actual run var
	private boolean run = true;
	private LinkedList<String> acceptedClientsQueue;
	private LobbyThread lobby;
	private List<String> acceptedClients;

	public LobbyConnectionThread(LinkedList<String> _acceptedClientsQueue, LobbyThread _lobby, List<String> _acceptedClients)
	{
		acceptedClientsQueue = _acceptedClientsQueue;
		lobby = _lobby;
		acceptedClients = _acceptedClients;
	}

	@Override
	public void run()
	{
		String client = null;
		while (run)
		{
			synchronized (this)
			{
				try
				{
					client = acceptedClientsQueue.removeFirst();
				} catch (NoSuchElementException e)
				{
					client = null;
				}
			}
			if (client != null)
			{
				synchronized (this)
				{

					lobby.sendAccept(client);
					acceptedClients.add(client);
				}
			} else
			{

				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}

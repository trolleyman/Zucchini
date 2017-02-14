package game.networking.main.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class UserInput implements Runnable
{
	private LinkedList<String> toServer;

	public UserInput(LinkedList<String> _toServer)
	{
		toServer = _toServer;
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

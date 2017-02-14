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

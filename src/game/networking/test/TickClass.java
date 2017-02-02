package game.networking.test;

public class TickClass implements Runnable
{

	@Override
	public void run()
	{
		while (true)
		{
			System.out.println("tick");
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

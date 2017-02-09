package game.networking.main;

import game.networking.client.ClientTest;
import game.networking.client.ClientTest1;

public class NetMain2
{
	public static void main(String[] args)
	{

		// ClientTest clientTest = new ClientTest();

		Thread thread = new Thread(new Runnable() {
			public void run()
			{
				ClientTest clientTest = new ClientTest("T1");
				clientTest.start();
			}
		});
		thread.start();

		Thread thread1 = new Thread(new Runnable() {
			public void run()
			{
				ClientTest clientTest = new ClientTest("T2");
				clientTest.start();
			}
		});
		thread1.start();

		Thread thread2 = new Thread(new Runnable() {
			public void run()
			{
				ClientTest clientTest = new ClientTest("T3");
				clientTest.start();
			}
		});
		thread2.start();

		Thread thread3 = new Thread(new Runnable() {
			public void run()
			{
				ClientTest clientTest = new ClientTest("T1000");
				clientTest.start();
			}
		});
		thread3.start();

		Thread thread4 = new Thread(new Runnable() {
			public void run()
			{
				ClientTest clientTest = new ClientTest("T1000");
				clientTest.start();
			}
		});
		thread4.start();
	}
}

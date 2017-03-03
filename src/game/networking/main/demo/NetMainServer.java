package game.networking.main.demo;

public class NetMainServer
{

	public static void main(String[] args)
	{
		DemoServerTest serverTest = new DemoServerTest();
		serverTest.startServer();
		(new Thread(serverTest)).start();

		// char = 2bytes

		/*
		 * ClientTest clientTest = new ClientTest("old"); clientTest.start();
		 */
	}

}

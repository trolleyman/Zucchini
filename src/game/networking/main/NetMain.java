package networking.main;

import networking.server.*;
import networking.client.*;

public class NetMain
{

	public static void main(String[] args)
	{
		ServerTest serverTest = new ServerTest(8888);
		serverTest.startServer();

		// char = 2bytes

		/*
		 * ClientTest clientTest = new ClientTest("old"); clientTest.start();
		 */
	}

}

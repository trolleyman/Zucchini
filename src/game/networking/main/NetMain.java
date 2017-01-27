package game.networking.main;

import game.networking.server.*;
import game.networking.client.*;

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

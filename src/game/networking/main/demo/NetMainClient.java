package game.networking.main.demo;

import game.networking.main.demo.ClientTest3;

public class NetMainClient
{
	public static void main(String[] args)
	{

		ClientTest3 t1 = new ClientTest3("Human", true);
		(new Thread(t1)).start();
		ClientTest3 t2 = new ClientTest3("T1");
		(new Thread(t2)).start();
		ClientTest3 t3 = new ClientTest3("T2");
		(new Thread(t3)).start();
		ClientTest3 t4 = new ClientTest3("T3");
		(new Thread(t4)).start();
		ClientTest3 t5 = new ClientTest3("T1000");
		(new Thread(t5)).start();
		ClientTest3 t6 = new ClientTest3("T1000");
		(new Thread(t6)).start(); 
	}
}

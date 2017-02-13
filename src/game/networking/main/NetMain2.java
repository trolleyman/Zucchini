package game.networking.main;

import java.util.LinkedList;

import game.networking.test.ClientTest;
import game.networking.test.ClientTest1;
import game.networking.test.ClientTest2;
import game.networking.util.Tuple;

public class NetMain2
{
	public static void main(String[] args)
	{

		ClientTest2 t1 = new ClientTest2("T1");
		ClientTest2 t2 = new ClientTest2("T2");
		ClientTest2 t3 = new ClientTest2("T3");
		ClientTest2 t4 = new ClientTest2("T1000");
		ClientTest2 t5 = new ClientTest2("T1000");

		(new Thread(t1)).start();
		(new Thread(t2)).start();
		(new Thread(t3)).start();
		(new Thread(t4)).start();
		(new Thread(t5)).start();

		// ClientTest clientTest = new ClientTest();

		// LinkedList<ClientTestRunn> cRunns = new LinkedList<>();
		// boolean[] successes = new boolean[6];
		//
		// for (int i = 1; i <= 3; i++)
		// {
		// cRunns.add(new ClientTestRunn(("T" + i)));
		// }
		// cRunns.add(new ClientTestRunn(("T1000")));
		// cRunns.add(new ClientTestRunn(("T1000")));
		//
		// for (ClientTestRunn client : cRunns)
		// {
		// (new Thread(client)).start();
		// }
		//
		// int i = 1;
		//
		// for (ClientTestRunn client : cRunns)
		// {
		// while (!client.done())
		// {
		// try
		// {
		// Thread.sleep(100);
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// if (client.done())
		// {
		// successes[i] = client.success;
		// i++;
		// }
		// }
		// for (i = 1; i < successes.length; i++)
		// {
		// System.out.println(i + ": " + successes[i]);
		// }

		// Thread thread = new Thread(new Runnable() {
		// public void run()
		// {
		// ClientTest clientTest = new ClientTest("T1");
		// clientTest.start();
		// }
		// });
		// thread.start();
		//
		// Thread thread1 = new Thread(new Runnable() {
		// public void run()
		// {
		// ClientTest clientTest = new ClientTest("T2");
		// clientTest.start();
		// }
		// });
		// thread1.start();
		//
		// Thread thread2 = new Thread(new Runnable() {
		// public void run()
		// {
		// ClientTest clientTest = new ClientTest("T3");
		// clientTest.start();
		// }
		// });
		// thread2.start();
		//
		// Thread thread3 = new Thread(new Runnable() {
		// public void run()
		// {
		// ClientTest clientTest = new ClientTest("T1000");
		// clientTest.start();
		// }
		// });
		// thread3.start();
		//
		// Thread thread4 = new Thread(new Runnable() {
		// public void run()
		// {
		// ClientTest clientTest = new ClientTest("T1000");
		// clientTest.start();
		// }
		// });
		// thread4.start();
	}
}

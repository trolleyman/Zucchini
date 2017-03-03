package game.networking.main;

import com.sun.swing.internal.plaf.synth.resources.synth;

import game.networking.test.ClientTest;

public class ClientTestRunn implements Runnable
{
	String name;
	boolean success = false;
	boolean done = false;

	public ClientTestRunn(String _name)
	{
		name = _name;
	}

	@Override
	public void run()
	{
		ClientTest clientTest = new ClientTest(name);
		clientTest.start();
		synchronized (this)
		{
			success = clientTest.success();
			done = true;
		}

	}

	public synchronized boolean getSuccess()
	{
		return success;
	}

	public synchronized boolean done()
	{
		return done;
	}

}

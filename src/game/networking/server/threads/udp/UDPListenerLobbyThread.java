package game.networking.server.threads.udp;

import java.net.DatagramSocket;

//FAST: implement this!!!

public class UDPListenerLobbyThread implements Runnable
{
	private DatagramSocket socket;
	private boolean run = false;

	public UDPListenerLobbyThread(DatagramSocket _socket)
	{
		socket = _socket;
	}

	@Override
	public void run()
	{

	}

	public synchronized void StartGame()
	{
		run = true;
		(new Thread(this)).start();
	}

	public synchronized void StopGame()
	{
		run = false;
	}

}

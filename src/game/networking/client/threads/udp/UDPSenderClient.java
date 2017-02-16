package game.networking.client.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class UDPSenderClient implements Runnable
{

	private DatagramSocket socket;
	private int port;
	private LinkedList<String> toServer;
	private InetAddress address;
	private boolean run;

	public UDPSenderClient(DatagramSocket _UDPsocket, InetAddress _address, int _port, LinkedList<String> _udpToServer)
	{
		socket = _UDPsocket;
		port = _port;
		toServer = _udpToServer;
		address = _address;
		run = false;
	}

	@Override
	public void run()
	{
		while (run)
		{
			String message = null;
			message = toServer.poll();
			if (message != null)
			{

				try
				{
					byte[] buffer = message.getBytes();
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

					socket.send(packet);

				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public void start()
	{
		run = true;
		(new Thread(this)).start();

	}

	public synchronized void stop()
	{
		run = false;
	}

}

package game.networking.client.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class UDPSenderClient implements Runnable
{

	private DatagramSocket socket;
	private LinkedList<String> toServer;
	private boolean run;

	public UDPSenderClient(DatagramSocket _UDPsocket, LinkedList<String> _udpToServer)
	{
		socket = _UDPsocket;
		toServer = _udpToServer;
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
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socket.getInetAddress(), socket.getPort());

					socket.send(packet);

				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public synchronized void start()
	{
		run = true;
		(new Thread(this)).start();

	}

	public synchronized void stop()
	{
		run = false;
	}

	public synchronized void setServerPort(int port, InetAddress server)
	{
		socket.connect(server, port);

		System.out.println(socket.getInetAddress() + " - " + socket.getPort());

	}

}

package game.networking.client.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class UDPListenerClient implements Runnable
{

	private DatagramSocket socket;
	private LinkedList<String> fromServer;

	private boolean run;

	public UDPListenerClient(DatagramSocket _UDPsocket, LinkedList<String> _udpFromServer)
	{
		socket = _UDPsocket;
		fromServer = _udpFromServer;
		run = false;
	}

	@Override
	public void run()
	{
		while (run)
		{
			try
			{
				// System.out.println("Listening to " + socket.getPort());
				byte[] buffer = new byte[50000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				String message = new String(packet.getData());
				// System.out.println(message);
				synchronized (fromServer)
				{
					// fromServer.add(message);
				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		System.out.println("client list: " + socket.getInetAddress() + " - " + socket.getPort());

	}

}

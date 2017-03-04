package game.networking.client.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

import game.networking.util.Protocol;

public class UDPSenderClient implements Runnable
{

	private DatagramSocket socket;
	private LinkedList<String> toServer;
	private boolean run;
	private String name;

	public UDPSenderClient(String _name, DatagramSocket _UDPsocket, LinkedList<String> _udpToServer)
	{
		name = _name;
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
			message = "hello Server UDP";// toServer.poll();
			message = Protocol.UDP_playerNameTagBegin + name + Protocol.UDP_playerNameTagEnd + message;
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
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
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

		System.out.println("client send: " + socket.getInetAddress() + " - " + socket.getPort());

	}

}

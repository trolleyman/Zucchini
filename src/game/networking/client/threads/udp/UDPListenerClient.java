package game.networking.client.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class UDPListenerClient implements Runnable
{

	private DatagramSocket socket;
	private int port;
	private LinkedList<String> fromServer;

	public UDPListenerClient(DatagramSocket _UDPsocket, int _port, LinkedList<String> _udpFromServer)
	{
		socket = _UDPsocket;
		port = _port;
		fromServer = _udpFromServer;
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{

				byte[] buffer = new byte[50000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				String message = new String(packet.getData());
				synchronized (fromServer)
				{
					fromServer.add(message);
				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

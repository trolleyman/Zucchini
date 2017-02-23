package game.networking.server.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Map;

import game.networking.util.ConnectionDetails;
import game.networking.util.Protocol;
import game.networking.util.Tuple;

//FAST: implement this!!!!

public class UDPSenderLobbyThread implements Runnable
{

	private DatagramSocket socket;
	private boolean run = false;
	private LinkedList<Tuple<String, String>> UDP_actions;
	private Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> clients;

	public UDPSenderLobbyThread(DatagramSocket _socket, LinkedList<Tuple<String, String>> _udpActions, Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> _gameClients)
	{
		socket = _socket;
		UDP_actions = _udpActions;
		clients = _gameClients;
		try
		{
			socket.setBroadcast(true);
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{

		while (run)
		{
			Tuple<String, String> tuple = null;
			synchronized (UDP_actions)
			{
				tuple = new Tuple<String, String>("Server", "udp");// UDP_actions.poll();
			}
			if (tuple != null)
			{
				String message = Protocol.UDP_playerNameTagBegin + tuple.getFirst() + Protocol.UDP_playerNameTagEnd + tuple.getSecond();
				byte[] buffer = message.getBytes();
				synchronized (this)
				{
					for (String name : clients.keySet())
					{
						InetAddress address = clients.get(name).getFirst();
						int port = clients.get(name).getSecond().getSecond();
						DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address, port);
						try
						{
							// System.out.println("Sending to" + dp.getAddress()
							// + "
							// - " + dp.getPort());
							socket.send(dp);
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		System.out.println("udp Sender exit");

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

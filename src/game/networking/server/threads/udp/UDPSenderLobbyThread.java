package game.networking.server.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	}

	@Override
	public void run()
	{

		while (run)
		{
			Tuple<String, String> tuple = null;
			synchronized (UDP_actions)
			{
				tuple = UDP_actions.poll();
			}
			if (tuple != null)
			{
				String message = Protocol.UDP_playerNameTagBegin + tuple.getFirst() + Protocol.UDP_playerNameTagEnd + tuple.getSecond();
				byte[] buffer = message.getBytes();
				for (String name : clients.keySet())
				{
					InetAddress address = clients.get(name).getFirst();
					int port = clients.get(name).getSecond().getSecond();
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address, port);
					try
					{
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

package game.networking.server.threads.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Map;

import game.networking.util.Protocol;
import game.networking.util.Tuple;

public class UDPListenerLobbyThread implements Runnable
{
	private DatagramSocket socket;
	private boolean run = false;
	private LinkedList<Tuple<String, String>> UDP_actions;
	private Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> clients;

	public UDPListenerLobbyThread(DatagramSocket _socket, LinkedList<Tuple<String, String>> _udpActions, Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> _gameClients)
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
			byte[] receiveBuffer = new byte[50000];
			DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			try
			{
				socket.receive(packet);
				String message = new String(packet.getData()).trim();
				int tagEnd = message.indexOf(Protocol.UDP_playerNameTagEnd);
				int tagBegin = message.indexOf(Protocol.UDP_playerNameTagBegin);
				if (tagBegin == 0)
				{
					String plName = message.substring(tagBegin + Protocol.UDP_playerNameTagBegin.length(), tagEnd);
					String stuff = message.substring(tagEnd + Protocol.UDP_playerNameTagEnd.length());
					synchronized (UDP_actions)
					{
						UDP_actions.add(new Tuple<>(plName, stuff));
					}
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("UDP LISTENER could not receive message");
				run = false;
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

package game.networking.server.threads.udp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;

import game.networking.util.ConnectionDetails;
import game.networking.util.Tuple;
import game.networking.util.UtilityCode;

public class UDP_Connection
{

	private UDPListenerLobbyThread udpListener;
	private UDPSenderLobbyThread udpSender;

	private int portSender;
	private int portReceiver;

	public UDP_Connection(Map<String, Tuple<InetAddress, Tuple<Integer, Integer>>> _gameClients, LinkedList<Tuple<String, String>> _receivedActions, LinkedList<Tuple<String, String>> _sendActions)
	{

		DatagramSocket datagramSocket;
		try
		{
			portReceiver = UtilityCode.getNextAvailabePort();
			if (portReceiver != -1)
			{
				datagramSocket = new DatagramSocket(portReceiver, InetAddress.getByName("0.0.0.0"));
				udpListener = new UDPListenerLobbyThread(datagramSocket, _receivedActions, _gameClients);
			}

			portSender = UtilityCode.getNextAvailabePort();
			if (portSender != -1)
			{
				datagramSocket = new DatagramSocket(portSender, InetAddress.getByName("0.0.0.0"));
				udpSender = new UDPSenderLobbyThread(datagramSocket, _sendActions, _gameClients);
			}

		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized int getSenderPort()
	{
		return portSender;
	}

	public synchronized int getReceiverPort()
	{
		return portReceiver;
	}

	public synchronized void StartGame()
	{
		udpListener.StartGame();
		udpSender.StartGame();
	}

	public synchronized void StopGame()
	{
		udpListener.StopGame();
		udpSender.StopGame();
	}
}

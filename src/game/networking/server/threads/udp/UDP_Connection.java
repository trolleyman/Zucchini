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
	public UDP_Connection(Map<String, ConnectionDetails> _gameClients, LinkedList<Tuple<String, String>> _receivedActions, LinkedList<Tuple<String, String>> _sendActions)
	{

		int port = UtilityCode.getNextAvailabePort();
		if (port != -1)
		{

			DatagramSocket datagramSocket;
			try
			{
				datagramSocket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));

				UDPListenerLobbyThread udpListenerLobbyThread = new UDPListenerLobbyThread(datagramSocket, _receivedActions);
				UDPSenderLobbyThread udpSenderLobbyThread = new UDPSenderLobbyThread(datagramSocket, _sendActions, _gameClients);

				udpListenerLobbyThread.StartGame();
				udpSenderLobbyThread.StartGame();

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
	}
}

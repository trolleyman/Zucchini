package game.networking.server.threads.tcp;

import java.net.Socket;
import java.util.LinkedList;

import game.networking.util.Tuple;

public class TCP_Connection
{
	LinkedList<Tuple<String, String>> receivedMes;
	LinkedList<Tuple<String, String>> sendMes;

	public TCP_Connection(Socket _socket, String _name, LinkedList<Tuple<String, String>> _sendMes, LinkedList<Tuple<String, String>> _receivedMes)
	{

		receivedMes = _receivedMes;
		sendMes = _sendMes;

		TCPListenerLobbyThread tcpListener = new TCPListenerLobbyThread(_socket, _name, _receivedMes);
		TCPSenderLobbyThread tcpSenderLobby = new TCPSenderLobbyThread(_socket, _name, _sendMes);

		(new Thread(tcpListener)).start();
		(new Thread(tcpSenderLobby)).start();

	}

	// public synchronized LinkedList<Tuple<String, String>>
	// getReceivedMessages()
	// {
	// return receivedMes;
	// }
	//
	// public synchronized LinkedList<Tuple<String, String>>
	// getSendingMessages()
	// {
	// return sendMes;
	// }

}

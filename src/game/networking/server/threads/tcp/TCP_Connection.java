package game.networking.server.threads.tcp;

import java.net.Socket;
import java.util.LinkedList;

import game.networking.util.Tuple;

public class TCP_Connection
{
	LinkedList<Tuple<String, String>> receivedMes;
	LinkedList<String> sendMes;

	public TCP_Connection(Socket _socket, String _name, LinkedList<String> _sendMessages, LinkedList<Tuple<String, String>> _receivedMes)
	{

		receivedMes = _receivedMes;
		sendMes = _sendMessages;

		TCPListenerLobbyThread tcpListener = new TCPListenerLobbyThread(_socket, _name, _receivedMes);
		TCPSenderLobbyThread tcpSenderLobby = new TCPSenderLobbyThread(_socket, _name, _sendMessages);

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

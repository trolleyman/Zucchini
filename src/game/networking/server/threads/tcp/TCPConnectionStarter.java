package game.networking.server.threads.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.lwjgl.system.CallbackI.S;

import game.networking.util.Tuple;
import game.networking.util.interfaces.IConnectionHandler;
import game.networking.util.interfaces.ITCPConnection;

public class TCPConnectionStarter implements ITCPConnection
{
	LinkedList<Tuple<String, String>> receivedMes;
	LinkedList<String> sendMes;
	Socket socket;
	TCPSenderLobbyThread tcpSender;
	TCPListenerLobbyThread tcpListener;

	public TCPConnectionStarter(Socket _socket, String _name, LinkedList<String> _sendMessages, LinkedList<Tuple<String, String>> _receivedMes, IConnectionHandler _conHandler)
	{

		receivedMes = _receivedMes;
		sendMes = _sendMessages;
		socket = _socket;

		tcpListener = new TCPListenerLobbyThread(_socket, _name, _receivedMes, _conHandler);
		tcpSender = new TCPSenderLobbyThread(_socket, _name, _sendMessages, _conHandler);

		(new Thread(tcpListener)).start();
		(new Thread(tcpSender)).start();

	}

	@Override
	public void closeConnection()
	{
		tcpListener.Stop();
		tcpSender.Stop();
		try
		{
			socket.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void setNewProcessor(LinkedList<String> send, LinkedList<Tuple<String, String>> receive)
	{

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

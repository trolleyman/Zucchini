package game.networking.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import game.networking.client.threads.tcp.TCPListenerClient;
import game.networking.client.threads.tcp.TCPSenderClient;

public class ClientConnection implements Runnable
{
	String name;
	boolean success = false;

	LinkedList<String> toServer;
	LinkedList<String> fromServer;

	public ClientConnection(String _name)
	{
		name = _name;
		toServer = new LinkedList<>();
		fromServer = new LinkedList<>();
	}

	@Override
	public void run()
	{
		DatagramSocket socket;
		Socket tcpSocket = null;
		// FAST: create TCP connection for client
		try
		{
			socket = new DatagramSocket();
			ClientDiscovery clientDiscovery = new ClientDiscovery(name, socket);
			if (clientDiscovery.isAccepted())
			{
				System.out.println("---------------------------------" + name + ": Success!!");
				try
				{
					tcpSocket = new Socket(clientDiscovery.getServerAddress(), clientDiscovery.getTCPport());
					DataOutputStream toServer = new DataOutputStream(tcpSocket.getOutputStream());
					toServer.writeBytes(name + "\n");
					success = true;
				} catch (IOException e)
				{
					success = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				System.out.println("---------------------------------" + name + ": fail!!");
				success = false;
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (success)
		{

			TCPListenerClient listener = new TCPListenerClient(tcpSocket, fromServer);
			TCPSenderClient sender = new TCPSenderClient(tcpSocket, toServer);

			(new Thread(listener)).start();
			(new Thread(sender)).start();
		}

	}

	public synchronized LinkedList<String> getToServerOutput()
	{
		return toServer;
	}

	public synchronized LinkedList<String> getFromServerOutput()
	{
		return fromServer;
	}

}

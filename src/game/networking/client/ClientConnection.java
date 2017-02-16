package game.networking.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import game.networking.client.threads.tcp.TCPListenerClient;
import game.networking.client.threads.tcp.TCPSenderClient;
import game.networking.client.threads.udp.UDPConnectionClient;

public class ClientConnection implements Runnable
{
	String name;
	boolean success = false;

	LinkedList<String> toServer;
	LinkedList<String> fromServer;

	LinkedList<String> udptoServer;
	LinkedList<String> udpfromServer;

	InetAddress server;

	public ClientConnection(String _name)
	{
		name = _name;
		toServer = new LinkedList<>();
		fromServer = new LinkedList<>();

		udptoServer = new LinkedList<>();
		udpfromServer = new LinkedList<>();

	}

	@Override
	public void run()
	{
		DatagramSocket socket = null;
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
					server = clientDiscovery.getServerAddress();
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
		try
		{
			socket.setReuseAddress(true);
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();

	}

	public void startUDP()
	{
		UDPConnectionClient conn = new UDPConnectionClient(server, udptoServer, udpfromServer);
		if (conn.getReceivePort() != -1 && conn.getSendPort() != -1)
		{
			synchronized (this)
			{
				toServer.add("[UDPStart]lobbyname[UDPS]" + conn.getSendPort() + "[UDPR]" + conn.getReceivePort());
			}
			conn.Start();
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

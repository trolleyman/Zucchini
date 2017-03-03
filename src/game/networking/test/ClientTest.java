package game.networking.test;

import java.net.*;
import java.util.Enumeration;
import java.util.logging.*;

import game.networking.client.ClientDiscovery;
import game.networking.util.Protocol;
import game.networking.util.TraceLog;
import game.networking.util.UtilityCode;

import java.io.*;

public class ClientTest
{
	String name;
	boolean success = false;

	public ClientTest(String _name)
	{
		name = _name;
	}

	public void start()
	{
		DatagramSocket socket;
		// FAST: create TCP connection for client
		try
		{
			socket = new DatagramSocket();
			ClientDiscovery clientDiscovery = new ClientDiscovery(name, socket);
			if (clientDiscovery.isAccepted())
			{
				System.out.println("---------------------------------" + name + ": Success!!");
				success = true;
				Socket s;
				try
				{
					s = new Socket(clientDiscovery.getServerAddress(), clientDiscovery.getTCPport());
					DataOutputStream toServer = new DataOutputStream(s.getOutputStream());
					toServer.writeBytes(name + "\n");
				} catch (IOException e)
				{
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
	}

	public boolean success()
	{
		return success;
	}

}

package game.networking.client;

import java.net.*;
import java.util.Enumeration;
import java.util.logging.*;

import game.networking.util.Protocol;
import game.networking.util.TraceLog;
import game.networking.util.UtilityCode;

import java.io.*;

public class ClientTest
{
	String name;

	public ClientTest(String _name)
	{
		name = _name;
	}

	public void start()
	{
		DatagramSocket socket;
		try
		{
			socket = new DatagramSocket();
			ClientDiscovery clientDiscovery = new ClientDiscovery(name, socket);
			if (clientDiscovery.isAccepted())
			{
				System.out.println("---------------------------------" + name + ": Success!!");
			} else
			{
				System.out.println("---------------------------------" + name + ": fail!!");
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

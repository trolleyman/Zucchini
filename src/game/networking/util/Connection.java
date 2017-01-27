package game.networking.util;

import java.net.InetAddress;

public class Connection
{
	public InetAddress address;
	public int port;

	public Connection(InetAddress _address, int _port)
	{
		address = _address;
		port = _port;
	}
}

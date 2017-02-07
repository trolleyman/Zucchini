package game.networking.util;

import java.net.InetAddress;

public class ConnectionDetails
{
	public InetAddress address;
	public int port;

	public ConnectionDetails(InetAddress _address, int _port)
	{
		address = _address;
		port = _port;
	}
}

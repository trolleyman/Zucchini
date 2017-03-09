package game.networking.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class UtilityCode
{
	public static final int MIN_PORT_NUMBER = 1;
	public static final int MAX_PORT_NUMBER = 65535;

	public static int getNextAvailabePort()
	{
		for (int i = MIN_PORT_NUMBER; i < MAX_PORT_NUMBER; i++)
		{
			if (isPortAvailable(i))
				return i;
		}
		return -1;
	}

	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port
	 *            the port to check for availability
	 */
	public static boolean isPortAvailable(int port)
	{
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER)
		{
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try
		{
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e)
		{
		} finally
		{
			if (ds != null)
			{
				ds.close();
			}

			if (ss != null)
			{
				try
				{
					ss.close();
				} catch (IOException e)
				{
					/* should not be thrown */
					e.printStackTrace();
				}
			}
		}

		return false;
	}
}

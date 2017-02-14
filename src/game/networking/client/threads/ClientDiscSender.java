package game.networking.client.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.sun.swing.internal.plaf.synth.resources.synth;

import game.networking.util.Protocol;
import game.networking.util.TraceLog;
import game.networking.util.UtilityCode;

public class ClientDiscSender implements Runnable
{
	private DatagramSocket socket;
	private String name;
	private boolean done = false;

	public ClientDiscSender(String _name, DatagramSocket _socket)
	{
		socket = _socket;
		name = _name;
	}

	@Override
	public void run()
	{

		// Find the server using UDP broadcast
		try
		{
			// Open a random port to send the package
			DatagramSocket datagramSender = socket;
			datagramSender.setBroadcast(true);

			byte[] sendData = (Protocol.CtoS_Discovery + name).getBytes();

			// Try the 255.255.255.255 first
			try
			{
				for (int i = UtilityCode.MIN_PORT_NUMBER; i < UtilityCode.MAX_PORT_NUMBER; i++)
				{
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), i);
					datagramSender.send(sendPacket);
				}
				TraceLog.consoleLog(getClass().getName() + name + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp())
				{
					continue; // Don't want to broadcast to the loopback
								// interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
				{
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
					{
						continue;
					}

					// Send the broadcast package!
					try
					{

						for (int i = UtilityCode.MIN_PORT_NUMBER; i < UtilityCode.MAX_PORT_NUMBER; i++)
						{
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, i);
							datagramSender.send(sendPacket);
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}

					TraceLog.consoleLog(getClass().getName() + name + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}
			}

			TraceLog.consoleLog(getClass().getName() + name + ">>> Done looping over all network interfaces. Now waiting for a reply!");

		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		done = true;
	}

	public synchronized boolean done()
	{
		return done;
	}

}

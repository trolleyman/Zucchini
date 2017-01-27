package game.networking.client;

import java.net.*;
import java.util.Enumeration;
import java.util.logging.*;

import game.networking.util.Protocol;
import game.networking.util.TraceLog;

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

		// Find the server using UDP broadcast
		try
		{
			// Open a random port to send the package
			DatagramSocket c = new DatagramSocket();
			c.setBroadcast(true);

			byte[] sendData = (Protocol.CtoS_Discovery + name).getBytes();

			// Try the 255.255.255.255 first
			try
			{
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
				c.send(sendPacket);
				TraceLog.consoleLog(getClass().getName() + name + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
			} catch (Exception e)
			{
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
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
						c.send(sendPacket);
					} catch (Exception e)
					{
					}

					TraceLog.consoleLog(getClass().getName() + name + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}
			}

			TraceLog.consoleLog(getClass().getName() + name + ">>> Done looping over all network interfaces. Now waiting for a reply!");
			boolean wait = true;
			// Wait for a response
			while (wait)
			{
				// we no longer need to wait
				wait = false;

				byte[] recvBuf = new byte[15000];
				DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				c.receive(receivePacket);

				// We have a response
				TraceLog.consoleLog(getClass().getName() + name + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

				// Check if the message is correct
				String message = new String(receivePacket.getData()).trim();

				TraceLog.consoleLog(getClass().getName() + name + ">>> SERVER MESSAGE: " + message);

				// if message is destined to me deocde it
				if (message.endsWith(name))
				{
					int namePos = message.lastIndexOf(name);
					// System.out.println(message.substring(0, namePos));
					message = message.substring(0, namePos);
				}

				switch (message)
				{
				case Protocol.StoC_DiscoveryAccept:
				{

					// DO SOMETHING WITH THE SERVER'S IP (for example, store it
					// in
					// your controller)
					// Controller_Base.setServerIp(receivePacket.getAddress());
					TraceLog.consoleLog(getClass().getName() + name + ">>> Connected to: " + receivePacket.getAddress().getHostAddress());
					TraceLog.consoleLog(getClass().getName() + name + " Exiting!!!!");

					// FIXME: this is not good

					Socket s = new Socket(receivePacket.getAddress(), receivePacket.getPort() + 1);
					DataOutputStream toServer = new DataOutputStream(s.getOutputStream());
					toServer.writeBytes(name + "\n");

					// FIXME: this is not good
					boolean t = true;
					while (t)
					{

						// System.out.println(getClass().getName() + name + ">>>
						// SLEEP!!");
						try
						{
							Thread.sleep(1000);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					break;
				}
				case Protocol.StoC_DiscoveryWait:
				{
					TraceLog.consoleLog(getClass().getName() + name + ">>>Waiting...");
					wait = true;
					break;
				}
				case Protocol.StoC_DiscoveryReject:
				{
					TraceLog.consoleLog(getClass().getName() + name + ">>> Rejected by: " + receivePacket.getAddress().getHostAddress());
					TraceLog.consoleLog(getClass().getName() + name + ">>> Sad Face :(");
					wait = false;
					break;
				}

				default:
				{
					// FIXME: what happens if we do not get a message for us or
					// a message that we do not know what it means
					break;
				}
				}
			}
			// // Close the port!
			c.close();
		} catch (IOException ex)
		{
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
	}

}

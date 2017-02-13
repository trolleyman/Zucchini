package game.networking.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import game.networking.client.threads.ClientDiscSender;
import game.networking.util.Protocol;
import game.networking.util.TraceLog;
import game.networking.util.UtilityCode;

public class ClientDiscovery
{
	private String name;
	private DatagramSocket socket;

	private int tcpPort;
	private int udpPort;
	private InetAddress serverAddress;

	public ClientDiscovery(String _name, DatagramSocket _udpSocket)
	{
		name = _name;
		socket = _udpSocket;
	}

	public boolean isAccepted()
	{

		boolean accepted = false;
		ClientDiscSender clientDiscSender = new ClientDiscSender(name, socket);
		(new Thread(clientDiscSender)).start();
		DatagramSocket c = socket;
		try
		{

			boolean wait = true;
			// Wait for a response
			while (wait)
			{
				// we no longer need to wait
				wait = false;

				byte[] recvBuf = new byte[20000];
				DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				c.receive(receivePacket);

				// We have a response
				TraceLog.consoleLog(getClass().getName() + name + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

				// Check if the message is correct
				String OiginalMessage = new String(receivePacket.getData()).trim();
				String message = OiginalMessage;

				TraceLog.consoleLog(getClass().getName() + name + ">>> SERVER MESSAGE: " + message);

				// if message is destined to me deocde it
				// FIXME: this is here momentarily
				// if (message.substring(message.length() -
				// name.length()).equals(name))

				if (!message.contains(Protocol.TCPSocketTag))
				{
					if (message.substring(message.length() - name.length()).equals(name))
					{
						int namePos = message.lastIndexOf(name);
						// System.out.println(message.substring(0, namePos));
						message = message.substring(0, namePos);
					}
				} else
				{
					String receivedName = message.substring(message.indexOf(Protocol.TCPSocketTag) - name.length(), message.indexOf(Protocol.TCPSocketTag));
					if (receivedName.equals(name))
					{
						message = message.substring(0, message.indexOf(Protocol.TCPSocketTag) - name.length());
					}
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
					String string = OiginalMessage.substring(OiginalMessage.indexOf(Protocol.TCPSocketTag) + Protocol.TCPSocketTag.length());

					int port = Integer.parseInt(string);
					udpPort = receivePacket.getPort();
					tcpPort = port;
					serverAddress = receivePacket.getAddress();

					accepted = true;
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

					accepted = false;
					break;
				}

				default:
				{
					// bad message => still wait
					wait = true;
					break;
				}
				}
			}
		} catch (IOException e)
		{
			accepted = false;
		}
		while (!clientDiscSender.done())
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return accepted;
	}

	public int getTCPport()
	{
		return tcpPort;
	}

	public int getUDPport()
	{
		return udpPort;
	}

	public InetAddress getServerAddress()
	{
		return serverAddress;
	}

}

package game.networking.server.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import game.networking.server.ServerTest;
import game.networking.util.Connection;
import game.networking.util.Protocol;
import game.networking.util.ServerMainable;
import game.networking.util.TraceLog;

public class DiscoveryThread implements Runnable
{
	private boolean run = true;

	private int socketInt;
	private Map<String, Connection> clients;
	private List<String> acceptedClients;
	private ServerMainable server;

	public DiscoveryThread(int _socketInt, Map<String, Connection> _clients, ServerMainable _server, List<String> _acceptedClients)
	{
		socketInt = _socketInt;
		clients = _clients;
		server = _server;
		acceptedClients = _acceptedClients;
	}

	@Override
	public void run()
	{
		String name = "";
		DatagramSocket socket;
		try
		{
			// Keep a socket open to listen to all the UDP trafic that is
			// destined for this port
			socket = new DatagramSocket(socketInt, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (run)
			{
				TraceLog.consoleLog(getClass().getName() + ">>>Ready to receive broadcast packets!");

				// Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				// Packet received
				TraceLog.consoleLog(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
				TraceLog.consoleLog(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

				// See if the packet holds the right command (message)

				String message = new String(packet.getData()).trim();

				if (message.startsWith(Protocol.CtoS_Discovery))
				{
					name = message.substring(Protocol.CtoS_Discovery.length());
					message = Protocol.CtoS_Discovery;
				}
				if (message.equals(Protocol.CtoS_Discovery))
				{

					// see if we already have a client that has registered with
					// that name;

					if (acceptedClients.contains(name))
					{
						// reject if we have another client with the same name

						sendData(socket, (Protocol.StoC_DiscoveryReject + name), packet.getAddress(), packet.getPort());

					} else
					{
						// accept and add client to table if he has a unique
						// name
						if (!clients.containsKey(name))
						{
							// send wait request to client

							sendData(socket, (Protocol.StoC_DiscoveryWait + name), packet.getAddress(), packet.getPort());

							// thread safe way of putting stuff into the map
							synchronized (this)
							{
								Connection connection = new Connection(packet.getAddress(), packet.getPort());
								clients.put(name, connection);
								server.acceptClientConnection(name);

							}
						} else if (!processingClient(name, packet))
						{
							// if the client is already online deny access
							sendData(socket, (Protocol.StoC_DiscoveryReject + name), packet.getAddress(), packet.getPort());

						}

					}

				}
			}
			socket.close();
		} catch (IOException ex)
		{
			Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void sendData(DatagramSocket socket, String data, InetAddress address, int port) throws IOException
	{
		byte[] sendData = (data).getBytes();
		// Send a response
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
		socket.send(sendPacket);
		TraceLog.consoleLog(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
	}

	private boolean processingClient(String name, DatagramPacket packet)
	{
		Connection con = clients.get(name);
		if (con.address.equals(packet.getAddress()) && (con.port == packet.getPort()))
			return true;
		else
			return false;
	}

}

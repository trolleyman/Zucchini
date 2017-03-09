package game.networking.client.threads.udp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

import game.networking.util.UtilityCode;

public class UDPConnectionClient
{
	private int portSender;
	private int portReceiver;
	private UDPSenderClient sender;
	private UDPListenerClient receiver;
	private InetAddress server;

	public UDPConnectionClient(String _name, InetAddress _address, LinkedList<String> _udpToServer, LinkedList<String> _udpFromServer)
	{

		DatagramSocket datagramSocket;
		server = _address;
		try
		{
			portSender = UtilityCode.getNextAvailabePort();
			if (portSender != -1)
			{
				System.out.println("client sender port:" + portSender);
				datagramSocket = new DatagramSocket(portSender, InetAddress.getByName("0.0.0.0"));
				sender = new UDPSenderClient(_name, datagramSocket, _udpToServer);
			}
			portReceiver = UtilityCode.getNextAvailabePort();
			if (portReceiver != -1)
			{
				System.out.println("client receiver port:" + portReceiver);
				datagramSocket = new DatagramSocket(portReceiver, InetAddress.getByName("0.0.0.0"));
				receiver = new UDPListenerClient(datagramSocket, _udpFromServer);
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Start()
	{
		sender.start();
		receiver.start();
	}

	public synchronized int getReceivePort()
	{
		return portReceiver;
	}

	public synchronized int getSendPort()
	{
		return portSender;
	}

	public void setUDPports(int sendPort, int receivePort)
	{
		sender.setServerPort(sendPort, server);
		receiver.setServerPort(receivePort, server);

	}
}

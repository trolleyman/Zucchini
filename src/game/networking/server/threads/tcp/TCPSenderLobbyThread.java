package game.networking.server.threads.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import game.networking.util.Protocol;
import game.networking.util.Tuple;

public class TCPSenderLobbyThread implements Runnable
{

	private Socket socket;
	private String name;
	LinkedList<Tuple<String, String>> messages;

	public TCPSenderLobbyThread(Socket _socket, String _name, LinkedList<Tuple<String, String>> _messages)
	{
		socket = _socket;
		name = _name;
		messages = _messages;

		// SEND FIRST PING
		DataOutputStream toClient;
		try
		{
			toClient = new DataOutputStream(socket.getOutputStream());
			toClient.writeBytes(Protocol.TCP_Ping + name + "\n");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run()
	{
		boolean run = true;
		try
		{
			DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());

			while (run)
			{
				synchronized (messages)
				{

					while (!messages.isEmpty())
					{
						Tuple<String, String> t = messages.poll();
						try
						{
							toClient.writeBytes(Protocol.TCP_Message + t.getFirst() + ": " + t.getSecond() + "\n");
							System.out.println(name + " should receive:  " + Protocol.TCP_Message + t.getFirst() + ": " + t.getSecond());
						} catch (IOException e)
						{
							run = false;
						}
					}
				}

				try
				{
					Thread.sleep(100);
					try
					{
						toClient.writeBytes(Protocol.TCP_Ping + name + "\n");
					} catch (IOException e)
					{
						run = false;
					}
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println(this.getClass().getName() + name + ">>disconected!!");

	}

}

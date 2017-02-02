package game.networking.server.threads;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import game.networking.util.Protocol;
import game.networking.util.Touple;

public class TCPSenderLobbyThread implements Runnable
{

	private Socket socket;
	private String name;
	LinkedList<Touple<String, String>> messages;

	public TCPSenderLobbyThread(Socket _socket, String _name, LinkedList<Touple<String, String>> _messages)
	{
		socket = _socket;
		name = _name;
		messages = _messages;
	}

	@Override
	public void run()
	{
		boolean run = true;

		try
		{
			DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());
			toClient.writeBytes(Protocol.TCP_Ping + name + "\n");

			while (run)
			{
				synchronized (messages)
				{

					while (!messages.isEmpty())
					{
						Touple<String, String> t = messages.poll();
						toClient.writeBytes(Protocol.TCP_Message + t.getFirst() + ": " + t.getSecond() + "\n");
						System.out.println(name + " should receive:  " + Protocol.TCP_Message + t.getFirst() + ": " + t.getSecond());
					}
				}
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(name + " disconected!!");
		}

	}

}

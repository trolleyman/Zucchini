package game.networking.server.threads.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import game.networking.server.threads.LobbyThread;

public class TCPConThread implements Runnable
{
	private ServerSocket serverSocket;
	private Map<String, Socket> clientSockets;
	private LobbyThread lobby;
	private List<String> updatedList;

	public TCPConThread(Map<String, Socket> _clientSockets, LobbyThread _lobby, List<String> _updatedList)
	{

		clientSockets = _clientSockets;
		lobby = _lobby;
		updatedList = _updatedList;

		try
		{
			serverSocket = new ServerSocket(0);
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
		while (run)
		{
			try
			{
				// System.out.println("runTCP");

				Socket socket = serverSocket.accept();
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String name = fromClient.readLine();
				synchronized (updatedList)
				{
					clientSockets.put(name, socket);
					updatedList.add(name);
					lobby.newClient();

					// System.out.println("-------------------------" + name);
				}

			} catch (IOException e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}

		}

		// program is done
		// close all sockets

		synchronized (clientSockets)
		{
			for (Socket socket : clientSockets.values())
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try
		{
			serverSocket.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized int getTCPServerPort()
	{
		return serverSocket.getLocalPort();
	}

}

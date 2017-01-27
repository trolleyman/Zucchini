package networking.server.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class TCPConLobbyThread implements Runnable
{
	private ServerSocket serverSocke;
	private Map<String, Socket> clientSockets;
	private LobbyThread lobby;
	private List<String> updatedList;

	public TCPConLobbyThread(int _socketInt, Map<String, Socket> _clientSockets, LobbyThread _lobby, List<String> _updatedList)
	{

		clientSockets = _clientSockets;
		lobby = _lobby;
		updatedList = _updatedList;
		try
		{
			serverSocke = new ServerSocket(_socketInt + 1);
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
				Socket socket = serverSocke.accept();
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String name = fromClient.readLine();
				synchronized (updatedList)
				{
					clientSockets.put(name, socket);
					lobby.newClient();
					updatedList.add(name);
					System.out.println("-------------------------" + name);
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
			serverSocke.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

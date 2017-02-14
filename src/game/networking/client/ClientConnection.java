package game.networking.client;

import game.LobbyInfo;
import game.action.Action;
import game.net.DummyClientConnectionHandler;
import game.net.IClientConnection;
import game.net.IClientConnectionHandler;
import game.net.LobbyCallback;
import game.networking.client.threads.tcp.TCPListenerClient;
import game.networking.client.threads.tcp.TCPSenderClient;
import game.networking.util.Protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ClientConnection implements Runnable, IClientConnection
{
	private IClientConnectionHandler cch = new DummyClientConnectionHandler();
	
	String name;
	boolean success = false;

	LinkedList<String> toServer;
	LinkedList<String> fromServer;
	
	TCPListenerClient listener;
	TCPSenderClient sender;
	
	private ArrayList<LobbyCallback> lobbyCallbacks = new ArrayList<>();

	public ClientConnection(String _name)
	{
		name = _name;
		toServer = new LinkedList<>();
		fromServer = new LinkedList<>();
		
		Thread t = new Thread(this);
		t.setName("Connection Handler Thread");
		t.start();
	}

	@Override
	public void run()
	{
		DatagramSocket socket = null;
		Socket tcpSocket = null;
		// FAST: create TCP connection for client
		try
		{
			socket = new DatagramSocket();
			ClientDiscovery clientDiscovery = new ClientDiscovery(name, socket);
			if (clientDiscovery.isAccepted())
			{
				System.out.println("---------------------------------" + name + ": Success!!");
				try
				{
					tcpSocket = new Socket(clientDiscovery.getServerAddress(), clientDiscovery.getTCPport());
					DataOutputStream toServer = new DataOutputStream(tcpSocket.getOutputStream());
					toServer.writeBytes(name + "\n");
					success = true;
				} catch (IOException e)
				{
					success = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				System.out.println("---------------------------------" + name + ": fail!!");
				success = false;
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (success)
		{

			listener = new TCPListenerClient(tcpSocket, fromServer);
			sender = new TCPSenderClient(tcpSocket, toServer);

			(new Thread(listener)).start();
			(new Thread(sender)).start();
		}
		socket.close();
		
//		while (true) {
//			// handle incoming messages
//			synchronized (fromServer) {
//				try {
//					fromServer.wait();
//				} catch (InterruptedException e) {
//					// This is fine
//				}
//				for (String message : fromServer)
//					this.handleMessage(message);
//				fromServer.clear();
//			}
//		}
	}

	public LinkedList<String> getToServerOutput()
	{
		return toServer;
	}

	public LinkedList<String> getFromServerOutput()
	{
		return fromServer;
	}
	
	private void handleMessage(String message) {
		if (Protocol.isEntityUpdate(message)) {
			cch.updateEntity(Protocol.parseEntityUpdate(message));
		} else if (Protocol.isRemoveEntity(message)) {
			cch.removeEntity(Protocol.parseRemoveEntity(message));
		} else if (Protocol.isAudioEvent(message)) {
			cch.processAudioEvent(Protocol.parseAudioEvent(message));
		} else if (Protocol.isLobbiesReply(message)) {
			ArrayList<LobbyInfo> lobbies = Protocol.parseLobbiesReply(message);
			synchronized (this.lobbyCallbacks) {
				for (LobbyCallback cb : this.lobbyCallbacks)
					cb.success(lobbies);
				this.lobbyCallbacks.clear();
			}
		} else {
			System.err.println("Warning: Unknown message received: " + message);
		}
	}
	
	@Override
	public void setHandler(IClientConnectionHandler _cch) {
		this.cch = cch;
	}
	
	@Override
	public void sendAction(Action a) {
		synchronized (toServer) {
			toServer.add(Protocol.sendAction(a));
			toServer.notifyAll();
		}
	}
	
	@Override
	public void getLobbies(LobbyCallback cb) {
		synchronized (this.lobbyCallbacks) {
			this.lobbyCallbacks.add(cb);
		}
		synchronized (toServer) {
			toServer.add(Protocol.sendLobbiesRequest());
			toServer.notifyAll();
		}
	}
	
	@Override
	public void close() {
		listener.close();
		sender.close();
	}
}

package game.net.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.function.Consumer;

import game.LobbyInfo;
import game.action.Action;
import game.audio.event.AudioEvent;
import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.Protocol;
import game.net.TCPConnection;
import game.net.UDPConnection;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

public class ClientConnection implements IClientConnection {
	private String name;
	
	private UDPConnection udpConn;
	private TCPConnection tcpConn;
	
	private volatile boolean running = true;
	private Thread udpHandler;
	private Thread tcpHandler;
	
	private final Object cchLock = new Object();
	private IClientConnectionHandler cch = new DummyClientConnectionHandler();
	
	private final Object lobbiesLock = new Object();
	private ArrayList<Consumer<ArrayList<LobbyInfo>>> lobbiesSuccessCallbacks;
	private ArrayList<Consumer<String>> lobbiesErrorCallbacks;
	
	public ClientConnection(String _name) throws ProtocolException, NameException {
		this.name = _name;
		
		ClientDiscovery clientDiscovery = new ClientDiscovery(name);
		clientDiscovery.tryDiscover();
		udpConn = clientDiscovery.getUDP();
		tcpConn = clientDiscovery.getTCP();
		
		udpHandler = new Thread(this::runUdpHandler, "ClientConnection UDP Handler");
		udpHandler.start();
		
		tcpHandler = new Thread(this::runTcpHandler, "ClientConnection TCP Handler");
		tcpHandler.start();
	}
	
	private void outUDP(String msg) {
		System.out.println("[Net UDP] " + name + ": " + msg);
	}
	private void outTCP(String msg) {
		System.out.println("[Net TCP] " + name + ": " + msg);
	}
	
	private void runUdpHandler() {
		while (running) {
			try {
				String msg = udpConn.recvString();
				outUDP("Message received: " + msg);
			} catch (ProtocolException e) {
				outUDP("Exception encountered:");
				e.printStackTrace();
				this.close();
			}
		}
	}
	
	private void runTcpHandler() {
		while (running) {
			try {
				// Recv msg
				String msg = tcpConn.recvString();
				
				// Parse message
				if (Protocol.isAddEntity(msg)) {
					Entity e = Protocol.parseAddEntity(msg);
					synchronized (cchLock) {
						cch.addEntity(e);
					}
				} else if (Protocol.isUpdateEntity(msg)) {
					EntityUpdate eu = Protocol.parseUpdateEntity(msg);
					synchronized (cchLock) {
						cch.updateEntity(eu);
					}
				} else if (Protocol.isRemoveEntity(msg)) {
					int id = Protocol.parseRemoveEntity(msg);
					synchronized (cchLock) {
						cch.removeEntity(id);
					}
				} else if (Protocol.isAudioEvent(msg)) {
					AudioEvent ae = Protocol.parseAudioEvent(msg);
					synchronized (cchLock) {
						cch.processAudioEvent(ae);
					}
				} else if (Protocol.isLobbiesReply(msg)) {
					try {
						ArrayList<LobbyInfo> lobbies = Protocol.parseLobbiesReply(msg);
						synchronized (lobbiesLock) {
							if (lobbiesSuccessCallbacks.size() == 0) {
								System.err.println("Warning: LobbiesReply received, but no handlers assigned.");
							}
							
							for (Consumer<ArrayList<LobbyInfo>> cb : lobbiesSuccessCallbacks) {
								cb.accept(new ArrayList<>(lobbies));
							}
							
							lobbiesSuccessCallbacks.clear();
							lobbiesErrorCallbacks.clear();
						}
					} catch (ProtocolException e) {
						// If there is an error parsing the reply, pass that onto the error callbacks.
						synchronized (lobbiesLock) {
							if (lobbiesErrorCallbacks.size() == 0) {
								System.err.println("Warning: LobbiesReply errored, but no handlers assigned.");
							}
							
							for (Consumer<String> cb : lobbiesErrorCallbacks) {
								cb.accept(e.toString());
							}
							
							lobbiesSuccessCallbacks.clear();
							lobbiesErrorCallbacks.clear();
						}
					}
				} else {
					System.err.println("Warning: Unknown message received: " + msg);
				}
			} catch (ProtocolException e) {
				outTCP("Exception encountered:");
				e.printStackTrace();
				this.close();
			}
		}
	}
	
	@Override
	public void sendAction(Action a) throws ProtocolException {
		tcpConn.sendString(Protocol.sendAction(a));
	}
	
	@Override
	public void requestFullUpdate() throws ProtocolException {
		tcpConn.sendString(Protocol.sendFullUpdateRequest());
	}
	
	@Override
	public void setHandler(IClientConnectionHandler cch) {
		synchronized (cchLock) {
			this.cch = cch;
		}
	}
	
	@Override
	public void getLobbies(Consumer<ArrayList<LobbyInfo>> successCallback, Consumer<String> errorCallback) {
		synchronized (lobbiesLock) {
			lobbiesSuccessCallbacks.add(successCallback);
			lobbiesErrorCallbacks.add(errorCallback);
			
			// LobbiesRequest has already been sent, just return;
			if (lobbiesSuccessCallbacks.size() > 1 && lobbiesErrorCallbacks.size() > 1) {
				return;
			}
			
			// Try to actually send LobbiesRequest
			try {
				tcpConn.sendString(Protocol.sendLobbiesRequest());
			} catch (ProtocolException e) {
				// Call error callbacks
				for (Consumer<String> cb : lobbiesErrorCallbacks)
					cb.accept(e.toString());
				
				// Clear callbacks
				lobbiesSuccessCallbacks.clear();
				lobbiesErrorCallbacks.clear();
			}
		}
	}
	
	@Override
	public void close() {
		tcpConn.close();
		udpConn.close();
		this.running = false;
	}
	
	@Override
	public boolean isClosed() {
		return !this.running;
	}
}

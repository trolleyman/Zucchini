package game.net.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.function.Consumer;

import game.LobbyInfo;
import game.action.Action;
import game.audio.event.AudioEvent;
import game.exception.NameException;
import game.exception.ProtocolException;
import game.net.*;
import game.world.entity.Entity;
import game.world.entity.update.EntityUpdate;
import game.world.update.WorldUpdate;

public class ClientConnection implements IClientConnection {
	private String name;
	
	private UDPConnection udpConn;
	private TCPConnection tcpConn;
	
	private volatile boolean running = true;
	private Thread udpHandler;
	private Thread tcpHandler;
	
	private final Object cchLock = new Object();
	private IClientConnectionHandler cch = new IClientConnectionHandler() {};
	
	private final Object lobbiesLock = new Object();
	private ArrayList<Consumer<ArrayList<LobbyInfo>>> lobbiesSuccessCallbacks = new ArrayList<>();
	private ArrayList<Consumer<String>> lobbiesErrorCallbacks = new ArrayList<>();
	
	public ClientConnection(String _name) throws ProtocolException, NameException {
		this(_name, -1);
	}
	
	/**
	 * Autoconnect to the server {@code tries} times. If the number of tries is exceeded, throws a ProtocolException
	 */
	public ClientConnection(String _name, int tries) throws ProtocolException, NameException {
		this.name = _name;
		
		ClientDiscovery clientDiscovery = new ClientDiscovery(name);
		clientDiscovery.tryDiscover(tries);
		udpConn = clientDiscovery.getUDP();
		tcpConn = clientDiscovery.getTCP();
		
		udpHandler = new Thread(this::runUdpHandler, "ClientConnection UDP Handler");
		udpHandler.start();
		
		tcpHandler = new Thread(this::runTcpHandler, "ClientConnection TCP Handler");
		tcpHandler.start();
	}
	
	public ClientConnection(String name, InetAddress addr) throws ProtocolException, NameException {
		this.name = name;
		
		udpConn = new UDPConnection();
		udpConn.connect(new InetSocketAddress(addr, Protocol.UDP_SERVER_PORT));
		tcpConn = new TCPConnection(addr, Protocol.TCP_SERVER_PORT);
		
		// Now to actually connect - send request with local port to connect to.
		tcpConn.sendConnectionRequest(name, udpConn.getSocket().getLocalPort());
		
		// Recieve response
		try {
			tcpConn.recvConnectionResponse();
		} catch (NameException e) {
			// Name is already in use / name is invalid
			throw e;
		}
		
		udpHandler = new Thread(this::runUdpHandler, "ClientConnection UDP Handler");
		udpHandler.start();
		
		tcpHandler = new Thread(this::runTcpHandler, "ClientConnection TCP Handler");
		tcpHandler.start();
	}
	
	private void runUdpHandler() {
		while (running) {
			try {
				String msg = udpConn.recvString();
				
				if (Protocol.isUpdateEntity(msg)) {
					EntityUpdate eu = Protocol.parseUpdateEntity(msg);
					synchronized (cchLock) {
						cch.updateEntity(eu);
					}
				} else {
					System.err.println("[UDP]: " + name + " Warning: Unknown message received: " + msg);
				}
			} catch (ProtocolException e) {
				System.err.println("[UDP]: " + name + ": Exception encountered:");
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
				} else if (Protocol.isLobbiesResponse(msg)) {
					try {
						ArrayList<LobbyInfo> lobbies = Protocol.parseLobbiesResponse(msg);
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
				} else if (Protocol.isLobbyJoinAccept(msg)) {
					synchronized (cchLock) {
						cch.handleLobbyJoinAccept();
					}
				} else if (Protocol.isLobbyJoinReject(msg)) {
					synchronized (cchLock) {
						cch.handleLobbyJoinReject(Protocol.parseLobbyJoinReject(msg));
					}
				} else if (Protocol.isLobbyUpdate(msg)) {
					LobbyInfo info = Protocol.parseLobbyUpdate(msg);
					synchronized (cchLock) {
						cch.processLobbyUpdate(info);
					}
				} else if (Protocol.isWorldStart(msg)) {
					WorldStart start = Protocol.parseWorldStart(msg);
					synchronized (cchLock) {
						cch.handleWorldStart(start);
					}
				} else if (Protocol.isLobbyLeaveNotify(msg)) {
					synchronized (cchLock) {
						cch.handleLobbyLeaveNotify();
					}
				} else if (Protocol.isLobbyCreateAccept(msg)) {
					synchronized (cchLock) {
						cch.handleLobbyCreateAccept();
					}
				} else if (Protocol.isLobbyCreateReject(msg)) {
					String reason = Protocol.parseLobbyCreateReject(msg);
					synchronized (cchLock) {
						cch.handleLobbyCreateReject(reason);
					}
				} else if (Protocol.isWorldUpdate(msg)) {
					WorldUpdate update = Protocol.parseWorldUpdate(msg);
					synchronized (cchLock) {
						cch.handleWorldUpdate(update);
					}
				} else if (Protocol.isMessageToClient(msg)) {
					Tuple<String, String> t = Protocol.parseMessageToClient(msg);
					String name = t.getFirst();
					String cmsg = t.getSecond();
					synchronized (cchLock) {
						cch.handleMessage(name, cmsg);
					}
				} else {
					System.err.println("[TCP]: " + name + ": Warning: Unknown message received: " + msg);
				}
			} catch (ProtocolException e) {
				System.err.println("[TCP]: " + name + ": Exception encountered:");
				e.printStackTrace();
				this.close();
			}
		}
	}
	
	@Override
	public void sendAction(Action a) throws ProtocolException {
		udpConn.sendString(Protocol.sendAction(a));
	}
	
	@Override
	public void requestFullUpdate() throws ProtocolException {
		tcpConn.sendString(Protocol.sendFullUpdateRequest());
	}
	
	@Override
	public void sendLobbyJoinRequest(String lobbyName) throws ProtocolException {
		tcpConn.sendString(Protocol.sendLobbyJoinRequest(lobbyName));
	}
	
	@Override
	public void sendToggleReady() throws ProtocolException {
		tcpConn.sendString(Protocol.sendReadyToggle());
	}
	
	@Override
	public void sendLobbyLeaveRequest() throws ProtocolException {
		tcpConn.sendString(Protocol.sendLobbyLeaveRequest());
	}
	
	@Override
	public void sendLobbyCreateRequest(LobbyInfo lobby) throws ProtocolException {
		tcpConn.sendString(Protocol.sendLobbyCreateRequest(lobby));
	}
	
	@Override
	public String getName() {
		return name;
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
	public void sendStringTcp(String msg) throws ProtocolException {
		tcpConn.sendString(msg);
	}
	
	@Override
	public void sendStringUdp(String msg) throws ProtocolException {
		udpConn.sendString(msg);
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

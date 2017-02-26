package game.net.client;

import java.net.InetAddress;

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
	
	private InetAddress serverAddress;
	private int serverUdpPort;
	
	private volatile boolean running = true;
	private Thread udpHandler;
	private Thread tcpHandler;
	
	private final Object cchLock = new Object();
	private IClientConnectionHandler cch = new DummyClientConnectionHandler();
	
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
		System.out.println("[net_udp] " + name + ":" + msg);
	}
	private void outTCP(String msg) {
		System.out.println("[net_tcp] " + name + ":" + msg);
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

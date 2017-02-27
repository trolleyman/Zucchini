package game.net.server;

import game.exception.ProtocolException;

import java.util.function.Consumer;

/**
 * Handles a TCP + UDP connection to the client.
 */
public class ClientHandler {
	private final ClientInfo info;
	
	private Consumer<String> tcpCallback = null;
	private Consumer<String> udpCallback = null;
	private Consumer<ProtocolException> errorCallback = null;
	private Runnable closeCallback = null;
	
	private volatile boolean closed = false;
	
	public ClientHandler(ClientInfo info) {
		this.info = info;
	}
	
	public ClientInfo getClientInfo() {
		return info;
	}
	
	public void start() {
		Thread tcpHandler = new Thread(this::runTcpHandler, "TCP ClientHandler: "
				+ info.tcpConn.getSocket().getRemoteSocketAddress());
		tcpHandler.start();
		
		Thread udpHandler = new Thread(this::runUdpHandler, "UDP ClientHandler: "
				+ info.udpConn.getSocket().getRemoteSocketAddress());
		udpHandler.start();
	}
	
	private void runTcpHandler() {
		while (!closed) {
			try {
				// Wait for message
				String msg = info.tcpConn.recvString();
				
				synchronized (this) {
					this.tcpCallback.accept(msg);
				}
			} catch (ProtocolException e) {
				this.error(e);
			}
		}
	}
	
	public void sendStringTcp(String msg) throws ProtocolException {
		if (closed)
			throw new ProtocolException("ClientHandler closed");
		
		try {
			info.tcpConn.sendString(msg);
		} catch (ProtocolException e) {
			this.error(e);
			throw e;
		}
	}
	
	private void runUdpHandler() {
		while (!closed) {
			try {
				// Wait for message
				String msg = info.udpConn.recvString();
				
				synchronized (this) {
					this.udpCallback.accept(msg);
				}
			} catch (ProtocolException e) {
				this.error(e);
			}
		}
	}
	
	public void sendStringUdp(String msg) throws ProtocolException {
		if (closed)
			throw new ProtocolException("ClientHandler closed");
		
		try {
			info.udpConn.sendString(msg);
		} catch (ProtocolException e) {
			this.error(e);
			throw e;
		}
	}
	
	private void error(ProtocolException e) {
		synchronized (this) {
			closed = true;
			info.tcpConn.close();
			info.udpConn.close();
			this.errorCallback.accept(e);
			this.close();
		}
	}
	
	private void close() {
		synchronized (this) {
			closed = true;
			info.tcpConn.close();
			info.udpConn.close();
			this.closeCallback.run();
		}
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void onTcpMessage(Consumer<String> cb) {
		synchronized (this) {
			this.tcpCallback = cb;
		}
	}
	
	public void onUdpMessage(Consumer<String> cb) {
		synchronized (this) {
			this.udpCallback = cb;
		}
	}
	
	public void onError(Consumer<ProtocolException> cb) {
		synchronized (this) {
			this.errorCallback = cb;
		}
	}
	
	public void onClose(Runnable cb) {
		synchronized (this) {
			this.closeCallback = cb;
		}
	}
}

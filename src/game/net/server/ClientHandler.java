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
	
	private boolean isDebugPrint = false;
	
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
				+ info.udpConn.getSocketAddress());
		udpHandler.start();
	}
	
	private void runTcpHandler() {
		while (!closed) {
			try {
				// Wait for message
				String msg = info.tcpConn.recvString();
				if (isDebugPrint)
					System.out.println("[TCP]: RECV " + info.tcpConn.getSocket().getRemoteSocketAddress() + ": " + msg);
				
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
			if (isDebugPrint)
				System.out.println("[TCP]: SEND " + info.tcpConn.getSocket().getRemoteSocketAddress() + ": " + msg);
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
				if (isDebugPrint)
					System.out.println("[UDP]: RECV " + info.udpConn.getSocketAddress() + ": " + msg);
				
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
			if (isDebugPrint)
				System.out.println("[UDP]: SEND " + info.udpConn.getSocketAddress() + ": " + msg);
		} catch (ProtocolException e) {
			this.error(e);
			throw e;
		}
	}
	
	public void error(ProtocolException e) {
		synchronized (this) {
			if (isClosed())
				return;
			if (isDebugPrint)
				System.err.println("[Net]: ERROR " + info.tcpConn.getSocket().getRemoteSocketAddress() + ": " + e.toString());
			this.errorCallback.accept(e);
			this.close();
		}
	}
	
	public void close() {
		synchronized (this) {
			if (isClosed())
				return;
			if (isDebugPrint)
				System.out.println("[Net]: CLOSE " + info.tcpConn.getSocket().getRemoteSocketAddress() + " + " + info.udpConn.getSocketAddress());
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

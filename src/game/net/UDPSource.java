package game.net;

import game.exception.ProtocolException;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.function.Consumer;

public class UDPSource {
	
	private UDPConnection conn;
	
	private boolean running;
	
	private final Object cbLock = new Object();
	private HashMap<SocketAddress, Consumer<String>> msgCallbacks = new HashMap<>();
	private HashMap<SocketAddress, Consumer<ProtocolException>> errorCallbacks = new HashMap<>();
	
	public UDPSource(UDPConnection _conn) {
		this.conn = _conn;
		
		Thread t = new Thread(this::runHandler, "UDPSource Handler");
		t.start();
	}
	
	private void runHandler() {
		running = true;
		
		while (running) {
			DatagramPacket packet;
			try {
				packet = conn.recv();
			} catch (ProtocolException e) {
				// Close source - fatal error
				this.error(e);
				break;
			}
			
			SocketAddress address = packet.getSocketAddress();
			try {
				String msg = conn.decode(packet);
				
				synchronized (cbLock) {
					Consumer<String> cb = this.msgCallbacks.get(address);
					if (cb == null)
						System.err.println("Warning: No handler for " + address);
					else
						cb.accept(msg);
				}
			} catch (ProtocolException e) {
				// Non-fatal error - report to callback and proceed
				synchronized (cbLock) {
					Consumer<ProtocolException> cb = this.errorCallbacks.get(address);
					if (cb == null)
						System.err.println("Warning: No handler for " + address);
					else
						cb.accept(e);
				}
			}
		}
	}
	
	public void setListener(SocketAddress address, Consumer<String> msgCallback, Consumer<ProtocolException> errorCallback) {
		synchronized (cbLock) {
			msgCallbacks.put(address, msgCallback);
			errorCallbacks.put(address, errorCallback);
		}
	}
	
	public void unsetListener(SocketAddress address) {
		synchronized (cbLock) {
			msgCallbacks.remove(address);
			errorCallbacks.remove(address);
		}
	}
	
	public void sendString(String msg, SocketAddress address) throws ProtocolException {
		conn.sendString(msg, address);
	}
	
	public void error(ProtocolException e) {
		synchronized (cbLock) {
			for (Consumer<ProtocolException> cb : errorCallbacks.values())
				cb.accept(e);
			close();
		}
	}
	
	public void close() {
		running = false;
		conn.close();
	}
}

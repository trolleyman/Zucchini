package game.net;

import game.exception.ProtocolException;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;

public class UDPRelay {
	private UDPConnection conn;
	
	private HashMap<SocketAddress, LinkedList<String>> messages;
	
	private final Object recvLock = new Object();
	
	private ProtocolException error = null;
	
	public UDPRelay(UDPConnection conn) {
		this.conn = conn;
		
		Thread t = new Thread(this::runHandler, "UDPRelay Handler");
		t.start();
	}
	
	private void runHandler() {
		while (true) {
			try {
				// Receive packet
				DatagramPacket packet = conn.recv();
				String msg = conn.decode(packet);
				SocketAddress from = packet.getSocketAddress();
				
				// Add to queue
				synchronized (recvLock) {
					messages.computeIfAbsent(from, k -> new LinkedList<>());
					
					messages.get(from).push(msg);
					
					// Notify receivers
					recvLock.notifyAll();
				}
			} catch (ProtocolException e) {
				error = e;
				return;
			}
		}
	}
	
	public void sendString(String msg, SocketAddress address) throws ProtocolException {
		if (error != null)
			throw error;
		conn.sendString(msg, address);
	}
	
	public String recvFrom(SocketAddress from) throws ProtocolException {
		while (true) {
			if (error != null)
				throw error;
			
			synchronized (recvLock) {
				messages.computeIfAbsent(from, k -> new LinkedList<>());
				
				LinkedList<String> ll = messages.get(from);
				if (!ll.isEmpty()) {
					return ll.pollLast();
				}
				
				try {
					recvLock.wait();
				} catch (InterruptedException e) {
					// Ignore
				}
			}
		}
	}
}

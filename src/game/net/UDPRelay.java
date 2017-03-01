package game.net;

import game.exception.ProtocolException;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class UDPRelay {
	private final Object recvLock = new Object();
	
	private UDPSource source;
	private SocketAddress address;
	
	private ProtocolException error;
	
	private LinkedList<String> messages = new LinkedList<>();
	
	public UDPRelay(UDPSource _source, SocketAddress _address) {
		this.source = _source;
		this.address = _address;
		
		source.setListener(address, this::onMessage, this::onError);
	}
	
	private void onMessage(String msg) {
		synchronized (recvLock) {
			this.messages.push(msg);
			recvLock.notifyAll();
		}
	}
	
	private void onError(ProtocolException error) {
		synchronized (recvLock) {
			this.error = error;
			recvLock.notifyAll();
			source.unsetListener(address);
		}
	}
	
	public void close() {
		synchronized (recvLock) {
			this.onError(new ProtocolException("UDP Socket Closed"));
		}
	}
	
	public String recvString() throws ProtocolException {
		while (true) {
			synchronized (recvLock) {
				if (error != null) {
					// Error
					throw new ProtocolException(error);
				} else if (messages.size() > 0) {
					return messages.pollLast();
				}
				
				try {
					recvLock.wait();
				} catch (InterruptedException e) {
					// This is fine
				}
			}
		}
	}
	
	public void sendString(String msg) throws ProtocolException {
		source.sendString(msg, address);
	}
	
	public SocketAddress getSocketAddress() {
		return address;
	}
}

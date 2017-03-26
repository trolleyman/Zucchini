package game.net;

import game.exception.ProtocolException;
import game.net.client.IClientConnection;
import game.world.ServerWorldClient;

import java.util.ArrayList;

public class PacketCache {
	private ArrayList<String> tcpCache = new ArrayList<>();
	private ArrayList<String> udpCache = new ArrayList<>();
	
	private ArrayList<Pair<String, String>> tcpToCache = new ArrayList<>();
	
	public synchronized void sendStringTcp(String msg) {
		tcpCache.add(msg);
	}
	
	public synchronized void sendStringUdp(String msg) {
		udpCache.add(msg);
	}
	
	public synchronized void processCache(IClientConnection conn) throws ProtocolException {
		try {
			for (String msg : tcpCache) {
				conn.sendStringTcp(msg);
			}
			for (String msg : udpCache) {
				conn.sendStringUdp(msg);
			}
		} catch (ProtocolException e) {
			conn.error(e);
			throw e;
		} finally {
			tcpCache.clear();
			udpCache.clear();
		}
	}
	
	public synchronized void processCache(ArrayList<ServerWorldClient> clients) {
		for (ServerWorldClient swc : clients) {
			try {
				for (String msg : tcpCache) {
					swc.handler.sendStringTcp(msg);
				}
				for (String msg : udpCache) {
					swc.handler.sendStringUdp(msg);
				}
			} catch (ProtocolException e) {
				swc.handler.error(e);
			}
		}
		tcpCache.clear();
		udpCache.clear();
		
		for (Pair<String, String> t : tcpToCache) {
			String name = t.getFirst();
			String msg = t.getSecond();
			for (ServerWorldClient swc : clients) {
				if (swc.handler.getClientInfo().name.equals(name)) {
					try {
						swc.handler.sendStringTcp(msg);
					} catch (ProtocolException e) {
						swc.handler.error(e);
					}
				}
			}
		}
		tcpToCache.clear();
	}
	
	public void sendStringTcp(String name, String msg) {
		tcpToCache.add(new Pair<>(name, msg));
	}
}

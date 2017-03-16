package game.net;

import game.exception.ProtocolException;
import game.net.client.IClientConnection;
import game.world.ServerWorldClient;

import java.util.ArrayList;

public class PacketCache {
	private ArrayList<String> tcpCache = new ArrayList<>();
	private ArrayList<String> udpCache = new ArrayList<>();
	
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
				// The handler handles this
			}
		}
		tcpCache.clear();
		udpCache.clear();
	}
}

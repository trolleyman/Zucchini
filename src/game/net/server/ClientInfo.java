package game.net.server;

import game.net.TCPConnection;
import game.net.UDPConnection;
import game.net.UDPRelay;
import game.world.Team;
import game.world.entity.Entity;

public class ClientInfo {
	public String name;
	/** The name of the lobby currently connected to. Can be null. */
	public String lobby;
	
	public TCPConnection tcpConn;
	public UDPRelay udpConn;
	
	public ClientInfo(String name, TCPConnection tcpConn, UDPRelay udpConn) {
		this.name = name;
		this.lobby = null;
		
		this.tcpConn = tcpConn;
		this.udpConn = udpConn;
	}
}

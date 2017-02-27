package game.net.server;

import game.net.TCPConnection;
import game.net.UDPConnection;
import game.world.Team;

public class ClientInfo {
	public String name;
	/** The name of the lobby currently connected to. Can be null. */
	public String lobby;
	public int team;
	public TCPConnection tcpConn;
	public UDPConnection udpConn;
	
	public ClientInfo(String name, TCPConnection tcpConn, UDPConnection udpConn) {
		this.name = name;
		this.lobby = null;
		this.team = Team.INVALID_TEAM;
		this.tcpConn = tcpConn;
		this.udpConn = udpConn;
	}
}

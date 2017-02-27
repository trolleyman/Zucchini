package game.net.server;

import game.net.TCPConnection;
import game.net.UDPConnection;

public class ClientInfo {
	public String name;
	public TCPConnection tcpConn;
	public UDPConnection udpConn;
	
	public ClientInfo(String name, TCPConnection tcpConn, UDPConnection udpConn) {
		this.name = name;
		this.tcpConn = tcpConn;
		this.udpConn = udpConn;
	}
}

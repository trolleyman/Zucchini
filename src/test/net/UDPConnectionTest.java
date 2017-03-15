package test.net;

import game.exception.ProtocolException;
import game.net.Protocol;
import game.net.UDPConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class UDPConnectionTest {
	private UDPConnection c1;
	private UDPConnection c2;
	
	@BeforeEach
	void setUp() throws ProtocolException {
		c1 = new UDPConnection();
		c2 = new UDPConnection();
	}
	
	@AfterEach
	void tearDown() {
		c1.close();
		c2.close();
	}
	
	@Test
	void connect() throws ProtocolException {
		c1.connect(new InetSocketAddress("localhost", c2.getSocket().getLocalPort()));
		c2.connect(new InetSocketAddress("localhost", c1.getSocket().getLocalPort()));
		
		c1.sendString("test1");
		String s = c2.recvString();
		assertEquals("test1", s);
	}
	
	@Test
	void send() throws ProtocolException {
		UDPConnection serverConn = new UDPConnection(Protocol.UDP_SERVER_PORT);
		c1.sendString("test2", new InetSocketAddress("localhost", serverConn.getSocket().getLocalPort()));
		String s = serverConn.recvString();
		assertEquals("test2", s);
		serverConn.close();
	}
	
	@Test
	void decode() throws ProtocolException {
		c1.connect(new InetSocketAddress("localhost", c2.getSocket().getLocalPort()));
		c2.connect(new InetSocketAddress("localhost", c1.getSocket().getLocalPort()));
		
		c1.sendString("test3");
		DatagramPacket p = c2.recv();
		String s = c2.decode(p);
		assertEquals( "test3", s);
		
		// Invalid encoding
		DatagramPacket p2 = new DatagramPacket(new byte[]{(byte)0xFF}, 0, 1);
		assertThrows(ProtocolException.class, () -> c2.decode(p2));
	}
	
	@Test
	void timeout() throws ProtocolException {
		c1.connect(new InetSocketAddress("localhost", c2.getSocket().getLocalPort()));
		c2.connect(new InetSocketAddress("localhost", c1.getSocket().getLocalPort()));
		
		DatagramPacket p = c2.recv(100);
		assertEquals(p, null);
		c1.sendString("test4");
		p = c2.recv(1000);
		String s = c2.decode(p);
		assertEquals("test4", s);
	}
}
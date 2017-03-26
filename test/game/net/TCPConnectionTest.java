package game.net;

import game.exception.NameException;
import game.exception.ProtocolException;
import org.junit.*;

import java.io.IOException;
import java.net.*;

import static org.junit.Assert.*;
import static game.TestUtil.assertThrows;

public class TCPConnectionTest {
	private static TCPConnection t1;
	private static TCPConnection t2;
	
	private static final Object lock = new Object();
	
	private static void setSocket(int port) {
		try {
			Thread.sleep(200);
			t2 = new TCPConnection(InetAddress.getLocalHost(), port);
			Thread.sleep(200);
			synchronized (lock) {
				lock.notifyAll();
			}
		} catch (InterruptedException | UnknownHostException | ProtocolException e) {
			throw new RuntimeException(e);
		}
	}
	
	@BeforeClass
	public static void setUp() throws IOException, ProtocolException {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// This is fine
		}
		ServerSocket ss = new ServerSocket(0);
		new Thread(() -> TCPConnectionTest.setSocket(ss.getLocalPort()), "setSocket").start();
		Socket s = ss.accept();
		t1 = new TCPConnection(s);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// This is fine
			}
		}
		ss.close();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// This is fine
		}
	}
	
	@AfterClass
	public static void tearDown() throws InterruptedException {
		t1.close();
		t2.close();
	}
	
	@Test
	public void send() throws ProtocolException, NameException {
		// Connection request & response
		t1.sendConnectionRequest("test1", 123);
		Tuple<String, InetSocketAddress> t = t2.recvConnectionRequest();
		assertEquals("test1", t.getFirst());
		assertEquals(123, t.getSecond().getPort());
		t2.sendConnectionResponseSuccess();
		t1.recvConnectionResponse();
		t2.sendConnectionResponseReject("Nope");
		assertThrows(NameException.class, () -> t1.recvConnectionResponse());
		
		// Test sending strings
		t1.sendString("test1");
		assertEquals("test1", t2.recvString());
		t1.sendString("");
		assertEquals("", t2.recvString());
		t1.close();
		assertThrows(ProtocolException.class, () -> t1.sendString("test2"));
		// v--- This should not error
		t1.close();
		t2.close();
		t2.close();
	}
}
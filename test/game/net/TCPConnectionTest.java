package game.net;

import game.exception.NameException;
import game.exception.ProtocolException;
import org.junit.*;

import java.io.IOException;
import java.net.*;

import static org.junit.Assert.*;
import static game.TestUtil.assertThrows;

public class TCPConnectionTest {
	private TCPConnection t1;
	private TCPConnection t2;
	
	private final Object lock = new Object();
	
	private void setSocket() {
		try {
			Thread.sleep(200);
			t2 = new TCPConnection(InetAddress.getLocalHost(), Protocol.TCP_SERVER_PORT);
			Thread.sleep(200);
			synchronized (lock) {
				lock.notifyAll();
			}
		} catch (InterruptedException | UnknownHostException | ProtocolException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Before
	public void setUp() {
		try {
			ServerSocket ss = new ServerSocket(Protocol.TCP_SERVER_PORT);
			new Thread(this::setSocket, "setSocket").start();
			Socket s = ss.accept();
			t1 = new TCPConnection(s);
			synchronized (lock) {
				lock.wait();
			}
			ss.close();
			Thread.sleep(200);
		} catch (InterruptedException | IOException | ProtocolException e) {
			throw new RuntimeException(e);
		}
	}
	
	@After
	public void tearDown() throws InterruptedException {
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
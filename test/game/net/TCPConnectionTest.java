package game.net;

import game.exception.InvalidMessageException;
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
	
	private void setSocket(int port) {
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
	
	@Before
	public void setUp() throws IOException, ProtocolException {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// This is fine
		}
		ServerSocket ss = new ServerSocket(0);
		new Thread(() -> this.setSocket(ss.getLocalPort()), "setSocket").start();
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
	
	@Test
	public void invalidMessageExceptionTest() throws ProtocolException, NameException {
		t1.sendConnectionRequest("test2", 124);
		t2.recvConnectionRequest();
		t2.sendString("invalid message");
		assertThrows(InvalidMessageException.class, () -> t1.recvConnectionResponse());
	}
}
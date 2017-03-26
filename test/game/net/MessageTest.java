package game.net;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {
	@Test
	public void toStringTest() throws Exception {
		Message m1 = new Message("", "test message 1");
		Message m2 = new Message("from", "test message 2");
		
		assertEquals(m1.toString(), "test message 1");
		assertEquals(m2.toString(), "from: test message 2");
	}
}
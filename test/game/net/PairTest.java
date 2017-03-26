package game.net;

import org.junit.Test;

import static org.junit.Assert.*;

public class PairTest {
	@Test
	public void pair() {
		Pair<String, Integer> p1 = new Pair<>("xxx", 52);
		Pair<String, Integer> p2 = new Pair<>("xxx", 53);
		Pair<String, Integer> p3 = new Pair<>("xxx", 52);
		assertTrue(p1.equals(p3));
		assertTrue(p3.equals(p1));
		
		assertFalse(p1.equals(p2));
		assertFalse(p2.equals(p1));
		
		assertFalse(p3.equals(p2));
		assertFalse(p2.equals(p3));
	}
}
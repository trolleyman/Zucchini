package test.world;

import game.world.PlayerScoreboardInfo;
import game.world.Scoreboard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ScoreboardTest {
	public Scoreboard sb;
	
	@Before
	public void setUp() throws Exception {
		sb = new Scoreboard();
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	private int getPlayerIndex(ArrayList<PlayerScoreboardInfo> is, String name) {
		for (int i = 0; i < is.size(); i++) {
			if (is.get(i).name.equals(name))
				return i;
		}
		return -1;
	}
	
	@Test
	public void sortPlayers() throws Exception {
		// Add players
		sb.addPlayer("1");
		sb.addPlayer("2");
		sb.addPlayer("3");
		sb.addPlayer("4");
		sb.addPlayer("5");
		sb.addPlayer("6");
		sb.addPlayer("7");
		sb.addPlayer("8");
		sb.addPlayer("9");
		sb.update(10.0f);
		sb.addPlayer("10");
		
		// Setup deaths (5,6,7,8,9)
		sb.killPlayer("5");
		sb.killPlayer("6");
		sb.killPlayer("7");
		sb.killPlayer("8");
		sb.killPlayer("9");
		sb.killPlayer("10");
		
		// Setup player kills (1:1, 2:1, 5:2, 6:2, 10:2)
		sb.addPlayerKill("1");
		sb.addPlayerKill("2");
		sb.addPlayerKill("5"); sb.addPlayerKill("5");
		sb.addPlayerKill("6"); sb.addPlayerKill("6");
		sb.addPlayerKill("10"); sb.addPlayerKill("10");
		
		// Setup monster kills (1:1, 3:2, 5:1, 7:2, 8:1, 9:1, 10:2)
		sb.addMonsterKill("1");
		sb.addMonsterKill("3"); sb.addMonsterKill("3");
		sb.addMonsterKill("5");
		sb.addMonsterKill("7"); sb.addMonsterKill("7");
		sb.addMonsterKill("8");
		sb.addMonsterKill("9");
		sb.addMonsterKill("10"); sb.addMonsterKill("10");
		
		// Debug print
		System.out.println(sb.toString().replace(", ", ",\n"));
		
		// Order should be 1,2,3,4,5,6,7,8,9 at the end
		ArrayList<PlayerScoreboardInfo> is = sb.getPlayers();
		assertEquals(0, getPlayerIndex(is, "1"));
		assertEquals(1, getPlayerIndex(is, "2"));
		assertEquals(2, getPlayerIndex(is, "3"));
		assertEquals(3, getPlayerIndex(is, "4"));
		assertEquals(4, getPlayerIndex(is, "5"));
		assertEquals(5, getPlayerIndex(is, "6"));
		assertEquals(6, getPlayerIndex(is, "7"));
		assertEquals(7, getPlayerIndex(is, "8"));
		assertEquals(8, getPlayerIndex(is, "9"));
		assertEquals(9, getPlayerIndex(is, "10"));
	}
}
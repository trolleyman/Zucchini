package test.ui;

import static org.junit.Assert.*;
import org.junit.Test;
import game.ui.GameUI;

public class GameUITest {

	
	GameUI gameui = new GameUI(null, null);
	
	@Test
	public void testWorld() {
		assertNotNull(gameui.world);
	}

}

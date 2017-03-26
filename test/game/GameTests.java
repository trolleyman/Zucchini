package game;

import game.net.NetTests;
import game.world.WorldTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for the game package
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		// Test Suites
		NetTests.class,
		WorldTests.class,
		
		// Standalone test classes in the game package
		InputPipeMultiTest.class,
		InputPipeTest.class,
		ResourcesTest.class,
		UtilTest.class,
})
public class GameTests {
}

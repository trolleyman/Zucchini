package game.world;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for the world package
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		ScoreboardTest.class,
		TeamTest.class,
})
public class WorldTests {
}

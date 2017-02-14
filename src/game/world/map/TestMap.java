package game.world.map;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple test map
 * 
 * @author Callum
 */
public class TestMap extends Map {
	
	private static float X0 = 0.0f;
	private static float Y0 = 0.0f;
	private static float X1 = 4.0f;
	private static float Y1 = 4.0f;

	
	/** The walls of the map */
	private static final Wall[] WALLS = {
		new Wall(X0, Y0, X0, Y1), // Left
		new Wall(X1, Y0, X1, Y1), // Right
		new Wall(X0, Y0, X1, Y0), // Top
		new Wall(X0, Y1, X1, Y1), // Bottom
	};
	
	/** Constructs the test map */
	public TestMap() {
		super(new ArrayList<>(Arrays.asList(WALLS)));
	}
}

package game.world;

public class TestMap extends Map {
	
	private static float X0 = 100.0f;
	private static float Y0 = 100.0f;
	private static float X1 = 800.0f;
	private static float Y1 = 600.0f;
	
	private static final float[] LINES = {
		X0, Y0, X0, Y1, // Left
		X1, Y0, X1, Y1, // Right
		X0, Y0, X1, Y0, // Top
		X0, Y1, X1, Y1, // Bottom
	};
	
	public TestMap() {
		super(LINES);
	}
}

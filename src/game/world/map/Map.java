package game.world.map;

import java.util.concurrent.ThreadLocalRandom;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;

/**
 * Represents a specified map.
 * 
 * @author Callum
 */
public class Map {
	public static Map createTestMap() {
		Maze maze = new Maze(ThreadLocalRandom.current(), 15, 15, 0, 0, 14, 14);
		return new MazeMap(maze);
	}
	
	/** The "walls" of the map that entities can collide with */
	private float[] lines;
	
	/**
	 * Construct a map with the specified "wall"
	 */
	protected Map(float[] _lines) {
		this.lines = _lines;
	}
	
	/**
	 * Returns the point at which the line given intersects with the map.
	 * @param x0 Start x-coordinate of the line
	 * @param y0 Start y-coordinate of the line
	 * @param x1 End x-coordinate of the line
	 * @param y1 End x-coordinate of the line
	 * @return null if there was no intersection
	 */
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		// TODO: Implement
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 * Render the map
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		for (int i = 0; i < lines.length - 3; i += 4) {
			float x0 = lines[i  ];
			float y0 = lines[i+1];
			float x1 = lines[i+2];
			float y1 = lines[i+3];
			
			r.drawLine(x0, y0, x1, y1, ColorUtil.RED, 1.0f);
		}
	}
}


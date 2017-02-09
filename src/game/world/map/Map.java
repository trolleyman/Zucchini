package game.world.map;

import java.util.concurrent.ThreadLocalRandom;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.PhysicsUtil;

/**
 * Represents a specified map.
 * 
 * @author Callum
 */
public class Map {
	public static Map createTestMap() {
		Maze maze = new Maze(ThreadLocalRandom.current(), 15, 15, 0, 0, 14, 14);
		return new MazeMap(maze, 1.5f);
	}
	
	private Vector2f temp = new Vector2f();
	
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
	public Vector2f intersectsLine(float x0, float y0, float x1, float y1) {
		Vector2f ret = null;
		for (int i = 0; i < lines.length - 3; i += 4) {
			float x2 = lines[i  ];
			float y2 = lines[i+1];
			float x3 = lines[i+2];
			float y3 = lines[i+3];
			
			Vector2f p = PhysicsUtil.intersectLineLine(x0, y0, x1, y1, x2, y2, x3, y3);
			ret = PhysicsUtil.getClosest(temp.set(x0, y0), ret, p);
		}
		return ret;
	}
	
	/**
	 * Returns the point at which a circle intersects with the map
	 * <p>
	 * For times when the circle intersects twice with the map, the average point is returned.
	 * @param x0 The x-coordinate of the circle
	 * @param y0 The y-coordinate of the circle
	 * @param radius The radius of the circle
	 * @return null if there was no intersection
	 */
	public Vector2f intersectsCircle(float x0, float y0, float radius) {
		Vector2f ret = null;
		for (int i = 0; i < lines.length - 3; i += 4) {
			float x1 = lines[i  ];
			float y1 = lines[i+1];
			float x2 = lines[i+2];
			float y2 = lines[i+3];
			
			Vector2f p = PhysicsUtil.intersectCircleLine(x0, y0, radius, x1, y1, x2, y2);
			ret = PhysicsUtil.getClosest(temp.set(x0, y0), ret, p);
		}
		return ret;
	}
	
	/**
	 * Gets the line of sight data for the position given.
	 * @param pos The position of the camera in the world
	 * @param num The number of samples
	 * @param max The maximum length of the line of sight
	 * @return A list of points, [pos.x, pos.y, x0, y0, x1, y1, ..., xn, yn, x0, y0]
	 */
	public float[] getLineOfSight(Vector2f pos, int num, float max) {
		float[] ret = new float[num * 2 + 4];
		ret[0] = pos.x;
		ret[1] = pos.y;
		for (int i = 0; i <= num; i++) {
			// Get current angle
			double ang = -((double)i / num * Math.PI * 2);
			// Convert angle to cartesian co-ords (length of max)
			float x = pos.x + max * (float)Math.sin(ang);
			float y = pos.y + max * (float)Math.cos(ang);
			Vector2f intersection = this.intersectsLine(pos.x, pos.y, x, y);
			if (intersection == null) {
				ret[2+i*2  ] = x;
				ret[2+i*2+1] = y;
			} else {
				ret[2+i*2  ] = intersection.x;
				ret[2+i*2+1] = intersection.y;
			}
		}
		return ret;
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


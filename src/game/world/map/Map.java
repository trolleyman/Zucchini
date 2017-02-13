package game.world.map;

import java.util.concurrent.ThreadLocalRandom;

import org.joml.Vector2f;

import game.ColorUtil;
import game.Util;
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
//		return new TestMap();
	}
	
	/** The "walls" of the map that entities can collide with */
	private ArrayList<Wall> walls;
	
	/**
	 * Construct a map with the specified walls
	 */
	protected Map(ArrayList<Wall> _walls) {
		this.walls = _walls;
	}
	
	/**
	 * Returns the point at which the line given intersects with the map.
	 * @param x0 Start x-coordinate of the line
	 * @param y0 Start y-coordinate of the line
	 * @param x1 End x-coordinate of the line
	 * @param y1 End x-coordinate of the line
	 * @param dest Where to store the result of the operation. If this is null, the function allocates
	 *             a new Vector2f.
	 * @return null if there was no intersection
	 */
	public Vector2f intersectsLine(float x0, float y0, float x1, float y1, Vector2f dest) {
		Vector2f p0 = Util.pushTemporaryVector2f().set(x0, y0);
		Vector2f temp1 = Util.pushTemporaryVector2f();
		Vector2f temp2 = Util.pushTemporaryVector2f();
		Vector2f acc = null;
		for (Wall wall : walls) {
			float x2 = wall.p0.x;
			float y2 = wall.p0.y;
			float x3 = wall.p1.x;
			float y3 = wall.p1.y;
			
			Vector2f intersection = PhysicsUtil.intersectLineLine(x0, y0, x1, y1, x2, y2, x3, y3, temp1);
			Vector2f closest = PhysicsUtil.getClosest(p0, acc, intersection);
			if (closest != null)
				temp2.set(closest);
			acc = closest == null ? null : temp2;
		}
		
		Vector2f ret;
		if (acc != null) {
			if (dest == null)
				dest = new Vector2f();
			dest.set(acc);
			ret = dest;
		} else {
			ret = null;
		}
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
		return ret;
	}
	
	/**
	 * Returns the point at which a circle intersects with the map
	 * <p>
	 * For times when the circle intersects twice with the map, the average point is returned.
	 * @param x0 The x-coordinate of the circle
	 * @param y0 The y-coordinate of the circle
	 * @param radius The radius of the circle
	 * @param dest Where to store the result of the operation. If this is null, the function allocates
	 *             a new Vector2f.
	 * @return null if there was no intersection
	 */
	public Vector2f intersectsCircle(float x0, float y0, float radius, Vector2f dest) {
		Vector2f p0 = Util.pushTemporaryVector2f().set(x0, y0);
		Vector2f temp = Util.pushTemporaryVector2f();
		Vector2f acc = Util.pushTemporaryVector2f();
		boolean intersection = false;
		for (Wall wall : walls) {
			float x1 = wall.p0.x;
			float y1 = wall.p0.y;
			float x2 = wall.p1.x;
			float y2 = wall.p1.y;
			
			Vector2f ret = PhysicsUtil.intersectCircleLine(x0, y0, radius, x1, y1, x2, y2, temp);
			if (ret != null) {
				intersection = true;
				acc.set(PhysicsUtil.getClosest(p0, acc, temp));
			}
		}
		if (!intersection) {
			// No intersection
			Util.popTemporaryVector2f();
			Util.popTemporaryVector2f();
			Util.popTemporaryVector2f();
			return null;
		} else {
			// Intersection
			if (dest == null)
				dest = new Vector2f();
			dest.set(acc);
			Util.popTemporaryVector2f();
			Util.popTemporaryVector2f();
			Util.popTemporaryVector2f();
			return dest;
		}
	}
	
	/**
	 * Gets the line of sight data for the position given.
	 * @param pos The position of the camera in the world
	 * @param num The number of samples
	 * @param max The maximum length of the line of sight
	 * @param buf Where to store the buffer. If this is null, will allocate a new float array.
	 * @return A list of points, [pos.x, pos.y, x0, y0, x1, y1, ..., xn, yn, x0, y0]
	 */
	public float[] getLineOfSight(Vector2f pos, int num, float max, float[] buf) {
		Vector2f temp = Util.pushTemporaryVector2f();
		int len = num * 2 + 4;
		if (buf == null || buf.length != len)
			buf = new float[len];
		buf[0] = pos.x;
		buf[1] = pos.y;
		for (int i = 0; i <= num; i++) {
			// Get current angle
			double ang = -((double)i / num * Math.PI * 2);
			// Convert angle to cartesian co-ords (length of max)
			float x = pos.x + max * (float)Math.sin(ang);
			float y = pos.y + max * (float)Math.cos(ang);
			Vector2f intersection = this.intersectsLine(pos.x, pos.y, x, y, temp);
			if (intersection == null) {
				buf[2+i*2  ] = x;
				buf[2+i*2+1] = y;
			} else {
				buf[2+i*2  ] = intersection.x;
				buf[2+i*2+1] = intersection.y;
			}
		}
		Util.popTemporaryVector2f();
		return buf;
	}
	
	/**
	 * Render the map
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		for (Wall wall : walls) {
			float x0 = wall.p0.x;
			float y0 = wall.p0.y;
			float x1 = wall.p1.x;
			float y1 = wall.p1.y;
			
			r.drawLine(x0, y0, x1, y1, ColorUtil.RED, 1.0f);
		}
	}
}


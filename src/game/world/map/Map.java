package game.world.map;

import game.ColorUtil;
import game.Util;
import game.exception.ProtocolException;
import game.render.Align;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Represents a specified map.
 * 
 * @author Callum
 */
public class Map {
	/** Holds the extents of the map. [x, y, w, h] format */
	private Vector4f rect;
	
	public static Map createTestMap() {
//		Maze maze = new Maze(ThreadLocalRandom.current(), 15, 15, 0, 0, 14, 14);
//		return new MazeMap(maze, 1.5f);
		
		//return new TestMap();
		return new FinalMap();
		
//		return new SimpleMap();
	}
	
	private static final float LINE_OF_SIGHT_EPSILON = 0.00001f;
	
	/** The "walls" of the map that entities can collide with */
	public ArrayList<Wall> walls;
	/** The intiial starting entities in the map */
	protected transient ArrayList<Entity> initialEntities;
	/** What scale the pathfinding algorithm should use */
	private float pathFindingScale;
	/** The cached pathfinding map */
	private transient PathFindingMap pathFindingMap = null;
	
	/**
	 * Construct a map with the specified walls
	 */
	protected Map(ArrayList<Wall> _walls, float _pathfindingScale) {
		this(_walls, new ArrayList<>(), _pathfindingScale);
	}
	
	/**
	 * Construct a map with the specified walls and initial entities
	 */
	protected Map(ArrayList<Wall> _walls, ArrayList<Entity> _initialEntities, float _pathFindingScale) {
		this.walls = _walls;
		this.initialEntities = _initialEntities;
		this.pathFindingScale = _pathFindingScale;
	}
	
	/**
	 * Gets the cached current pathfinding map
	 */
	public PathFindingMap getPathFindingMap() {
		if (pathFindingMap == null)
			this.pathFindingMap = new PathFindingMap(this, pathFindingScale);
		return pathFindingMap;
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
	
	private void projectLine(ArrayList<Vector3f> points, Vector2f pos, float max, float angle, Vector3f dest) {
		float x = pos.x + max * Util.getDirX(angle);
		float y = pos.y + max * Util.getDirY(angle);
		Vector2f temp = Util.pushTemporaryVector2f();
		Vector2f intersection = this.intersectsLine(pos.x, pos.y, x, y, temp);
		if (intersection == null) {
			dest.x = x;
			dest.y = y;
		} else {
			dest.x = temp.x;
			dest.y = temp.y;
		}
		dest.z = angle;
		Util.popTemporaryVector2f();
		points.add(dest);
	}
	
	private FloatBuffer putFloat(FloatBuffer buf, float f) {
		if (buf.position() == buf.capacity()) {
			buf = MemoryUtil.memRealloc(buf, buf.capacity() * 2);
		}
		buf.put(f);
		return buf;
	}
	
	private boolean inFov(float angle, float aimAngle, float fov) {
		float diff = Math.abs(angle - aimAngle);
		if (diff > Math.PI)
			diff = (float)(2*Math.PI) - diff;
		return diff < fov;
	}
	
	/**
	 * Gets the line of sight data for the position given.
	 * @param pos The position of the camera in the world
	 * @param max The maximum length of the line of sight
	 * @param aimAngle The current angle of the player
	 * @param fov The field of view of the player
	 * @param buf Where to store the buffer. This <b>**MUST**</b> be allocated using MemoryUtils.memAllocFloat()
	 * @return A list of points, [pos.x, pos.y, x0, y0, x1, y1, ..., xn, yn, x0, y0]
	 */
	public FloatBuffer getLineOfSight(Vector2f pos, float max, float aimAngle, float fov, FloatBuffer buf) {
		// Calculate points
		ArrayList<Vector3f> points = new ArrayList<>();
		
		// First the walls, making sure to void the points that are outside the fov.
		for (Wall w : walls) {
			float angle;
			angle = Util.getAngle(pos.x, pos.y, w.p0.x, w.p0.y);
			if (inFov(angle, aimAngle, fov)) {
				Vector3f t1 = Util.pushTemporaryVector3f();
				Vector3f t2 = Util.pushTemporaryVector3f();
				projectLine(points, pos, max, angle - LINE_OF_SIGHT_EPSILON, t1);
				projectLine(points, pos, max, angle + LINE_OF_SIGHT_EPSILON, t2);
			}
			
			angle = Util.getAngle(pos.x, pos.y, w.p1.x, w.p1.y);
			if (inFov(angle, aimAngle, fov)) {
				Vector3f t1 = Util.pushTemporaryVector3f();
				Vector3f t2 = Util.pushTemporaryVector3f();
				projectLine(points, pos, max, angle - LINE_OF_SIGHT_EPSILON, t1);
				projectLine(points, pos, max, angle + LINE_OF_SIGHT_EPSILON, t2);
			}
		}
		
		// And also project lines on the edge of the fov.
		float langle = Util.normalizeAngle(aimAngle - fov);
		float rangle = Util.normalizeAngle(aimAngle + fov);
		Vector3f ltemp = Util.pushTemporaryVector3f();
		Vector3f rtemp = Util.pushTemporaryVector3f();
		projectLine(points, pos, max, langle, ltemp);
		projectLine(points, pos, max, rangle, rtemp);
		
		// Sort points in order of angle, lowest first
		points.sort((l, r) -> -Float.compare(l.z, r.z));
		
		// Put into buffer
		buf.clear();
		buf = putFloat(buf, pos.x);
		buf = putFloat(buf, pos.y);
		
		float firstX = points.size() == 0 ? pos.x : points.get(0).x;
		float firstY = points.size() == 0 ? pos.y : points.get(0).y;
		for (Vector3f p : points) {
			buf = putFloat(buf, p.x);
			buf = putFloat(buf, p.y);
			Util.popTemporaryVector3f();
		}
		
		buf = putFloat(buf, firstX);
		buf = putFloat(buf, firstY);
		
		buf.flip();
		return buf;
	}
	
	/**
	 * Gets the line of sight data for the position given.
	 * @param pos The position of the camera in the world
	 * @param max The maximum length of the line of sight
	 * @param buf Where to store the buffer. This **MUST** be allocated using MemoryUtils.memAllocFloat()
	 * @return A list of points, [pos.x, pos.y, x0, y0, x1, y1, ..., xn, yn, x0, y0]
	 */
	public FloatBuffer getLineOfSight(Vector2f pos, float max, FloatBuffer buf) {
		// Calculate points
		ArrayList<Vector3f> points = new ArrayList<>();
		for (Wall w : walls) {
			Vector3f temp0 = Util.pushTemporaryVector3f();
			Vector3f temp1 = Util.pushTemporaryVector3f();
			Vector3f temp2 = Util.pushTemporaryVector3f();
			Vector3f temp3 = Util.pushTemporaryVector3f();
			
			float angle;
			angle = Util.getAngle(pos.x, pos.y, w.p0.x, w.p0.y);
			projectLine(points, pos, max, angle - LINE_OF_SIGHT_EPSILON, temp0);
			projectLine(points, pos, max, angle + LINE_OF_SIGHT_EPSILON, temp1);
			
			angle = Util.getAngle(pos.x, pos.y, w.p1.x, w.p1.y);
			projectLine(points, pos, max, angle - LINE_OF_SIGHT_EPSILON, temp2);
			projectLine(points, pos, max, angle + LINE_OF_SIGHT_EPSILON, temp3);
		}
		
		// Sort points in order of angle, lowest first
		points.sort((l, r) -> -Float.compare(l.z, r.z));
		
		// Put into buffer
		buf.clear();
		buf = putFloat(buf, pos.x);
		buf = putFloat(buf, pos.y);
		
		float firstX = points.size() == 0 ? pos.x : points.get(0).x;
		float firstY = points.size() == 0 ? pos.y : points.get(0).y;
		for (Vector3f p : points) {
			buf = putFloat(buf, p.x);
			buf = putFloat(buf, p.y);
			Util.popTemporaryVector3f();
		}
		
		buf = putFloat(buf, firstX);
		buf = putFloat(buf, firstY);
		
		buf.flip();
		return buf;
	}
	
	/**
	 * Render the map
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		// Render background
		this.renderBackground(r);
		
		// Render foreground
		this.renderForeground(r);
	}
	
	/**
	 * Renders the background of the map (i.e. the floor)
	 */
	public void renderBackground(IRenderer r) {
		Vector4f rect = getRect();
		r.drawBox(Align.BL, rect.x, rect.y, rect.z, rect.w, ColorUtil.BLACK);
	}
	
	/**
	 * Renders the foreground (i.e. the walls)
	 */
	public void renderForeground(IRenderer r) {
		for (Wall wall : walls) {
			float x0 = wall.p0.x;
			float y0 = wall.p0.y;
			float x1 = wall.p1.x;
			float y1 = wall.p1.y;
			
			r.drawLine(x0, y0, x1, y1, ColorUtil.RED, 1.0f);
		}
	}
	
	/**
	 * Gets the extents of the map. This returns a rectangle in the format [x,y,w,h]
	 */
	public Vector4f getRect() {
		if (rect != null)
			return rect;
		else
			rect = new Vector4f();
		
		Vector2f min = Util.pushTemporaryVector2f();
		Vector2f max = Util.pushTemporaryVector2f();
		
		for (Wall w : walls) {
			// Min x
			min.x = Math.min(min.x, w.p0.x);
			min.x = Math.min(min.x, w.p1.x);
			// Min y
			min.y = Math.min(min.y, w.p0.y);
			min.y = Math.min(min.y, w.p0.y);
			// Max x
			max.x = Math.max(max.x, w.p0.x);
			max.x = Math.max(max.x, w.p1.x);
			// Max y
			max.y = Math.max(max.y, w.p0.y);
			max.y = Math.max(max.y, w.p0.y);
		}
		
		rect.set(min.x, min.y, max.x - min.x, max.y - min.y);
		
		Util.popTemporaryVector2f();
		Util.popTemporaryVector2f();
		
		return rect;
	}
	
	public ArrayList<Entity> getInitialEntities() {
		return initialEntities;
	}
	
	public Vector2f getSpawnLocation(int team) {
		// TODO: Actually have different spawns for different teams
		return new Vector2f(2.0f, 2.0f);
	}
}


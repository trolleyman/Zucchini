package game.world.map;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.PhysicsUtil;
import game.world.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Represents a specified map.
 * 
 * @author Callum
 */
public class Map {
	/** Small number that is the max fidelity of the line of sight algorithm */
	private static final float LOS_ANGLE_MAX_FIDELITY = (float)Math.toRadians(0.1f);
	/** Small number added and subtracted from critical points to get either side of the critical point */
	private static final float LOS_DIFF_EPSILON = (float) Math.toRadians(0.1f);
	
	/** Holds the extents of the map. [x, y, w, h] format */
	private Vector4f rect;
	
	public static Map createTestMap() {
//		Maze maze = new Maze(ThreadLocalRandom.current(), 15, 15, 0, 0, 14, 14);
//		return new MazeMap(maze, 1.5f);
		
//		return new TestMap();
		return new FinalMap();
		
//		return new SimpleMap();
	}
	
	/** The "walls" of the map that entities can collide with */
	public ArrayList<Wall> walls;
	/** The intiial starting entities in the map */
	protected transient ArrayList<Entity> initialEntities;
	/** What scale the pathfinding algorithm should use */
	private float pathFindingScale = 10;
	/** The cached pathfinding map */
	private transient PathFindingMap pathFindingMap = null;
	
	private transient FloatBuffer tempLosAngleBuf = null;
	private transient ArrayList<Vector3f> tempLosPointsBuf = null;
	
	/**
	 * Construct a map with the specified walls
	 */
	public Map(ArrayList<Wall> _walls, float _pathfindingScale) {
		this(_walls, new ArrayList<>(), _pathfindingScale);
	}
	
	/**
	 * Construct a map with the specified walls and initial entities
	 */
	public Map(ArrayList<Wall> _walls, ArrayList<Entity> _initialEntities, float _pathFindingScale) {
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
		return intersectsLine(x0, y0, x1, y1, dest, null, (w) -> true );
	}
	
	/**
	 * Returns the point at which the line given intersects with the map.
	 * @param x0 Start x-coordinate of the line
	 * @param y0 Start y-coordinate of the line
	 * @param x1 End x-coordinate of the line
	 * @param y1 End x-coordinate of the line
	 * @param dest Where to store the result of the operation. If this is null, the function allocates
	 *             a new Vector2f.
	 * @param destWall Where to store the wall that intersects with the line. If this is null, this is not used.
	 * @param pred The predicate to apply to each wall in turn. If false, ignores the wall.
	 * @return null if there was no intersection
	 */
	public Vector2f intersectsLine(float x0, float y0, float x1, float y1, Vector2f dest, Wall destWall, Predicate<Wall> pred) {
		Vector2f p0 = Util.pushTemporaryVector2f().set(x0, y0);
		Vector2f temp1 = Util.pushTemporaryVector2f();
		Vector2f temp2 = Util.pushTemporaryVector2f();
		Vector2f acc = null;
		Wall closestWall = null;
		for (Wall wall : walls) {
			if (!pred.test(wall))
				continue;
			
			float x2 = wall.p0.x;
			float y2 = wall.p0.y;
			float x3 = wall.p1.x;
			float y3 = wall.p1.y;
			
			// Get intersection
			Vector2f intersection = PhysicsUtil.intersectLineLine(x0, y0, x1, y1, x2, y2, x3, y3, temp1);
			
			// Get closest
			Vector2f closest;
			if (acc == null) {
				closest = intersection;
				closestWall = wall;
			} else if (intersection == null) {
				closest = acc;
			} else {
				float accDistSq = acc.distanceSquared(p0);
				float intersectionDistSq = intersection.distanceSquared(p0);
				
				if (accDistSq < intersectionDistSq) {
					closest = acc;
				} else {
					closest = intersection;
					closestWall = wall;
				}
			}
			
			if (closest != null) {
				temp2.set(closest);
				acc = temp2;
			}
		}
		
		if (closestWall != null && destWall != null) {
			if (destWall.p0 == null)
				destWall.p0 = new Vector2f();
			destWall.p0.set(closestWall.p0);
			if (destWall.p1 == null)
				destWall.p1 = new Vector2f();
			destWall.p1.set(closestWall.p1);
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
	 * Puts the float into the buffer, resizing if necessary
	 * @return the (potentially) new buffer
	 */
	private FloatBuffer putFloat(FloatBuffer buf, float f) {
		if (buf.position() == buf.capacity()) {
			buf = MemoryUtil.memRealloc(buf, buf.capacity() * 2);
		}
		buf.put(f);
		return buf;
	}
	
	/**
	 * Calculates the critical angles for the line of sight algorithm, and stores them in {@link #tempLosAngleBuf}
	 */
	public void calculateCriticalAngles(Vector2f pos) {
		if (tempLosAngleBuf == null)
			tempLosAngleBuf = MemoryUtil.memAllocFloat(32);
		tempLosAngleBuf.clear();
		
		// Put all wall points in the buffer (if they are in the field of view)
		for (Wall w : walls) {
			float a0 = Util.getAngle(pos.x, pos.y, w.p0.x, w.p0.y);
			tempLosAngleBuf = putFloat(tempLosAngleBuf, a0);
			float a1 = Util.getAngle(pos.x, pos.y, w.p1.x, w.p1.y);
			tempLosAngleBuf = putFloat(tempLosAngleBuf, a1);
		}
		
		// Flip buffer
		tempLosAngleBuf.flip();
		
		// Sort buffer
		Util.sortFloatBuffer(tempLosAngleBuf);
		
		// Get rid of close angles
		Util.removeSimilarFloats(tempLosAngleBuf, LOS_ANGLE_MAX_FIDELITY);
		
		// Reverse angles
		Util.reverseFloatBuffer(tempLosAngleBuf);
	}
	
	/**
	 * Calculates the critical angles for the line of sight algorithm, and stores them in {@link #tempLosAngleBuf}
	 */
	public void calculateCriticalAngles(Vector2f pos, float aimAngle, float fov) {
		if (tempLosAngleBuf == null)
			tempLosAngleBuf = MemoryUtil.memAllocFloat(32);
		tempLosAngleBuf.clear();
		
		// Put all wall points in the buffer (if they are in the field of view)
		for (Wall w : walls) {
			float a0 = Util.getAngle(pos.x, pos.y, w.p0.x, w.p0.y);
			if (Util.getAngleDiff(a0, aimAngle) < (fov / 2))
				tempLosAngleBuf = putFloat(tempLosAngleBuf, a0);
			float a1 = Util.getAngle(pos.x, pos.y, w.p1.x, w.p1.y);
			if (Util.getAngleDiff(a1, aimAngle) < (fov / 2))
				tempLosAngleBuf = putFloat(tempLosAngleBuf, a1);
		}
		
		// Add edge of FOV
		float langle = Util.normalizeAngle(aimAngle - fov/2);
		float rangle = Util.normalizeAngle(aimAngle + fov/2);
		tempLosAngleBuf = putFloat(tempLosAngleBuf, langle);
		tempLosAngleBuf = putFloat(tempLosAngleBuf, rangle);
		
		// Flip buffer
		tempLosAngleBuf.flip();
		
		// Sort buffer
		Util.sortFloatBuffer(tempLosAngleBuf);
		
		// Get rid of close angles
		Util.removeSimilarFloats(tempLosAngleBuf, LOS_ANGLE_MAX_FIDELITY);
		
		// Reverse angles
		Util.reverseFloatBuffer(tempLosAngleBuf);
	}
	
	/**
	 * Projects a line from pos with the angle specified, stores the result in dest.
	 */
	private void projectLine(Vector2f pos, float max, float angle, Vector3f dest) {
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
	}
	
	/**
	 * Calculates the points that intersect with the map based on the angles in {@link #tempLosAngleBuf}
	 * and stores the result in {@link #tempLosPointsBuf}.
	 */
	public void calculatePoints(Vector2f pos, float max) {
		if (tempLosPointsBuf == null)
			tempLosPointsBuf = new ArrayList<>();
		for (int i = 0; i < tempLosPointsBuf.size(); i++) {
			Util.popTemporaryVector3f();
		}
		tempLosPointsBuf.clear();
		
		for (int i = 0; i < tempLosAngleBuf.limit(); i++) {
			Vector3f t0 = Util.pushTemporaryVector3f();
			Vector3f t1 = Util.pushTemporaryVector3f();
			float angle = tempLosAngleBuf.get(i);
			projectLine(pos, max, angle - LOS_DIFF_EPSILON, t0);
			projectLine(pos, max, angle + LOS_DIFF_EPSILON, t1);
			tempLosPointsBuf.add(t0);
			tempLosPointsBuf.add(t1);
		}
		
		tempLosPointsBuf.sort((l, r) -> -Float.compare(l.z, r.z));
	}
	
	/**
	 * Gets the line of sight data for the position given.
	 * @param pos The position of the camera in the world
	 * @param max The maximum length of the line of sight
	 * @param aimAngle The current angle of the player
	 * @param fov The field of view of the player
	 * @param buf Where to store the buffer. This <b>**MUST**</b> be allocated using MemoryUtil.memAllocFloat()
	 * @return A list of points in triangle fan format [pos.x, pos.y, x0, y0, x1, y1, ..., xn, yn, x0, y0]
	 */
	public FloatBuffer getLineOfSight(Vector2f pos, float max, float aimAngle, float fov, FloatBuffer buf) {
		if (buf == null)
			buf = MemoryUtil.memAllocFloat(64);
		if (tempLosAngleBuf == null)
			tempLosAngleBuf = MemoryUtil.memAllocFloat(64);
		
		// Calculate critical angles
		calculateCriticalAngles(pos, aimAngle, fov);
		
		// Calculate points from the critical angles
		calculatePoints(pos, max);
		
		// Put points into buffer as a triangle fan
		return calculateTriangleFan(pos, buf);
	}
	
	/**
	 * Gets the line of sight data for the position given.
	 * @param pos The position of the camera in the world
	 * @param max The maximum length of the line of sight
	 * @param buf Where to store the buffer. This **MUST** be allocated using MemoryUtils.memAllocFloat(), or be null.
	 * @return A list of points in triangle fan format [pos.x, pos.y, x0, y0, x1, y1, ..., xn, yn, x0, y0]
	 */
	public FloatBuffer getLineOfSight(Vector2f pos, float max, FloatBuffer buf) {
		if (buf == null)
			buf = MemoryUtil.memAllocFloat(64);
		if (tempLosAngleBuf == null)
			tempLosAngleBuf = MemoryUtil.memAllocFloat(64);
		
		// Calculate critical angles
		calculateCriticalAngles(pos);
		
		// Calculate points from the critical angles
		calculatePoints(pos, max);
		
		// Put points into buffer as a triangle fan
		return calculateTriangleFan(pos, buf);
	}
	
	/**
	 * Converts {@link #tempLosPointsBuf} into a triangle fan and stores the result in buf. Returns the new buffer holding
	 * the result.
	 */
	private FloatBuffer calculateTriangleFan(Vector2f pos, FloatBuffer buf) {
		buf.clear();
		buf = putFloat(buf, pos.x);
		buf = putFloat(buf, pos.y);
		
		float firstX = tempLosPointsBuf.size() == 0 ? pos.x : tempLosPointsBuf.get(0).x;
		float firstY = tempLosPointsBuf.size() == 0 ? pos.y : tempLosPointsBuf.get(0).y;
		for (Vector3f p : tempLosPointsBuf) {
			buf = putFloat(buf, p.x);
			buf = putFloat(buf, p.y);
			Util.popTemporaryVector3f();
		}
		tempLosPointsBuf.clear();
		
		buf = putFloat(buf, firstX);
		buf = putFloat(buf, firstY);
		
		// Flip buffer for reading
		buf.flip();
		return buf;
	}
	
	/**
	 * Render the map
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		this.render(r, false);
	}
	
	/**
	 * Render the map
	 * @param r The renderer
	 * @param drawWalls Whether to draw the walls
	 */
	public void render(IRenderer r, boolean drawWalls) {
		// Render background
		this.renderFloor(r);
		
		// Render foreground
		if (drawWalls)
			this.renderWalls(r);
	}
	
	/**
	 * Renders the background of the map (i.e. the floor)
	 */
	public void renderFloor(IRenderer r) {
		Vector4f rect = getRect();
		r.drawBox(Align.BL, rect.x, rect.y, rect.z, rect.w, ColorUtil.BLACK);
		Texture background = r.getTextureBank().getTexture("map_background3.png");
		r.drawTexture(background, Align.BL, -1.0f, -1.0f, 32.0f, 32.0f);
	}
	
	/**
	 * Renders the foreground (i.e. the walls)
	 */
	public void renderWalls(IRenderer r) {
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


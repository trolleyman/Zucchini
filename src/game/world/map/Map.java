package game.world.map;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.entity.Entity;
import game.world.physics.PhysicsUtil;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

/**
 * Represents a specified map.
 *
 * @author Callum
 */
public class Map {
	public static Map createTestMap() {
//		Maze maze = new Maze(ThreadLocalRandom.current(), 15, 15, 0, 0, 14, 14);
//		return new MazeMap(maze, 1.5f);
		
//		return new TestMap();
		
		return new CollisionTestMap();
	}
	
	/** The "walls" of the map that entities can collide with */
	public ArrayList<Wall> walls;
	/** The intiial starting entities in the map */
	protected ArrayList<Entity> initialEntities;
	/** What scale the pathfinding algorithm should use */
	private float pathFindingScale;
	/** The cached pathfinding map */
	private PathFindingMap pathFindingMap = null;
	
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
	
	public Vector4f getAABB() {
		if (walls.size() == 0)
			return new Vector4f();
		
		Wall wall = walls.get(0);
		float xMin = Math.min(wall.p0.x, wall.p1.x);
		float xMax = Math.max(wall.p0.x, wall.p1.x);
		float yMin = Math.min(wall.p0.y, wall.p1.y);
		float yMax = Math.max(wall.p0.y, wall.p1.y);
		for (Wall w : walls) {
			xMin = Math.min(xMin, w.p0.x);
			xMin = Math.min(xMin, w.p1.x);
			xMax = Math.max(xMax, w.p0.x);
			xMax = Math.max(xMax, w.p1.x);
			yMin = Math.min(yMin, w.p0.y);
			yMin = Math.min(yMin, w.p1.y);
			yMax = Math.max(yMax, w.p0.y);
			yMax = Math.max(yMax, w.p1.y);
		}
		Vector4f aabb = new Vector4f();
		aabb.x = xMin;
		aabb.y = yMin;
		aabb.z = xMax;
		aabb.w = yMax;
		return aabb;
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
	
	public ArrayList<Entity> getInitialEntities() {
		return initialEntities;
	}
}


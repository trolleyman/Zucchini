package game.world.physics;

import game.Util;
import game.world.entity.Entity;
import game.world.map.Map;
import game.world.map.Wall;
import game.world.physics.shape.Line;
import game.world.physics.shape.Shape;
import game.world.physics.tree.QTLeaf;
import game.world.physics.tree.QuadTree;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class PhysicsWorld {
	private QuadTree tree;
	
	/** Temporary line used for {@link #getLineOfSight(Vector2f, int, float, float[])} */
	private final Line tempLine = new Line(Entity.INVALID_ID, 0.0f, 0.0f, 0.0f, 0.0f);
	
	/** Temporary arraylist used for {@link #clean()} */
	private final ArrayList<Shape> tempShapes = new ArrayList<>();
	
	public PhysicsWorld(Map map) {
		Vector4f aabb = map.getAABB();
		tree = new QTLeaf(4, 0, aabb.x, aabb.y, aabb.z, aabb.w);
		
		for (Wall wall : map.walls)
			tree = tree.addShape(new Line(Entity.INVALID_ID, wall.p0, wall.p1));
	}
	
	public PhysicsWorld(PhysicsWorld w) {
		this.tree = w.tree.clone();
	}
	
	public void addShape(Shape s) {
		this.tree = this.tree.addShape(s);
	}
	
	public void removeShape(Shape s) {
		this.tree.removeShape(s);
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
			tempLine.setPosition(pos.x, pos.y);
			tempLine.setEnd(x, y);
			Vector2f intersection = this.getClosestIntersection(tempLine, temp);
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
	
	public ArrayList<Collision> getCollisions(Shape s, ArrayList<Collision> dest) {
		return tree.getCollisions(s, dest);
	}
	
	public Collision getClosestCollision(Shape s) {
		return tree.getClosestCollision(s);
	}
	
	public Vector2f getClosestIntersectionLine(float x0, float y0, float x1, float y1, Vector2f dest) {
		tempLine.setPosition(x0, y0);
		tempLine.setEnd(x1, y1);
		return this.getClosestIntersection(tempLine, dest);
	}
	
	public Vector2f getClosestIntersection(Shape s, Vector2f dest) {
		return tree.getClosestIntersection(s, dest);
	}
	
	public void clean() {
		tempShapes.clear();
		this.tree.removeAllDirty(tempShapes);
		for (Shape s : tempShapes) {
			this.tree.addShape(s);
		}
		tempShapes.clear();
		tree = tree.trim();
	}
	
	@Override
	public PhysicsWorld clone() {
		return new PhysicsWorld(this);
	}
}

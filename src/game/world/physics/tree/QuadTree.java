package game.world.physics.tree;

import game.world.physics.Collision;
import game.world.physics.shape.Shape;
import org.joml.Vector2f;

import java.util.ArrayList;

public abstract class QuadTree {
	
	protected int maxLevel;
	protected int level;
	
	// Position is measured from the bottom left of the aabb
	protected float x0;
	protected float y0;
	protected float x1;
	protected float y1;
	
	public QuadTree(QuadTree tree) {
		this.maxLevel = tree.maxLevel;
		this.level = tree.level;
		
		this.x0 = tree.x0;
		this.y0 = tree.y0;
		this.x1 = tree.x1;
		this.y1 = tree.y1;
	}
	
	public QuadTree(int maxLevel, int level, float x0, float y0, float x1, float y1) {
		this.maxLevel = maxLevel;
		this.level = level;
		
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}
	
	/**
	 * Adds the specified shape to the tree
	 * @param s The shape
	 * @return The new QuadTree
	 */
	public abstract QuadTree addShape(Shape s);
	
	/**
	 * Removes the specified shape from the tree
	 * @param s The shape
	 */
	public abstract void removeShape(Shape s);
	
	/**
	 * Gets the collisions that a specified shape has.
	 * @param s The shape
	 * @param dest Where to store the collisions. Can be null.
	 * @return The list of collisions. Can be null if there were none.
	 */
	public abstract ArrayList<Collision> getCollisions(Shape s, ArrayList<Collision> dest);
	
	/**
	 * Gets the closest collision to the origin of shape s
	 * @param s The shape
	 * @return null if there was no collision
	 */
	public abstract Collision getClosestCollision(Shape s);
	
	/**
	 * Gets the closest intersection to the origin of the shape s
	 * @param s The shape
	 * @param dest Where to store the intersection. Can be null.
	 * @return null if there was no intersection, otherwise the intersection point.
	 */
	public abstract Vector2f getClosestIntersection(Shape s, Vector2f dest);
	
	@Override
	public abstract QuadTree clone();
	
	/**
	 * Removes all dirty shapes from the quadtree.
	 */
	public abstract void removeAllDirty(ArrayList<Shape> dest);
	
	/**
	 * Trims the QuadTree
	 */
	public abstract QuadTree trim();
}

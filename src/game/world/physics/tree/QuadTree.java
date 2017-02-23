package game.world.physics.tree;

import game.world.physics.Collision;
import game.world.physics.shape.Shape;

import java.util.ArrayList;

public abstract class QuadTree {
	// Position is measured from the bottom left of the aabb
	protected float x0;
	protected float y0;
	protected float x1;
	protected float y1;
	
	public QuadTree(float x0, float y0, float x1, float y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}
	
	/**
	 * Adds the specified shape to the tree
	 * @param s The shape
	 */
	public abstract void addShape(Shape s);
	
	/**
	 * Gets the collisions that a specified shape has.
	 * @param s The shape
	 * @param dest Where to store the collisions. Can be null.
	 * @return The list of collisions. Can be null if there were none.
	 */
	public abstract ArrayList<Collision> getCollisions(Shape s, ArrayList<Collision> dest);
}

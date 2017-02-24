package game.world.physics;

import game.world.physics.shape.Shape;
import org.joml.Vector2f;

/**
 * Records a collision at the specified point between two shapes
 */
public class Collision {
	/** The first shape */
	public Shape a;
	/** The second shape */
	public Shape b;
	/** The point of intersection */
	public Vector2f point;
	
	public Collision(Shape a, Shape b, Vector2f point) {
		this.a = a;
		this.b = b;
		this.point = point;
	}
}

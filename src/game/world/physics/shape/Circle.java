package game.world.physics.shape;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Represents a circle shape.
 */
public class Circle extends Shape {
	private float radius;
	
	public Circle(int entityID, Vector2f position, float _radius) {
		super(entityID, position);
		this.radius = _radius;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	@Override
	public void getAABB(Vector4f dest) {
		dest.set(getPositionX() - radius, getPositionY() - radius,
		         getPositionX() + radius, getPositionY() + radius);
	}
}

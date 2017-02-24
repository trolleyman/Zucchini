package game.world.physics.shape;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Represents a circle shape.
 */
public class Circle extends Shape {
	private float radius;
	
	public Circle(Circle c) {
		super(c);
		
		this.radius = c.radius;
	}
	
	public Circle(int entityID, Vector2f position, float _radius) {
		super(entityID, position);
		this.radius = _radius;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
		this.dirty();
	}
	
	@Override
	protected void calculateAABB(Vector4f dest) {
		dest.set(getPosition().x - radius, getPosition().y - radius,
		         getPosition().x + radius, getPosition().y + radius);
	}
	
	@Override
	public Circle clone() {
		return new Circle(this);
	}
}

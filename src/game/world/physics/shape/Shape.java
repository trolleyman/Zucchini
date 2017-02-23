package game.world.physics.shape;

import game.world.physics.PhysicsUtil;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class Shape {
	private final int entityID;
	
	private final Vector2f position = new Vector2f();
	
	private boolean dirty = true;
	
	public Shape(int _entityID, Vector2f _position) {
		this(_entityID, _position.x, _position.y);
	}
	
	public Shape(int _entityID, float x, float y) {
		this.entityID = _entityID;
		this.position.set(x, y);
	}
	
	public void setPosition(Vector2f _position) {
		this.position.set(_position);
		
		this.dirty = true;
	}
	
	public float getPositionX() {
		return position.x;
	}
	
	public float getPositionY() {
		return position.y;
	}
	
	/**
	 * Gets the entity ID associated with this Shape.
	 */
	public int getEntityID() {
		return entityID;
	}
	
	/**
	 * Gets the Axis-Aligned Bounding Box for the shape.
	 * @param dest Where to store the result.
	 */
	public abstract void getAABB(Vector4f dest);
	
	public Vector4f getAABB() {
		Vector4f dest = new Vector4f();
		this.getAABB(dest);
		return dest;
	}
	
	/**
	 * Calculates an intersection between this shape and another.
	 * @param other The other shape
	 * @param dest The place to store the intersection point. Can be null.
	 * @return null if there was no collision, otherwise the intersection point
	 */
	public Vector2f queryCollision(Shape other, Vector2f dest) {
		return PhysicsUtil.intersectShapeShape(this, other, dest);
	}
}

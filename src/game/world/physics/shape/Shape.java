package game.world.physics.shape;

import game.world.physics.PhysicsUtil;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class Shape {
	private int entityID;
	
	private final Vector2f position = new Vector2f();
	private float angle;
	
	private final Vector4f aabb = new Vector4f();
	private boolean aabbDirty = true;
	private boolean dirty = true;
	
	public Shape(int _entityID, Vector2f _position) {
		this(_entityID, _position.x, _position.y);
	}
	
	public Shape(int _entityID, float x, float y) {
		this.entityID = _entityID;
		this.position.set(x, y);
		this.angle = 0.0f;
	}
	
	public Shape(Shape s) {
		this.entityID = s.entityID;
		
		this.position.set(s.position);
		this.angle = s.angle;
		
		this.aabbDirty = s.aabbDirty;
		this.dirty = s.dirty;
	}
	
	public void dirty() {
		this.aabbDirty = true;
		this.dirty = true;
	}
	
	public void clean() {
		this.dirty = false;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
		this.dirty();
	}
	
	public float getAngle() {
		return angle;
	}
	
	public void setPosition(Vector2f pos) {
		this.setPosition(pos.x, pos.y);
	}
	
	public void setPosition(float x, float y) {
		this.position.set(x, y);
		this.dirty();
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	/**
	 * Gets the entity ID associated with this Shape.
	 */
	public int getEntityID() {
		return entityID;
	}
	
	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}
	
	protected abstract void calculateAABB(Vector4f dest);
	
	/**
	 * Gets the Axis-Aligned Bounding Box for the shape.
	 */
	public Vector4f getAABB() {
		if (this.aabbDirty)
			calculateAABB(this.aabb);
		
		return this.aabb;
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
	
	@Override
	public abstract Shape clone();
}

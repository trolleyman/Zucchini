package game.world.physics.shape;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Line extends Shape {
	private Vector2f end = new Vector2f();
	
	public Line(int entityID, Vector2f p0, Vector2f p1) {
		super(entityID, p0);
		this.end.set(p1);
	}
	
	public Line(int entityID, float x0, float y0, float x1, float y1) {
		super(entityID, x0, y0);
		this.end.set(x1, y1);
	}
	
	public float getEndX() {
		return this.end.x;
	}
	
	public float getEndY() {
		return this.end.y;
	}
	
	@Override
	public void setPosition(Vector2f position) {
		float dx = position.x - this.getPositionX();
		float dy = position.y - this.getPositionY();
		this.end.add(dx, dy);
		super.setPosition(position);
	}
	
	@Override
	public void getAABB(Vector4f dest) {
		float xMin = Math.min(this.getPositionX(), this.end.x);
		float xMax = Math.max(this.getPositionX(), this.end.x);
		float yMin = Math.min(this.getPositionY(), this.end.y);
		float yMax = Math.max(this.getPositionY(), this.end.y);
		dest.set(xMin, yMin, xMax, yMax);
	}
}

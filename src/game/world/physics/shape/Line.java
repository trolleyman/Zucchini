package game.world.physics.shape;

import game.Util;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Line extends Shape {
	private final Vector2f diff = new Vector2f();
	
	private boolean endDirty;
	private final Vector2f end = new Vector2f();
	
	public Line(int entityID, Vector2f p0, Vector2f p1) {
		this(entityID, p0.x, p0.y, p1.x, p1.y);
	}
	
	public Line(int entityID, float x0, float y0, float x1, float y1) {
		super(entityID, x0, y0);
		endDirty = false;
		this.end.set(x0, y0).sub(x1, y1);
	}
	
	public Line(Line line) {
		super(line);
		this.diff.set(line.diff);
		this.endDirty = line.endDirty;
		this.end.set(line.end);
	}
	
	public void setEnd(Vector2f end) {
		this.setEnd(end.x, end.y);
	}
	
	public void setEnd(float x, float y) {
		this.endDirty = false;
		this.diff.set(x, y).sub(this.getPosition());
		float s = (float)Math.sin(-this.getAngle());
		float c = (float)Math.cos(-this.getAngle());
		float nx = x * c - y * s;
		float ny = x * s + y * c;
		this.diff.set(nx, ny);
		this.end.set(x, y);
	}
	
	public Vector2f getEnd() {
		if (this.endDirty) {
			// Calculate end from diff, position and angle.
			float s = (float)Math.sin(this.getAngle());
			float c = (float)Math.cos(this.getAngle());
			float x = diff.x * c - diff.y * s;
			float y = diff.x * s + diff.y * c;
			this.end.set(x, y).add(this.getPosition());
		}
		this.endDirty = false;
		return end;
	}
	
	@Override
	protected void calculateAABB(Vector4f dest) {
		float xMin = Math.min(this.getPosition().x, this.end.x);
		float xMax = Math.max(this.getPosition().x, this.end.x);
		float yMin = Math.min(this.getPosition().y, this.end.y);
		float yMax = Math.max(this.getPosition().y, this.end.y);
		dest.set(xMin, yMin, xMax, yMax);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		this.endDirty = true;
	}
	
	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
		this.endDirty = true;
	}
	
	@Override
	public Line clone() {
		return new Line(this);
	}
}

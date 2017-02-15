package game.world.map;

import org.joml.Vector2f;

public class Wall {
	public Vector2f p0;
	public Vector2f p1;
	
	public Wall(float x0, float y0, float x1, float y1) {
		this(new Vector2f(x0, y0), new Vector2f(x1, y1));
	}
	
	public Wall(Vector2f _p0, Vector2f _p1) {
		this.p0 = _p0;
		this.p1 = _p1;
	}
}

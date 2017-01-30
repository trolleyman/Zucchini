package game.world.entity;

import org.joml.Vector2f;

import game.render.IRenderer;

public abstract class Entity {
	public static int INVALID_ID = -1;
	private int id = INVALID_ID;
	
	public Vector2f position;
	
	/**
	 * The angle clockwise in radians from the north direction.
	 */
	public float angle;
	
	public Entity(Vector2f _position) {
		this.position = _position;
	}
	
	public abstract void update(double dt);
	public abstract void render(IRenderer r);

	public int getId() {
		return id;
	}
	public void setId(int _id) {
		this.id = _id;
	}
}

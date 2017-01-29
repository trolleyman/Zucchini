package game.world;

import org.joml.Vector2f;

import game.render.IRenderer;

public abstract class Entity {
	public Vector2f position;
	
	public Entity(Vector2f _position) {
		this.position = _position;
	}
	
	public abstract void update(double dt);
	public abstract void render(IRenderer r);
}

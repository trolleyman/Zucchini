package game.world.update;

import game.world.entity.Entity;
import org.joml.Vector2f;

public class PositionUpdate extends EntityUpdate {
	private Vector2f position;
	
	public PositionUpdate(int id, Vector2f _position) {
		super(id, false);
		this.position = _position;
	}
	
	public PositionUpdate(PositionUpdate update) {
		super(update);
		this.position = update.position;
	}
	
	@Override
	public void updateEntity(Entity e) {
		e.position = this.position;
	}
	
	@Override
	public PositionUpdate clone() {
		return new PositionUpdate(this);
	}
}

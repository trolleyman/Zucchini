package game.world.entity.update;

import game.world.entity.Entity;
import org.joml.Vector2f;

public class PositionUpdate extends EntityUpdate {
	private static int tidMax = 1;
	
	private Vector2f position;
	private int tid;
	
	public PositionUpdate(int id, Vector2f _position) {
		super(id, false);
		this.position = _position;
		this.tid = tidMax++;
	}
	
	public PositionUpdate(PositionUpdate update) {
		super(update);
		this.position = update.position;
		this.tid = update.tid;
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

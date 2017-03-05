package game.world.entity.update;

import game.world.entity.Entity;
import game.world.entity.MovableEntity;
import org.joml.Vector2f;

public class VelocityUpdate extends EntityUpdate {
	private Vector2f velocity;
	
	public VelocityUpdate(int id, Vector2f _velocity) {
		super(id, false);
		
		this.velocity = _velocity;
	}
	
	public VelocityUpdate(VelocityUpdate update) {
		super(update);
		this.velocity = update.velocity;
	}
	
	@Override
	public void updateEntity(Entity e) {
		if (e instanceof MovableEntity) {
			((MovableEntity) e).velocity = this.velocity;
		} else {
			System.err.println("Warning: Non-movable entity velocity updated");
		}
	}
	
	@Override
	public VelocityUpdate clone() {
		return new VelocityUpdate(this);
	}
}

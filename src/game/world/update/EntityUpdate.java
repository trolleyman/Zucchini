package game.world.update;

import game.world.entity.Entity;
import game.world.physics.PhysicsWorld;

public abstract class EntityUpdate {
	private int id;
	
	public EntityUpdate(int _id) {
		this.id = _id;
	}
	
	public EntityUpdate(EntityUpdate update) {
		this.id = update.id;
	}
	
	public int getId() {
		return id;
	}
	
	public abstract void applyUpdate(PhysicsWorld physics, Entity e);
	
	@Override
	public abstract EntityUpdate clone();
}

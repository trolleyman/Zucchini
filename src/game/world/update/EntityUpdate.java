package game.world.update;

import game.world.entity.Entity;

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
	
	public abstract void updateEntity(Entity e);
	
	@Override
	public abstract EntityUpdate clone();
}

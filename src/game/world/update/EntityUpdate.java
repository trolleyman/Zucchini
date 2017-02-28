package game.world.update;

import game.world.entity.Entity;

public abstract class EntityUpdate {
	private int id;
	/** Does the EntityUpdate need to be delivered over TCP, for reliability? */
	private transient boolean tcp;
	
	public EntityUpdate(int _id, boolean _tcp) {
		this.id = _id;
		this.tcp = _tcp;
	}
	
	public EntityUpdate(EntityUpdate update) {
		this.id = update.id;
		this.tcp = update.tcp;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isTcp() {
		return tcp;
	}
	
	public abstract void updateEntity(Entity e);
	
	@Override
	public abstract EntityUpdate clone();
}

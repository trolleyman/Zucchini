package game.ai;

import game.world.ServerWorld;
import game.world.World;
import game.world.entity.Entity;

public abstract class AI {
	private Entity entity;
	
	public AI(Entity _entity) {
		this.entity = _entity;
	}
	public abstract void update(ServerWorld w, double dt);
	
	public Entity getEntity() {
		return entity;
	}
}

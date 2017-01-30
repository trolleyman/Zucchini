package game.ai;

import game.world.World;
import game.world.entity.Entity;

public abstract class EntityController {
	private Entity entity;
	
	public EntityController(Entity _entity) {
		this.entity = _entity;
	}
	public abstract void update(World w, double dt);
	
	public Entity getEntity() {
		return entity;
	}
}

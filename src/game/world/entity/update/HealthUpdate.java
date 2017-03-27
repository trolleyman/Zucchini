package game.world.entity.update;

import game.world.entity.Entity;

public class HealthUpdate extends EntityUpdate {
	private float health;
	
	public HealthUpdate(int id, float _health) {
		super(id, true);
		this.health = _health;
	}
	
	public float getHealth() {
		return health;
	}
	
	@Override
	public void updateEntity(Entity e) {
		e.addHealth(health);
	}
}

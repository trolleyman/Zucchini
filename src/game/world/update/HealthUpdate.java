package game.world.update;

import game.world.entity.Entity;

public class HealthUpdate extends EntityUpdate {
	private float health;
	
	public HealthUpdate(int id, float _health) {
		super(id);
		this.health = _health;
	}
	
	public HealthUpdate(HealthUpdate hu) {
		super(hu);
		this.health = hu.health;
	}
	
	@Override
	public void updateEntity(Entity e) {
		e.addHealth(health);
	}
	
	@Override
	public HealthUpdate clone() {
		return new HealthUpdate(this);
	}
}
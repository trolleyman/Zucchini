package game.world.entity.update;

import game.world.entity.Entity;
import game.world.entity.damage.Damage;

public class DamageUpdate extends EntityUpdate {
	
	private Damage damage;
	
	public DamageUpdate(DamageUpdate u) {
		super(u);
		this.damage = u.damage;
	}
	
	public DamageUpdate(int id, Damage damage) {
		super(id, true);
		this.damage = damage;
	}
	
	@Override
	public void updateEntity(Entity e) {
		e.addDamage(damage);
	}
	
	@Override
	public EntityUpdate clone() {
		return new DamageUpdate(this);
	}
}

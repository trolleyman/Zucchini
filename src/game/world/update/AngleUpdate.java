package game.world.update;

import game.world.entity.Entity;

public class AngleUpdate extends EntityUpdate {
	private float angle;
	
	public AngleUpdate(int id, float _angle) {
		super(id);
		
		this.angle = _angle;
	}
	
	public AngleUpdate(AngleUpdate update) {
		super(update);
		
		this.angle = update.angle;
	}
	
	@Override
	public void updateEntity(Entity e) {
		e.angle = this.angle;
	}
	
	@Override
	public AngleUpdate clone() {
		return new AngleUpdate(this);
	}
}

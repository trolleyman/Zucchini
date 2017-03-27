package game.world.entity.update;

import game.world.entity.Entity;

public class AngleUpdate extends EntityUpdate {
	private float angle;
	
	public AngleUpdate(int id, float _angle) {
		super(id, false);
		
		this.angle = _angle;
	}
	
	@Override
	public void updateEntity(Entity e) {
		e.angle = this.angle;
	}
	
	public float getAngle() {
		return angle;
	}
}

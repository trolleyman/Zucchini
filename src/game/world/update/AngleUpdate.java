package game.world.update;

import game.world.entity.Entity;
import game.world.physics.PhysicsWorld;
import game.world.physics.shape.Shape;

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
	public void applyUpdate(PhysicsWorld physics, Entity e) {
		Shape s = e.getShape();
		if (s != null) {
			physics.removeShape(s);
			s.setAngle(this.angle);
			physics.addShape(s);
		}
		e.angle = this.angle;
	}
	
	@Override
	public AngleUpdate clone() {
		return new AngleUpdate(this);
	}
}

package game.world.update;

import game.world.entity.Entity;
import game.world.physics.PhysicsWorld;
import game.world.physics.shape.Shape;
import org.joml.Vector2f;

public class PositionUpdate extends EntityUpdate {
	private Vector2f position;
	
	public PositionUpdate(int id, Vector2f _position) {
		super(id);
		this.position = _position;
	}
	
	public PositionUpdate(PositionUpdate update) {
		super(update);
		this.position = update.position;
	}
	
	@Override
	public void applyUpdate(PhysicsWorld physics, Entity e) {
		Shape s = e.getShape();
		if (s != null) {
			physics.removeShape(s);
			s.setPosition(this.position);
			physics.addShape(s);
		}
		e.position = this.position;
	}
	
	@Override
	public PositionUpdate clone() {
		return new PositionUpdate(this);
	}
}

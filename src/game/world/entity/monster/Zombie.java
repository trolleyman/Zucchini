package game.world.entity.monster;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.physics.Collision;
import game.world.physics.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.AutonomousEntity;
import game.world.entity.Entity;
import game.world.map.PathFindingMap;
import game.world.physics.shape.Circle;
import game.world.update.PositionUpdate;
import org.joml.Vector2f;

import java.util.ArrayList;

public class Zombie extends AutonomousEntity {
	private static final float MAX_SPEED = 1.0f;
	private static final float RADIUS = 0.15f;
	
	public Zombie(Vector2f position) {
		super(Team.MONSTER_TEAM, new Circle(Entity.INVALID_ID, position, RADIUS), position, 1.0f, MAX_SPEED);
	}
	
	public Zombie(Zombie z) {
		super(z);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		
		// Set node
		Entity kill = ua.bank.getClosestHostileEntity(position.x, position.y, this.getTeam());
		if (kill == null) {
			this.setDestination(pfmap, null);
		} else {
			this.setDestination(pfmap, kill.position);
		}
		
		// Update AI
		super.update(ua);
		
		// TODO: Not DRY enough - see Player#update(UpdateArgs)
		// Get intersections
		ArrayList<Collision> collisions = ua.physics.getCollisions(this.shape, null);
		if (collisions.size() != 0) {
			// Intersection - push out
			Vector2f newPosition = new Vector2f();
			for (Collision c : collisions) {
				newPosition.set(position)
						.sub(c.point)
						.normalize()
						.mul(RADIUS + Util.EPSILON)
						.add(c.point);
				this.position.set(newPosition);
			}
			ua.bank.updateEntityCached(new PositionUpdate(this.getId(), newPosition));
			//ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), new Vector2f()));
		}
	}
	
	@Override
	public void render(IRenderer r) {
		float x = position.x + 0.25f * (float) Math.sin(angle);
		float y = position.y + 0.25f * (float) Math.cos(angle);
		
		r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
	}
	
	@Override
	protected float getMaxHealth() {
		return 10.0f;
	}
	
	@Override
	public Zombie clone() {
		return new Zombie(this);
	}
}

package game.world.entity.monster;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.AutonomousEntity;
import game.world.entity.Entity;
import game.world.map.PathFindingMap;
import game.world.update.PositionUpdate;
import org.joml.Vector2f;

public class Zombie extends AutonomousEntity {
	private static final float MAX_SPEED = 1.0f;
	private static final float RADIUS = 0.15f;
	private boolean soundSourceInit = false;
	private int zombieSoundID;
	private int walkingSoundID;
	
	public Zombie(Vector2f position) {
		super(Team.MONSTER_TEAM, position, 1.0f, MAX_SPEED);
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
		
		// Calculate intersection
		// TODO: Not DRY enough - see Player#update(UpdateArgs)
		Vector2f intersection = Util.pushTemporaryVector2f();
		if (ua.map.intersectsCircle(position.x, position.y, RADIUS, intersection) != null) {
			// Intersection with map - push out
			Vector2f newPosition = new Vector2f();
			newPosition.set(position)
					.sub(intersection)
					.normalize()
					.mul(RADIUS + Util.EPSILON)
					.add(intersection);
			ua.bank.updateEntityCached(new PositionUpdate(this.getId(), newPosition));
			//ua.bank.updateEntityCached(new VelocityUpdate(this.getId(), new Vector2f()));
		}
		
		if (!soundSourceInit) {
			this.zombieSoundID = ua.audio.playLoop("zombie1.wav", 0.6f,this.position);
			this.walkingSoundID = ua.audio.playLoop("footsteps_running.wav", 0.1f,this.position);
			ua.audio.pauseLoop(zombieSoundID);
			ua.audio.pauseLoop(walkingSoundID);
			soundSourceInit = true;
		}
		
		// Play zombie sounds
		ua.audio.continueLoop(this.zombieSoundID,this.position);
		ua.audio.continueLoop(this.walkingSoundID,this.position);

		Util.popTemporaryVector2f();
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
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(position.x, position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public Zombie clone() {
		return new Zombie(this);
	}
}
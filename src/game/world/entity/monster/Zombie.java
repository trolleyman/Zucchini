package game.world.entity.monster;

import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.PhysicsUtil;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.AutonomousEntity;
import game.world.entity.Entity;
import game.world.entity.Player;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageSource;
import game.world.entity.damage.DamageType;
import game.world.entity.update.AngleUpdate;
import game.world.entity.update.DamageUpdate;
import game.world.entity.update.PositionUpdate;
import game.world.map.Map;
import game.world.map.PathFindingMap;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Random;

public class Zombie extends AutonomousEntity {
	private static final float LINE_OF_SIGHT_MAX = 10.0f;
	private static final float MAX_SPEED = 1.0f;
	private static final float RADIUS = 0.2f;
	
	private transient boolean soundSourceInit = false;
	private transient int zombieSoundID;
	private transient float attackCooldown = 0.0f;
	private double time = 0.0;
	
	public Zombie(Vector2f position) {
		super(Team.MONSTER_TEAM, position, RADIUS, 1.0f, MAX_SPEED, true);
	}
	
	public Zombie(Zombie z) {
		super(z);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		time += ua.dt;
		attackCooldown += ua.dt;
		
		PathFindingMap pfmap = ua.map.getPathFindingMap();
		if (attackCooldown > 0.5f) {
			ArrayList<Entity> entities = ua.bank.getEntitiesNear(this.position.x, this.position.y, 0.5f);
			
			for (Entity e : entities) {
				if (e.getTeam() >= Team.FIRST_PLAYER_TEAM && attackCooldown > 0.5f) {
					Damage damage = new Damage(new DamageSource(this), DamageType.ZOMBIE_DAMAGE, 1.0f);
					ua.bank.updateEntityCached(new DamageUpdate(e.getId(), damage));
					attackCooldown = 0;
				}
			}
		}
		
		if (time >= 0.5f) {
			Vector2f temp = Util.pushTemporaryVector2f();
			Entity kill = ua.bank.getClosestEntity(position.x, position.y,
					(e) -> Team.isHostileTeam(this.getTeam(), e.getTeam())
							&& e.position.distance(position) <= LINE_OF_SIGHT_MAX
							&& ua.map.intersectsLine(position.x, position.y, e.position.x, e.position.y, temp) == null);
			Util.popTemporaryVector2f();
			
			if (kill != null) {
				this.setDestination(pfmap, kill.position);
				enabled = true;
			}
			time = 0.0;
		}
		super.update(ua);
		
		ua.bank.updateEntityCached(new AngleUpdate(this.getId(), Util.getAngle(velocity.x, velocity.y)));
		
		if (!soundSourceInit) {
			Random rng = new Random();
			this.zombieSoundID = ua.audio.play("zombie" + (rng.nextInt(3) + 1) + ".wav", 1f, this.position);
			//this.walkingSoundID = ua.audio.play("footsteps_running.wav", 0.1f,this.position);
			//System.out.println("found zombie footstep sound "+walkingSoundID+" for zombie "+this.getId());
			ua.audio.pauseLoop(zombieSoundID);
			//ua.audio.pauseLoop(walkingSoundID);
			soundSourceInit = true;
		}
		
		// Play zombie sounds
		ua.audio.continueLoop(this.zombieSoundID, this.position);
		//ua.audio.continueLoop(this.walkingSoundID,this.position);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		float x = position.x + 0.25f * (float) Math.sin(angle);
		float y = position.y + 0.25f * (float) Math.cos(angle);
		
		//r.drawLine(position.x, position.y, x, y, ColorUtil.RED, 1.0f);
		//r.drawCircle(position.x, position.y, RADIUS, ColorUtil.GREEN);
		r.drawTexture(r.getTextureBank().getTexture("zombie_v1.png"), Align.MM, position.x, position.y, RADIUS * 2, RADIUS * 2, angle);
	}
	
	@Override
	public String getReadableName() {
		return "a zombie";
	}
	
	@Override
	public float getMaxHealth() {
		return 10.0f;
	}
	
	@Override
	public Vector2f intersects(float x0, float y0, float x1, float y1) {
		return PhysicsUtil.intersectCircleLine(position.x, position.y, RADIUS, x0, y0, x1, y1, null);
	}
	
	@Override
	public void death(UpdateArgs ua) {
		super.death(ua);
		Damage d = getLastDamage();
		if (d.source.entityId != Entity.INVALID_ID && d.source.isPlayer) {
			ua.scoreboard.addMonsterKill(d.source.readableName);
		}
	}
	
	@Override
	public Zombie clone() {
		return new Zombie(this);
	}
}

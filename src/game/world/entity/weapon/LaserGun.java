package game.world.entity.weapon;

import game.world.entity.Entity;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageType;
import game.world.entity.update.DamageUpdate;
import game.world.entity.update.HealthUpdate;
import game.world.map.Wall;
import org.joml.Vector2f;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;

import java.util.ArrayList;

public class LaserGun extends Weapon {
	private static final int MAX_REFLECTIONS = 3;
	private static final float MAX_LASER_LENGTH = 10.0f;
	
	private transient Wall prevWall = null;
	private transient Vector2f curPos = null;
	
	public LaserGun(LaserGun g) {
		super(g);
	}
	
	public LaserGun(Vector2f position, int ammo) {
		super(position, ammo, true, 0.5f, 1, 0.5f);
	}
	
	@Override
	public void render(IRenderer r) {
		Align a = isHeld() ? Align.BM : Align.MM;
		r.drawBox(a, position.x, position.y, 0.15f, getHeight(), ColorUtil.RED, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.RED);
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	protected void fire(UpdateArgs ua, float fangle) {
		// Play audio
		ua.audio.play("lasergun-fire.wav", 1.0f, this.position);
		
		// Fire laser segments
		prevWall = null;
		Vector2f curDir = Util.pushTemporaryVector2f().set(Util.getDirX(fangle), Util.getDirY(fangle));
		curPos = new Vector2f(Util.getDirX(fangle), Util.getDirY(fangle)).mul(getHeight()).add(position);
		float lengthLeft = MAX_LASER_LENGTH;
		for (int i = 0; i < MAX_REFLECTIONS && lengthLeft > 0.0; i++) {
			// Calc new max pos
			Vector2f newPos = new Vector2f(curDir).mul(lengthLeft).add(curPos);
			
			// Calculate intersection
			Wall wall = new Wall(0.0f, 0.0f, 0.0f, 0.0f);
			Vector2f intersection = ua.map.intersectsLine(curPos.x, curPos.y, newPos.x, newPos.y, newPos, wall,
					(w) -> prevWall == null || !w.p0.equals(prevWall.p0) && !w.p1.equals(prevWall.p1));
			
			float newLengthLeft = lengthLeft - curPos.distance(newPos);
			
			// Spawn new segment
			ua.bank.addEntityCached(new LaserBulletSegment(this.ownerTeam,
					curPos, lengthLeft/MAX_LASER_LENGTH,
					newPos, newLengthLeft/MAX_LASER_LENGTH));
			
			// Damage entities that contact with laser
			// TODO: Fix so that entities can collide with outer extents of laser
			ArrayList<Entity> es = ua.bank.getEntities((e) -> e.intersects(curPos.x, curPos.y, newPos.x, newPos.y) != null);
			if (es != null) {
				for (Entity e : es) {
					// Only damage if the id is not who shot the laser, unless it is after the first shot
					if (e.getId() != this.ownerId || prevWall != null) {
						Damage damage = new Damage(ownerId, ownerTeam, DamageType.LASER_DAMAGE, 10.0f);
						ua.bank.updateEntityCached(new DamageUpdate(e.getId(), damage));
					}
				}
			}
			
			if (intersection == null)
				break;
			
			// Calculate reflection angle
			// r = v - 2*(dot(v, n))*n
			float dx = wall.p1.x - wall.p0.x;
			float dy = wall.p1.y - wall.p0.y;
			Vector2f normal = Util.pushTemporaryVector2f().set(dy, -dx).normalize();
			
			Vector2f r = Util.pushTemporaryVector2f();
			r.set(normal).mul(2).mul(normal.dot(curDir)).negate().add(curDir);
			curDir.set(r).normalize();
			
			Util.popTemporaryVector2f();
			Util.popTemporaryVector2f();
			
			// Update loop variables for next segment
			lengthLeft = newLengthLeft;
			curPos = newPos;
			if (prevWall == null)
				prevWall = new Wall(0.0f, 0.0f, 0.0f, 0.0f);
			prevWall.p0.set(wall.p0);
			prevWall.p1.set(wall.p1);
		}
		Util.popTemporaryVector2f();
	}
	
	private float getHeight() {
		return 0.35f;
	}
	
	@Override
	public LaserGun clone() {
		return new LaserGun(this);
	}
}

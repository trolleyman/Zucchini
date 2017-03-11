package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import org.joml.Vector2f;

public class LaserBulletSegment extends Entity {
	/** The tile to live that this entity has */
	public double ttl;
	
	public float length;
	
	public LaserBulletSegment(LaserBulletSegment b) {
		super(b);
		
		this.length = b.length;
	}
	
	public LaserBulletSegment(int team, Vector2f start, Vector2f end) {
		this(team, start, Util.getAngle(start.x, start.y, end.x, end.y), start.distance(end));
	}
	
	public LaserBulletSegment(int team, Vector2f position, float angle, float length) {
		super(team, position);
		
		this.ttl = 1.0f;
		this.angle = angle;
		this.length = length;
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		this.ttl -= ua.dt;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		this.ttl -= ua.dt;
		
		if (this.ttl <= 0.0) {
			this.ttl = 0.0;
			ua.bank.removeEntityCached(this.getId());
		}
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.BM, position.x, position.y, 0.1f, this.length, ColorUtil.RED, angle);
	}
	
	@Override
	public LaserBulletSegment clone() {
		return new LaserBulletSegment(this);
	}
}

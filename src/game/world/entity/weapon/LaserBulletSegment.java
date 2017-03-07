package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LaserBulletSegment extends Entity {
	private static final double TTL = 1.0f;
	private static final float WIDTH = 0.1f;
	
	/** The time since the construction of the segment */
	private double time;
	
	private float length;
	
	private float startGrad;
	private float endGrad;
	
	private transient Vector4f color;
	
	public LaserBulletSegment(LaserBulletSegment b) {
		super(b);
		
		this.time = b.time;
		this.length = b.length;
		
		this.startGrad = b.startGrad;
		this.endGrad = b.endGrad;
	}
	
	public LaserBulletSegment(int team, Vector2f start, float startGrad, Vector2f end, float endGrad) {
		this(team, start, Util.getAngle(start.x, start.y, end.x, end.y), start.distance(end), startGrad, endGrad);
	}
	
	public LaserBulletSegment(int team, Vector2f position, float angle, float length, float startGrad, float endGrad) {
		super(team, position);
		
		this.time = 0.0;
		this.angle = angle;
		this.length = length;
		
		this.startGrad = startGrad;
		this.endGrad = endGrad;
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		this.time += ua.dt;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		this.time += ua.dt;
		
		if (this.time > TTL) {
			this.time = TTL;
			ua.bank.removeEntityCached(this.getId());
		}
	}
	
	@Override
	public void render(IRenderer r) {
		if (color == null)
			color = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		color.w = 1 - (float)(time / TTL);
		Texture t = r.getTextureBank().getTexture("laserBullet.png");
		r.drawTextureUV(t, Align.BM, position.x, position.y, WIDTH, length, angle,  0.0f, endGrad, 1.0f, startGrad, color);
	}
	
	@Override
	public LaserBulletSegment clone() {
		return new LaserBulletSegment(this);
	}
}

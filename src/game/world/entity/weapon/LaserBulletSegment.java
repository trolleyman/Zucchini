package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.light.TubeLight;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LaserBulletSegment extends Entity {
	private static final double TTL = 1.0f;
	
	/** The time since the construction of the segment */
	private double time;
	
	private float length;
	
	private float startGrad;
	private float endGrad;
	
	private transient TubeLight light;
	
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
		
		if (this.time >= TTL) {
			this.time = TTL;
			ua.bank.removeEntityCached(this.getId());
		}
	}
	
	public void updateLightParams() {
		if (light == null)
			light = new TubeLight(new Vector2f(), 0.0f, 0.0f, 0.0f, ColorUtil.RED, 0.0f);
		
		light.position.set(this.position);
		light.angle = this.angle;
		light.length = this.length;
		light.color.w = 0.8f - 0.8f*(float)(time / TTL);
		light.width = 1.0f;
		light.attenuationFactor = 200.0f;
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		updateLightParams();
		light.render(r, map);
		//Texture t = r.getTextureBank().getTexture("laserBullet.png");
		//r.drawTextureUV(t, Align.BM, position.x, position.y, WIDTH, length, angle,  0.0f, endGrad, 1.0f, startGrad, color);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		updateLightParams();
		light.renderLight(r, map);
	}
	
	@Override
	public void renderGlitch(IRenderer r, Map map) {
		updateLightParams();
		light.color.w /= 2.0f;
		light.render(r, map);
	}
	
	@Override
	public LaserBulletSegment clone() {
		return new LaserBulletSegment(this);
	}
}

package game.world.entity.effect;

import game.ColorUtil;
import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.light.PointLight;
import game.world.map.Map;
import org.joml.Vector2f;

public class LaserMuzzleFlash extends Entity {
	private static float TTL = 0.3f;
	
	private double time;
	
	private PointLight light;
	
	public LaserMuzzleFlash(LaserMuzzleFlash l) {
		super(l);
		
		time = l.time;
		light = l.light.clone();
	}
	
	public LaserMuzzleFlash(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
		light = new PointLight(position, ColorUtil.WHITE, 20.0f, false);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		time += ua.dt;
		if (time >= TTL)
			ua.bank.removeEntityCached(this.getId());
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		time += ua.dt;
	}
	
	@Override
	public void render(IRenderer r, Map map) {}
	
	@Override
	public void renderGlitch(IRenderer r, Map map) {
		float p = 1-(float)(time/TTL);
		light.color.w = p;
		light.attenuationFactor = 20.0f;
		light.render(r, map);
	}
	
	@Override
	public LaserMuzzleFlash clone() {
		return new LaserMuzzleFlash(this);
	}
}

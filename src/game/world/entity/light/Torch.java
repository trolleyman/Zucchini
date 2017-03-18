package game.world.entity.light;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Torch extends Entity {
	
	private PointLight light;
	
	private transient double timeSinceRandom = Double.POSITIVE_INFINITY;
	private transient float random1;
	private transient float random2;
	private transient float intensity;
	
	public Torch(Torch t) {
		super(t);
		light = t.light.clone();
	}
	
	public Torch(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
		light = new PointLight(position, new Vector4f(227.0f/255.0f, 140.0f/255.0f, 45.0f/255.0f, 1.0f), 1.0f, false);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		// Calculate flame
		timeSinceRandom += ua.dt;
		if (timeSinceRandom > 0.05f) {
			timeSinceRandom = 0.0;
			random1 = (float)Math.random();
			random2 = (float)Math.random();
		}
		intensity += (float)ua.dt * (random1 - 0.5f) * 1.0f;
		intensity += (float)ua.dt * (random2 - 0.5f) * 3.0f;
		//intensity = (System.currentTimeMillis()%1000) / 1000.0f;
		
		// Clamp intensity
		intensity = Math.min(1.0f, Math.max(0.0f, intensity));
		light.clientUpdate(ua);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		light.update(ua);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		light.color.w = 0.25f + intensity*0.15f;
		light.attenuationFactor = 0.3f-intensity*0.05f;
		light.render(r, map);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		light.color.w = 0.25f + intensity*0.15f;
		light.attenuationFactor = 0.3f-intensity*0.05f;
		light.renderLight(r, map);
	}
	
	@Override
	public Torch clone() {
		return new Torch(this);
	}
}

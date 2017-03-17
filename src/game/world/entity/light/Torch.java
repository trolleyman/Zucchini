package game.world.entity.light;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.LightUtil;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Torch extends Entity {
	
	private PointLight light;
	
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
		light.clientUpdate(ua);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		light.update(ua);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		light.color.w = 1.0f;
		light.attenuationFactor = 1.0f;
		light.render(r, map);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		light.color.w = 1.0f;
		light.attenuationFactor = 1.0f;
		light.renderLight(r, map);
	}
	
	@Override
	public Torch clone() {
		return new Torch(this);
	}
}

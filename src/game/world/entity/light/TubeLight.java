package game.world.entity.light;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.LightUtil;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

public class TubeLight extends Entity {
	public float length;
	public float width;
	
	public final Vector4f color = new Vector4f();
	public float attenuationFactor;
	
	/**
	 * Constructs a new tube light
	 * @param position The intitial position of the light
	 * @param angle The angle of the light
	 * @param length The length of the light
	 * @param width The width of the light
	 * @param color The color intensity of the light
	 * @param attenuationFactor The attenuation factor of the light
	 */
	public TubeLight(Vector2f position, float angle, float length, float width, Vector4f color, float attenuationFactor) {
		super(Team.PASSIVE_TEAM, position);
		this.angle = angle;
		this.length = length;
		this.width = width;
		
		this.color.set(color);
		this.attenuationFactor = attenuationFactor;
	}
	
	@Override
	public void update(UpdateArgs ua) {}
	
	@Override
	public void render(IRenderer r, Map map) {
		r.drawTubeLight(position.x, position.y, angle, length, width, color, attenuationFactor);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		render(r, map);
	}
}

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
	/** The intensity multiplier below at which the intensity is assumed to be 0. */
	private static final float CUTOFF_INTENSITY = 0.01f;
	
	public float length;
	public float width;
	
	public Vector4f color;
	public float attenuationFactor;
	
	public TubeLight(TubeLight l) {
		super(l);
		
		this.length = l.length;
		this.width = l.width;
		
		this.color = l.color;
		this.attenuationFactor = l.attenuationFactor;
	}
	
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
		
		this.color = color;
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
		super.renderLight(r, map);
		this.render(r, map);
	}
	
	@Override
	public TubeLight clone() {
		return new TubeLight(this);
	}
}

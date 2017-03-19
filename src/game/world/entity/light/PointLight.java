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

public class PointLight extends Entity {
	/** The intensity multiplier below at which the intensity is assumed to be 0. */
	protected static final float CUTOFF_INTENSITY = 0.01f;
	
	public final Vector4f color = new Vector4f();
	public float attenuationFactor;
	private boolean dynamic;
	
	private transient boolean losGenerated = false;
	protected transient FloatBuffer losBuf = null;
	
	public PointLight(PointLight l) {
		super(l);
		
		this.color.set(l.color);
		this.attenuationFactor = l.attenuationFactor;
		this.dynamic = l.dynamic;
	}
	
	/**
	 * Constructs a new point light
	 * @param position The position of the light
	 * @param color The color intensity of the light
	 * @param attenuationFactor The attenuation factor of the light
	 * @param dynamic true if line of sight information should be regenerated every frame, false otherwise.
	 */
	public PointLight(Vector2f position, Vector4f color, float attenuationFactor, boolean dynamic) {
		super(Team.PASSIVE_TEAM, position);
		
		this.color.set(color);
		this.attenuationFactor = attenuationFactor;
		this.dynamic = dynamic;
	}
	
	/**
	 * Generate line of sight
	 */
	protected void generateLoS(Map map) {
		losBuf = map.getLineOfSight(position, LightUtil.getDistance(CUTOFF_INTENSITY, attenuationFactor), losBuf);
	}
	
	@Override
	public void update(UpdateArgs ua) {}
	
	protected void generateLosIfNecessary(Map map) {
		if (!losGenerated) {
			// Generate LoS if we haven't generated it yet...
			generateLoS(map);
			losGenerated = true;
		} else if (dynamic) {
			// Or if we are dynamic.
			generateLoS(map);
		}
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		generateLosIfNecessary(map);
		r.drawPointLight(losBuf, color, attenuationFactor);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		render(r, map);
	}
	
	@Override
	public PointLight clone() {
		return new PointLight(this);
	}
}

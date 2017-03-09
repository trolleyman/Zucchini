package game.world.entity;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

public class PointLight extends Entity {
	/** The intensity multiplier below at which the intensity is assumed to be 0. */
	private static final float CUTOFF_INTENSITY = 0.01f;
	
	protected Vector4f color;
	protected float attenuationFactor;
	private boolean dynamic;
	
	private transient boolean losGenerated = false;
	protected transient FloatBuffer losBuf = null;
	
	public PointLight(PointLight l) {
		super(l);
		
		this.color = l.color;
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
		
		this.color = color;
		this.attenuationFactor = attenuationFactor;
		this.dynamic = dynamic;
	}
	
	/**
	 * Returns distance above which the light's intensity can be assumed to be 0.
	 * It is a function of the light's attenuation factor.
	 * <p>
	 * <pre>
	 *     a = 1 / (1 + k * d^2);
	 * </pre>
	 * <p>
	 * Where a = attenuation, k = attenuation factor, and d = distance.
	 * <p>
	 * When the we rearrange for dist we get this:
	 * <p>
	 * <pre>
	 *     a * (1 + k * d^2) = 1;
	 *     a + ak + ad^2 = 1;
	 *     ad^2 = 1 - a - ak;
	 *     d^2 = 1/a - 1 - k;
	 *     d = sqrt(1/a - 1 - k);
	 * </pre>
	 */
	protected float getCutoffDist() {
		return (float) Math.sqrt(1/CUTOFF_INTENSITY - 1 - attenuationFactor);
	}
	
	/**
	 * Generate line of sight
	 */
	protected void generateLoS(Map map) {
		losBuf = map.getLineOfSight(position, getCutoffDist(), losBuf);
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
		super.renderLight(r, map);
		this.render(r, map);
	}
	
	@Override
	public PointLight clone() {
		return new PointLight(this);
	}
	
	/**
	 * Gets the attenuation factor necessary to have an attenuation of {@code cutoff} at {@code radius} distance
	 * away from the centre of the point.
	 * <p>
	 * <pre>
	 *     a = 1 / (1 + k * d^2);
	 *     a * (1 + k * d^2) = 1;
	 *     a + ak + ad^2 = 1;
	 *     ak = 1 - a - ad^2;
	 *     k = (1 - a - ad^2) / a;
	 *     k = 1/a - 1 - d^2;
	 * </pre>
	 * <p>
	 * Where a = attenuation, k = attenuation factor, and d = distance.
	 */
	public static float getAttenuationFactor(float radius, float cutoff) {
		return 1.0f / cutoff - 1.0f - radius * radius;
	}
}

package game.world.entity.light;

import game.Util;
import game.render.IRenderer;
import game.world.entity.LightUtil;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Spotlight extends PointLight {
	private float coneAngleMin;
	private float coneAngleMax;
	
	/**
	 * Constructs a new point light
	 * @param position The position of the light
	 * @param color The color intensity of the light
	 * @param attenuationFactor The attenuation factor of the light
	 * @param dynamic true if line of sight information should be regenerated every frame, false otherwise.
	 * @param coneAngleMin The angle between the centre of the spotlight and the edge of the solid beam
	 * @param coneAngleMax The angle between the centre of the spotlight and the edge of the beam
	 */
	public Spotlight(Vector2f position, Vector4f color, float attenuationFactor, boolean dynamic, float coneAngleMin, float coneAngleMax) {
		super(position, color, attenuationFactor, dynamic);
		this.coneAngleMin = coneAngleMin;
		this.coneAngleMax = coneAngleMax;
	}
	
	@Override
	protected void generateLoS(Map map) {
		losBuf = map.getLineOfSight(position, LightUtil.getDistance(CUTOFF_INTENSITY, attenuationFactor), angle, coneAngleMax*2, losBuf);
		//losBuf = map.getLineOfSight(position, LightUtil.getDistance(CUTOFF_INTENSITY, attenuationFactor), losBuf);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		generateLosIfNecessary(map);
		
		Vector2f coneDirection = Util.pushTemporaryVector2f().set(Util.getDirX(angle), Util.getDirY(angle));
		r.drawSpotlight(losBuf, color, attenuationFactor, coneAngleMin, coneAngleMax, coneDirection);
		Util.popTemporaryVector2f();
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		render(r, map);
	}
}

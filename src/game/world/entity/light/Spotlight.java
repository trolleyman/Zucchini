package game.world.entity.light;

import game.Util;
import game.render.IRenderer;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Spotlight extends PointLight {
	public float coneAngleMin;
	public float coneAngleMax;
	
	public Spotlight(Spotlight l) {
		super(l);
		
		this.coneAngleMin = l.coneAngleMin;
		this.coneAngleMax = l.coneAngleMax;
	}
	
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
		losBuf = map.getLineOfSight(position, getCutoffDist(), angle, coneAngleMax*2, losBuf);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		generateLosIfNecessary(map);
		
		Vector2f coneDirection = Util.pushTemporaryVector2f().set(Util.getDirX(angle), Util.getDirY(angle));
		r.drawSpotlight(losBuf, color, attenuationFactor, coneAngleMin, coneAngleMax, coneDirection);
		Util.popTemporaryVector2f();
	}
	
	@Override
	public Spotlight clone() {
		return new Spotlight(this);
	}
}

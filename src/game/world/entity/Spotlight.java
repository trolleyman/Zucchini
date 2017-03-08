package game.world.entity;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Spotlight extends PointLight {
	private float coneAngle;
	
	public Spotlight(Spotlight l) {
		super(l);
		
		this.coneAngle = l.coneAngle;
	}
	
	/**
	 * Constructs a new point light
	 * @param position The position of the light
	 * @param color The color intensity of the light
	 * @param attenuationFactor The attenuation factor of the light
	 * @param dynamic true if line of sight information should be regenerated every frame, false otherwise.
	 * @param coneAngle The angle between the centre of the spotlight and the edge of the beam
	 */
	public Spotlight(Vector2f position, Vector4f color, float attenuationFactor, boolean dynamic, float coneAngle) {
		super(position, color, attenuationFactor, dynamic);
		this.coneAngle = coneAngle;
	}
	
	@Override
	protected void generateLoS(Map map) {
		losBuf = map.getLineOfSight(position, getCutoffDist(), angle, coneAngle*2, losBuf);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		generateLosIfNecessary(map);
		
		Vector2f coneDirection = Util.pushTemporaryVector2f().set(Util.getDirX(angle), Util.getDirY(angle));
		r.drawSpotlight(losBuf, color, attenuationFactor, coneAngle, coneDirection);
		Util.popTemporaryVector2f();
	}
	
	@Override
	public Spotlight clone() {
		return new Spotlight(this);
	}
}

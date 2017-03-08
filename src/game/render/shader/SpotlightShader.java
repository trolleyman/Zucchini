package game.render.shader;

import org.joml.Vector2f;

import java.util.Vector;

import static org.lwjgl.opengl.GL20.*;

/**
 * Used to render a point light with a color, position, attenuation factor, cone angle and cone direction.
 *
 * @author Callum
 */
public class SpotlightShader extends PointLightShader {
	private int coneAngleUniform;
	private float coneAngle = (float) Math.toRadians(45.0);
	
	private int coneDirectionUniform;
	private Vector2f coneDirection = new Vector2f(1.0f, 0.0f);
	
	public SpotlightShader() {
		super("spotlight");
		
		coneAngleUniform = getUniformLocation("coneAngle");
		coneDirectionUniform = getUniformLocation("coneDirection");
	}
	
	public void setConeAngle(float angle) {
		this.coneAngle = angle;
		
		if (Shader.getCurrentShader() == this)
			uploadConeAngle();
	}
	
	public void setConeDirection(Vector2f direction) {
		setConeDirection(direction.x, direction.y);
	}
	
	public void setConeDirection(float x, float y) {
		this.coneDirection.set(x, y).normalize();
		
		if (Shader.getCurrentShader() == this)
			uploadConeDirection();
	}
	
	private void uploadConeAngle() {
		glUniform1f(coneAngleUniform, coneAngle);
	}
	
	private void uploadConeDirection() {
		glUniform2f(coneDirectionUniform, coneDirection.x, coneDirection.y);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadConeAngle();
		uploadConeDirection();
	}
}

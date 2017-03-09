package game.render.shader;

import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.*;

/**
 * Used to render a point light with a color, position, attenuation factor, cone angle and cone direction.
 *
 * @author Callum
 */
public class SpotlightShader extends PointLightShader {
	private int coneAngleMinUniform;
	private float coneAngleMin = (float) Math.toRadians(45.0);
	
	private int coneAngleMaxUniform;
	private float coneAngleMax = (float) Math.toRadians(50.0);
	
	private int coneDirectionUniform;
	private Vector2f coneDirection = new Vector2f(1.0f, 0.0f);
	
	public SpotlightShader() {
		super("spotlight");
		
		coneAngleMinUniform = getUniformLocation("coneAngleMin");
		coneAngleMaxUniform = getUniformLocation("coneAngleMax");
		coneDirectionUniform = getUniformLocation("coneDirection");
	}
	
	public void setConeAngleMin(float angle) {
		this.coneAngleMin = angle;
		
		if (Shader.getCurrentShader() == this)
			uploadConeAngleMin();
	}
	
	public void setConeAngleMax(float angle) {
		this.coneAngleMax = angle;
		
		if (Shader.getCurrentShader() == this)
			uploadConeAngleMax();
	}
	
	public void setConeDirection(Vector2f direction) {
		setConeDirection(direction.x, direction.y);
	}
	
	public void setConeDirection(float x, float y) {
		this.coneDirection.set(x, y).normalize();
		
		if (Shader.getCurrentShader() == this)
			uploadConeDirection();
	}
	
	private void uploadConeAngleMin() {
		glUniform1f(coneAngleMinUniform, coneAngleMin);
	}
	
	private void uploadConeAngleMax() {
		glUniform1f(coneAngleMaxUniform, coneAngleMax);
	}
	
	private void uploadConeDirection() {
		glUniform2f(coneDirectionUniform, coneDirection.x, coneDirection.y);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadConeAngleMin();
		uploadConeAngleMax();
		uploadConeDirection();
	}
}

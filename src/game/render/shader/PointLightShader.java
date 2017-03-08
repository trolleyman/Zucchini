package game.render.shader;

import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.*;

/**
 * Used to render a point light with a color, position and attenuation factor.
 *
 * @author Callum
 */
public class PointLightShader extends SimpleShader {
	private int lightPositionUniform;
	private Vector2f lightPosition = new Vector2f();
	
	private int attenuationFactorUniform;
	private float attenuationFactor = 1.0f;
	
	/**
	 * Constructs the shader with a default name
	 */
	public PointLightShader() {
		this("pointlight");
	}
	
	/**
	 * Constructs the shader with a specified name
	 */
	public PointLightShader(String name) {
		super(name);
		
		lightPositionUniform = getUniformLocation("lightPosition");
		attenuationFactorUniform = getUniformLocation("attenuationFactor");
	}
	
	public void setLightPosition(Vector2f position) {
		this.setLightPosition(position.x, position.y);
	}
	
	public void setLightPosition(float x, float y) {
		this.lightPosition.set(x, y);
		
		if (Shader.getCurrentShader() == this)
			uploadLightPosition();
	}
	
	public void setAttenuationFactor(float attenuationFactor) {
		this.attenuationFactor = attenuationFactor;
		
		if (Shader.getCurrentShader() == this)
			uploadAttenuationFactor();
	}
	
	private void uploadLightPosition() {
		glUniform2f(lightPositionUniform, lightPosition.x, lightPosition.y);
	}
	
	private void uploadAttenuationFactor() {
		glUniform1f(attenuationFactorUniform, attenuationFactor);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadLightPosition();
		uploadAttenuationFactor();
	}
}

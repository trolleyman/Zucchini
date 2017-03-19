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
	
	private int attenuationFactorUniform;
	
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
	
	public void setLightPosition(float x, float y) {
		glUniform2f(lightPositionUniform, x, y);
	}
	
	public void setAttenuationFactor(float attenuationFactor) {
		glUniform1f(attenuationFactorUniform, attenuationFactor);
	}
}

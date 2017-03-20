package game.render.shader;

import game.exception.ShaderCompilationException;

import static org.lwjgl.opengl.GL20.*;

/**
 * Used to render a simple tube light
 *
 * @author Callum
 */
public class TubeLightShader extends SimpleShader {
	private int attenuationFactorUniform;
	
	/**
	 * Constructs the shader with a default name
	 */
	public TubeLightShader() throws ShaderCompilationException {
		super("tubelight");
		
		attenuationFactorUniform = getUniformLocation("attenuationFactor");
	}
	
	public void setAttenuationFactor(float attenuationFactor) {
		glUniform1f(attenuationFactorUniform, attenuationFactor);
	}
}

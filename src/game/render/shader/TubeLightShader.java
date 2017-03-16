package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

/**
 * Used to render a simple tube light
 *
 * @author Callum
 */
public class TubeLightShader extends SimpleShader {
	private int attenuationFactorUniform;
	private float attenuationFactor = 1.0f;
	
	/**
	 * Constructs the shader with a default name
	 */
	public TubeLightShader() {
		super("tubelight");
		
		attenuationFactorUniform = getUniformLocation("attenuationFactor");
	}
	
	public void setAttenuationFactor(float attenuationFactor) {
		this.attenuationFactor = attenuationFactor;
		
		if (Shader.getCurrentShader() == this)
			uploadAttenuationFactor();
	}
	
	private void uploadAttenuationFactor() {
		glUniform1f(attenuationFactorUniform, attenuationFactor);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadAttenuationFactor();
	}
}

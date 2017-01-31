package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import org.joml.Vector4f;

import game.ColorUtil;

/**
 * Represents an OpenGL shader that has a transformation and color uniform.
 * 
 * @author Callum
 */
public class SimpleShader extends TransformationShader {
	/** Color uniform location */
	private int colorUniform;
	
	/** The color uniform data */
	private Vector4f color = ColorUtil.WHITE;
	
	/**
	 * Constructs a simple shader with the default name
	 */
	public SimpleShader() {
		this("simple");
	}
	
	/**
	 * Constructs the simple shader with the specified name
	 * @param name The shader's name
	 */
	public SimpleShader(String name) {
		super(name);
		
		colorUniform = getUniformLocation("color");
	}
	
	/**
	 * Sets the color for the shader
	 * @param _color The color
	 */
	public void setColor(Vector4f _color) {
		color = _color;
		
		if (getCurrentShader() == this)
			uploadColor();
	}
	
	/**
	 * Uploads the color to the shader
	 */
	private void uploadColor() {
		glUniform4f(colorUniform, color.x, color.y, color.z, color.w);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadColor();
	}
}

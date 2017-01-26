package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;

public class SimpleShader extends TransformationShader {
	private int colorUniform;
	
	private Color color = Color.WHITE;
	
	public SimpleShader() {
		this("simple");
	}
	
	public SimpleShader(String name) {
		super(name);
		
		colorUniform = getUniformLocation("color");
	}
	
	public void setColor(Color _color) {
		color = _color;
		
		if (getCurrentShader() == this)
			uploadColor();
	}
	
	private void uploadColor() {
		glUniform4f(colorUniform,
			color.getRed() / 255.0f,
			color.getGreen() / 255.0f,
			color.getBlue() / 255.0f,
			color.getAlpha() / 255.0f);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadColor();
	}
}

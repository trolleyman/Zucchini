package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import org.joml.Vector4f;

import game.ColorUtil;

public class SimpleShader extends TransformationShader {
	private int colorUniform;
	
	private Vector4f color = ColorUtil.WHITE;
	
	public SimpleShader() {
		this("simple");
	}
	
	public SimpleShader(String name) {
		super(name);
		
		colorUniform = getUniformLocation("color");
	}
	
	public void setColor(Vector4f _color) {
		color = _color;
		
		if (getCurrentShader() == this)
			uploadColor();
	}
	
	private void uploadColor() {
		glUniform4f(colorUniform, color.x, color.y, color.z, color.w);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadColor();
	}
}

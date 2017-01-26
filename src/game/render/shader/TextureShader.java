package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import game.render.Texture;

public class TextureShader extends SimpleShader {
	private int texUniform;
	
	private int tex;
	
	public TextureShader() {
		super("texture");
		
		texUniform = getUniformLocation("tex");
	}
	
	public void bindTexture(Texture img) {
		tex = img.getTextureID();
		
		if (getCurrentShader() == this)
			uploadTexture();
	}
	
	private void uploadTexture() {
		glUniform1i(texUniform, tex);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadTexture();
	}
}

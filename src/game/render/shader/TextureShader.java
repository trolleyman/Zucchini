package game.render.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
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
		glUniform1i(texUniform, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, tex);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadTexture();
	}
}

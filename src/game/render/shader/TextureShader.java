package game.render.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

import game.render.Texture;

/**
 * Represents an OpenGL shader with a transformation, color and texture uniform
 * 
 * @author Callum
 */
public class TextureShader extends SimpleShader {
	/** Texture uniform location */
	private int texUniform;
	
	/** Current texture ID */
	private int tex;
	
	/**
	 * Constructs a texture shader with the default name
	 */
	public TextureShader() {
		super("texture");
		
		texUniform = getUniformLocation("tex");
	}
	
	/**
	 * Binds an OpenGL texture to the shader
	 * @param _tex The texture
	 */
	public void bindTexture(Texture _tex) {
		this.tex = _tex.getTextureID();
		
		if (getCurrentShader() == this)
			uploadTexture();
	}
	
	/**
	 * Uploads the texture to the shader
	 */
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

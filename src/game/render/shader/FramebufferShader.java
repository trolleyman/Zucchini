package game.render.shader;

import game.render.Texture;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class FramebufferShader extends Shader {
	/** Texture uniform location */
	private int texUniform;
	
	/** Current texture ID */
	private int tex;
	
	/** Transformation uniform location */
	private int transUniform;
	
	/** Temp buffer used to upload the transformation matrix to the shader */
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Constructs a texture shader with the default name
	 */
	public FramebufferShader() {
		super("framebuffer");
		
		texUniform = getUniformLocation("tex");
		transUniform = getUniformLocation("trans");
		
		buffer.clear();
		new Matrix4f().setOrtho(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f).get(buffer).rewind();
	}
	
	/**
	 * Binds an OpenGL texture to the shader
	 * @param texId The texture id
	 */
	public void bindTextureId(int texId) {
		this.tex = texId;
		
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
	
	/**
	 * Uploads the transformation matrix to the shader
	 */
	private void uploadMatrix() {
		buffer.rewind();
		glUniformMatrix4fv(transUniform, false, buffer);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadTexture();
		uploadMatrix();
	}
}

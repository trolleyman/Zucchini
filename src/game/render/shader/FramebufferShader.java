package game.render.shader;

import game.render.Framebuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class FramebufferShader extends PassShader {
	/** Texture uniform location */
	private int texUniform;
	
	/** Current texture ID */
	private int tex;
	
	/** Transformation uniform location */
	private int transUniform;
	
	/** Temp buffer used to upload the transformation matrix to the shader */
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Constructs a framebuffer shader with the specified name
	 */
	public FramebufferShader(String name) {
		super(name);
		
		texUniform = getUniformLocation("tex");
		transUniform = getUniformLocation("trans");
		
		buffer.clear();
		new Matrix4f().setOrtho(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f).get(buffer).rewind();
	}
	
	/**
	 * Binds a framebuffer as input to the shader
	 * @param fb The framebuffer
	 */
	public void setFramebuffer(Framebuffer fb) {
		this.tex = fb.getColorTexId();
		
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

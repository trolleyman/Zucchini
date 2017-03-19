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

public class PassthroughShader extends PassShader {
	/** Texture uniform location */
	private int texUniform;
	
	/**
	 * Constructs the passthrough shader
	 */
	public PassthroughShader() {
		super("pass_passthrough");
		
		texUniform = getUniformLocation("tex");
	}
	
	/**
	 * Binds a framebuffer as input to the shader
	 * @param fb The framebuffer
	 */
	public void setFramebuffer(Framebuffer fb) {
		int tex = fb.getColorTexId();
		glUniform1i(texUniform, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, tex);
	}
}

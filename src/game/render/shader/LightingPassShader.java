package game.render.shader;

import game.render.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

public class LightingPassShader extends PassShader {
	/** World uniform location */
	private int worldUniform;
	
	/** Lighting uniform location */
	private int lightUniform;
	
	/**
	 * Constructs the lighting shader
	 */
	public LightingPassShader() {
		super("pass_lighting");
		
		worldUniform = getUniformLocation("world");
		lightUniform = getUniformLocation("light");
	}
	
	/**
	 * Binds the world framebuffer as input to the shader
	 * @param fb The world framebuffer
	 */
	public void setWorldFramebuffer(Framebuffer fb) {
		int worldTex = fb.getColorTexId();
		glUniform1i(worldUniform, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, worldTex);
	}
	
	/**
	 * Binds the light framebuffer as input to the shader
	 * @param fb The light framebuffer
	 */
	public void setLightFramebuffer(Framebuffer fb) {
		int lightTex = fb.getColorTexId();
		glUniform1i(lightUniform, 1);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, lightTex);
	}
}

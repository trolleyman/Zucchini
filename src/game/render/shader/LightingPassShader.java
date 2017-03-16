package game.render.shader;

import game.render.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

public class LightingPassShader extends PassShader {
	/** World uniform location */
	private int worldUniform;
	
	/** World texture ID */
	private int worldTex;
	
	/** Lighting uniform location */
	private int lightUniform;
	
	/** Lighting texture ID */
	private int lightTex;
	
	/**
	 * Constructs the lighting shader
	 */
	public LightingPassShader() {
		super("lightingPass");
		
		worldUniform = getUniformLocation("world");
		lightUniform = getUniformLocation("light");
	}
	
	/**
	 * Binds the world framebuffer as input to the shader
	 * @param fb The world framebuffer
	 */
	public void setWorldFramebuffer(Framebuffer fb) {
		this.worldTex = fb.getColorTexId();
		
		if (getCurrentShader() == this)
			uploadWorldTexture();
	}
	
	/**
	 * Binds the light framebuffer as input to the shader
	 * @param fb The light framebuffer
	 */
	public void setLightFramebuffer(Framebuffer fb) {
		this.lightTex = fb.getColorTexId();
		
		if (getCurrentShader() == this)
			uploadLightTexture();
	}
	
	/**
	 * Uploads the world texture to the shader
	 */
	private void uploadWorldTexture() {
		glUniform1i(worldUniform, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, worldTex);
	}
	
	/**
	 * Uploads the light texture to the shader
	 */
	private void uploadLightTexture() {
		glUniform1i(lightUniform, 1);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, lightTex);
	}
	
	@Override
	public void use() {
		super.use();
		
		uploadWorldTexture();
		uploadLightTexture();
	}
}

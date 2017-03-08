package game.render;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.*;

/**
 * Represents an OpenGL Framebuffer object
 */
public class Framebuffer {
	
	/** Framebuffer ID */
	private int fbId;
	
	/** Color Texture ID */
	private int colorTexId;
	
	/** Depth & stencil Renderbuffer ID */
	private int depthStencilTexId;
	
	public Framebuffer(int width, int height) {
		colorTexId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, colorTexId);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		
		this.resizeColorTex(width, height);
		
		glBindTexture(GL_TEXTURE_2D, 0);

		// Build the depth & stencil attachment
		depthStencilTexId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthStencilTexId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		this.resizeDepthStencilTex(width, height);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		// Build the framebuffer
		fbId = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbId);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexId, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthStencilTexId, 0);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
			// Error
			System.err.println("Error Initializing Framebuffer.");
			
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		Framebuffer.bindDefault();
	}
	
	private void resizeColorTex(int width, int height) {
		glBindTexture(GL_TEXTURE_2D, colorTexId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
	}
	
	private void resizeDepthStencilTex(int width, int height) {
		glBindTexture(GL_TEXTURE_2D, depthStencilTexId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
	}
	
	/**
	 * Resizes this framebuffer
	 */
	public void resize(int width, int height) {
		this.bind();
		
		this.resizeColorTex(width, height);
		this.resizeDepthStencilTex(width, height);
		
		Framebuffer.bindDefault();
	}
	
	/**
	 * Gets the texture ID associated with the color buffer
	 */
	public int getColorTexId() {
		return colorTexId;
	}
	
	/**
	 * Binds this framebuffer
	 */
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbId);
	}
	
	/**
	 * Destroys this framebuffer, freeing the resources associated with it
	 */
	public void destroy() {
		glDeleteTextures(colorTexId);
		glDeleteTextures(depthStencilTexId);
		glDeleteFramebuffers(fbId);
	}
	
	/**
	 * Sets the default OpenGL framebuffer
	 */
	public static void bindDefault() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}

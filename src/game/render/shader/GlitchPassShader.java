package game.render.shader;

import game.Util;
import game.exception.ShaderCompilationException;
import game.render.Framebuffer;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Responsible for the glitch effect when the laser is fired
 */
public class GlitchPassShader extends PassShader {
	/** Input uniform location */
	private int inputUniform;
	
	/** Effect uniform location */
	private int effectUniform;
	/** Red direction uniform location */
	private int redDirUniform;
	/** Green direction uniform location */
	private int greenDirUniform;
	/** Blue direction uniform location */
	private int blueDirUniform;
	
	public GlitchPassShader() throws ShaderCompilationException {
		super("pass_glitch");
		
		inputUniform = getUniformLocation("frame");
		effectUniform = getUniformLocation("effect");
		
		redDirUniform = getUniformLocation("rdir");
		greenDirUniform = getUniformLocation("gdir");
		blueDirUniform = getUniformLocation("bdir");
	}
	
	/**
	 * Sets the input framebuffer for this shader
	 */
	public void setInputFramebuffer(Framebuffer fb) {
		glUniform1i(inputUniform, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, fb.getColorTexId());
	}
	
	/**
	 * Sets the effect framebuffer for this shader
	 */
	public void setEffectFramebuffer(Framebuffer fb) {
		glUniform1i(effectUniform, 1);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, fb.getColorTexId());
	}
	
	/**
	 * Set red direction (in pixel coords)
	 * @param frameW Width of the framebuffer in pixels
	 * @param frameH Height of the framebuffer in pixels
	 */
	public void setRedDir(Vector2f dir, float frameW, float frameH) {
		uploadDirection(redDirUniform, dir, frameW, frameH);
	}
	
	/**
	 * Set green direction (in pixel coords)
	 * @param frameW Width of the framebuffer in pixels
	 * @param frameH Height of the framebuffer in pixels
	 */
	public void setGreenDir(Vector2f dir, float frameW, float frameH) {
		uploadDirection(greenDirUniform, dir, frameW, frameH);
	}
	
	/**
	 * Set blue direction (in pixel coords)
	 * @param frameW Width of the framebuffer in pixels
	 * @param frameH Height of the framebuffer in pixels
	 */
	public void setBlueDir(Vector2f dir, float frameW, float frameH) {
		uploadDirection(blueDirUniform, dir, frameW, frameH);
	}
	
	/**
	 * Upload a direction (in pixel coords) to the shader by converting it to texture coords before doing so
	 */
	private static void uploadDirection(int uniform, Vector2f dir, float frameW, float frameH) {
		// Transform into texture coords
		float pxW = 1.0f / frameW;
		float pxH = 1.0f / frameH;
		Vector2f temp = Util.pushTemporaryVector2f().set(dir);
		temp.mul(pxW, pxH);
		glUniform2f(uniform, -temp.x, -temp.y);
		Util.popTemporaryVector2f();
	}
	
}

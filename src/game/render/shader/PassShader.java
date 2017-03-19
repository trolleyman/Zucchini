package game.render.shader;

import game.exception.ShaderCompilationException;
import game.render.AccessFrequency;
import game.render.VAO;
import game.render.VBO;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * A shader that takes a framebuffer (or two) and draws it to the current framebuffer
 */
public abstract class PassShader extends Shader {
	/** The box that is used to draw the scene to the screen */
	private VAO boxUV;
	
	/** Transformation uniform location */
	private int transUniform;
	
	/** The identity matrix */
	private final Matrix4f identityMatrix = new Matrix4f();
	
	/** Temp buffer used to upload the transformation matrix to the shader */
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	
	public PassShader(String name) throws ShaderCompilationException {
		super(name);
		
		transUniform = getUniformLocation("trans");
		
		float[] vertexPositions = {
				// .: CCW :.
				// t0
				-1.0f, -1.0f, // BL
				 1.0f, -1.0f, // BR
				-1.0f,  1.0f, // TL
				// t1
				-1.0f,  1.0f, // TL
				 1.0f, -1.0f, // BR
				 1.0f,  1.0f, // TR
				// .: CW :.
				// t0
				-1.0f, -1.0f, // BL
				-1.0f,  1.0f, // TL
				 1.0f, -1.0f, // BR
				// t1
				-1.0f,  1.0f, // TL
				 1.0f,  1.0f, // TR
				 1.0f, -1.0f, // BR
		};
		float[] vertexUVs = {
				// .: CCW :.
				// t0
				0.0f, 0.0f, // BL
				1.0f, 0.0f, // BR
				0.0f, 1.0f, // TL
				// t1
				0.0f, 1.0f, // TL
				1.0f, 0.0f, // BR
				1.0f, 1.0f, // TR
				// .: CW :.
				// t0
				0.0f, 0.0f, // BL
				0.0f, 1.0f, // TL
				1.0f, 0.0f, // BR
				// t1
				0.0f, 1.0f, // TL
				1.0f, 1.0f, // TR
				1.0f, 0.0f, // BR
		};
		
		VBO positions = new VBO(vertexPositions, AccessFrequency.STATIC);
		VBO uvs = new VBO(vertexUVs, AccessFrequency.STATIC);
		
		boxUV = new VAO();
		boxUV.addData(this, "position", positions, 2, 0, 0);
		boxUV.addData(this, "uv", uvs, 2, 0, 0);
	}
	
	/**
	 * Processes the current inputs and draws the result to the current framebuffer
	 */
	public void draw() {
		this.draw(identityMatrix);
	}
	
	/**
	 * Processes the current inputs and draws the result to the current framebuffer
	 * @param matrix The transformation matrix
	 */
	public void draw(Matrix4f matrix) {
		matrix.get(buffer).rewind();
		glUniformMatrix4fv(transUniform, false, buffer);
		boxUV.draw(GL_TRIANGLES, 6);
	}
}

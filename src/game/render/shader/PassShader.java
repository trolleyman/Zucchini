package game.render.shader;

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
	
	/** Temp buffer used to upload the transformation matrix to the shader */
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	
	public PassShader(String name) {
		super(name);
		
		transUniform = getUniformLocation("trans");
		
		buffer.clear();
		new Matrix4f().setOrtho(0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f).get(buffer).rewind();
		
		float[] vertexPositions = {
				// t0
				0.0f, 0.0f, // BL
				0.0f, 1.0f, // TL
				1.0f, 0.0f, // BR
				// t1
				0.0f, 1.0f, // TL
				1.0f, 1.0f, // TR
				1.0f, 0.0f, // BR
		};
		float[] vertexUVs = {
				// t0
				0.0f, 1.0f, // BL
				0.0f, 0.0f, // TL
				1.0f, 1.0f, // BR
				// t1
				0.0f, 0.0f, // TL
				1.0f, 0.0f, // TR
				1.0f, 1.0f, // BR
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
		boxUV.bind();
		boxUV.draw(GL_TRIANGLES, 6);
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
		uploadMatrix();
	}
}

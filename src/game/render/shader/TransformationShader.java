package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

/**
 * Represents an OpenGL shader that has a transformation and color uniform.
 * 
 * @author Callum
 */
public class TransformationShader extends Shader {
	/** mvp uniform location */
	private int mvpUniform;
	
	/** Temp buffer used to upload the mvp matrix to the shader */
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	
	/** The ModelViewProjection matrix. mvp = Model * View * Projection */
	private Matrix4f matMVP;
	
	/** The projection matrix */
	private Matrix4f matProjection;
	/** The modelview matrix */
	private Matrix4f matModelView;
	
	/**
	 * Constructs a transformation shader with the specified name
	 * @param name The shader name
	 */
	public TransformationShader(String name) {
		super(name);
		
		this.mvpUniform = getUniformLocation("mvp");
		
		this.matMVP = new Matrix4f();
		
		this.matProjection = new Matrix4f();
		this.matModelView = new Matrix4f();
	}
	
	/**
	 * Sets the current projection matrix
	 * @param _matProjection The projection matrix
	 */
	public void setProjectionMatrix(Matrix4f _matProjection) {
		this.matProjection = _matProjection;
		if (getCurrentShader() == this)
			regenMVP();
	}
	
	/**
	 * Sets the current modelview matrix
	 * @param _matModelView The modelview matrix
	 */
	public void setModelViewMatrix(Matrix4f _matModelView) {
		this.matModelView = _matModelView;
		if (getCurrentShader() == this)
			regenMVP();
	}
	
	/**
	 * Regenerates the mvp matrix and uploads it to the shader
	 */
	private void regenMVP() {
		// Gen MVP
		matMVP.set(matProjection).mul(matModelView);
		
		// Upload MVP
		buffer.clear();
		matMVP.get(buffer).rewind();
		glUniformMatrix4fv(mvpUniform, false, buffer);
	}
	
	@Override
	public void use() {
		super.use();
		
		regenMVP();
	}
}

package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class TransformationShader extends Shader {
	private int mvpUniform;
	
	private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	
	// MVP == Model * View * Projection
	private Matrix4f matMVP;
	
	private Matrix4f matProjection;
	private Matrix4f matModelView;
	
	public TransformationShader(String name) {
		super(name);
		
		mvpUniform = getUniformLocation("mvp");
		
		matMVP = new Matrix4f();
		
		matProjection = new Matrix4f();
		matModelView = new Matrix4f();
	}
	
	public void setProjectionMatrix(Matrix4f _matProjection) {
		matProjection = _matProjection;
		if (getCurrentShader() == this)
			regenMVP();
	}
	
	public void setModelViewMatrix(Matrix4f _matModelView) {
		matModelView = _matModelView;
		if (getCurrentShader() == this)
			regenMVP();
	}
	
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

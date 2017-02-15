package game.render;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

/**
 * Class for interfacing with OpenGL Vertex Buffer Objects
 * 
 * @author Callum
 */
public class VBO {
	/** OpenGL VBO */
	private int vbo;
	/** Number of references to this VBO. If this hits 0, frees the OpenGL object. */
	private int references;
	/** Access Frequency */
	AccessFrequency freq;
	
	public VBO(float[] data, AccessFrequency _freq) {
		this.freq = _freq;
		
		this.vbo = glGenBuffers();
		this.setData(data);
	}
	
	public void setData(float[] data) {
		this.bind();
		switch (freq) {
		case STATIC : glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW); break;
		case DYNAMIC: glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW); break;
		}
	}

	public void setData(FloatBuffer data) {
		this.bind();
		switch (freq) {
			case STATIC : glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW); break;
			case DYNAMIC: glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW); break;
		}
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
	}

	public void incrementReference() {
		this.references++;
	}

	public void decrementReference() {
		this.references--;
		if (this.references == 0) {
			glDeleteBuffers(this.vbo);
		}
	}
}

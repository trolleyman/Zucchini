package game.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;

import game.render.shader.Shader;

/**
 * Class for interfacing with OpenGL Vertex Array Objects
 * 
 * @author Callum Tolley
 */
public class VAO {
	private int vao;
	private int primitiveType;
	private int count;
	
	private ArrayList<Integer> vbos;
	
	/**
	 * Constructs a VAO
	 * @param _primitiveType The primitive type that OpenGL will use to render, for example GL_QUADS or GL_TRIANGLES
	 * @param _count The number of vertices in the VAO
	 */
	public VAO(int _primitiveType, int _count) {
		vao = glGenVertexArrays();
		primitiveType = _primitiveType;
		count = _count;
		
		vbos = new ArrayList<>();
	}
	
	public void bind() {
		glBindVertexArray(vao);
	}
	
	/**
	 * Add data to the VAO.
	 * @param shader The shader that this VAO corresponds to
	 * @param attribName The name of the attribute to bind the data to
	 * @param data The vertex data
	 * @param components The number of data points for each vertex. For example, 2 for a vec2.
	 */
	public void addData(Shader shader, String attribName, float[] data, int components) {
		shader.use();
		this.bind();
				
		// Generate the VBO
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		vbos.add(vbo);
		
		int attribLoc = shader.getAttribLocation(attribName);
		glVertexAttribPointer(attribLoc, components, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(attribLoc);
	}
	
	public void destroy() {
		for (int i = 0; i < vbos.size(); i++)
			glDeleteBuffers(vbos.get(i));
		vbos.clear();
		
		glDeleteVertexArrays(vao);
	}

	public void draw() {
		this.bind();
		glDrawArrays(primitiveType, 0, count);
	}
}

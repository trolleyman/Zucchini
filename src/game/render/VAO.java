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
	
	/**
	 * VBOs referenced by this VAO.
	 */
	private ArrayList<VBO> vbos;
	
	/**
	 * Constructs a VAO
	 * @param _primitiveType The primitive type that OpenGL will use to render, for example GL_QUADS or GL_TRIANGLES
	 * @param _count The number of vertices in the VAO
	 */
	public VAO() {
		vao = glGenVertexArrays();
		
		vbos = new ArrayList<>();
	}
	
	/**
	 * Binds the VAO as the current VAO.
	 */
	public void bind() {
		glBindVertexArray(vao);
	}
	
	/**
	 * Add data to the VAO.
	 * @param shader The shader that this VAO corresponds to
	 * @param attribName The name of the attribute to bind the data to
	 * @param components The number of data points for each vertex. For example, 2 for a vec2.
	 * @param offset The offset of the first data point in the array
	 * @param stride The byte offset between consecutive generic vertex attributes.
	 *               If stride is 0, the generic vertex attributes are understood to be tightly packed in the array.
	 */
	public void addData(Shader shader, String attribName, VBO vbo, int components, int offset, int stride) {
		shader.use();
		this.bind();
		
		// Use the VBO
		vbo.incrementReference();
		vbo.bind();
		vbos.add(vbo);
		
		int attribLoc = shader.getAttribLocation(attribName);
		glVertexAttribPointer(attribLoc, components, GL_FLOAT, false, stride, offset);
		glEnableVertexAttribArray(attribLoc);
	}
	
	/**
	 * Frees resources associated with this VAO
	 */
	public void destroy() {
		for (int i = 0; i < vbos.size(); i++)
			vbos.get(i).decrementReference();
		vbos.clear();
		
		glDeleteVertexArrays(vao);
	}
	
	/**
	 * Draws this VAO to the screen.
	 * <p>
	 * <b>NB:</b> Does not bind the shader, so remember to!
	 * @param primitiveType The primitive type that OpenGL will use to render, for example GL_POLYGON or GL_TRIANGLES
	 * @param count The number of vertices
	 */
	public void draw(int primitiveType, int count) {
		this.bind();
		glDrawArrays(primitiveType, 0, count);
	}
}

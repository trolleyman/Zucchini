package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents the type of a shader.
 * See <a target="_top" href="https://www.khronos.org/opengl/wiki/Shader">here</a>.
 * 
 * @author Callum
 */
public enum ShaderType {
	VERTEX,
	FRAGMENT;
	
	public int glType() {
		switch (this) {
		case VERTEX  : return GL_VERTEX_SHADER;
		case FRAGMENT: return GL_FRAGMENT_SHADER;
		}
		return VERTEX.glType();
	}
	
	public String getExtension() {
		switch (this) {
		case VERTEX  : return ".vert";
		case FRAGMENT: return ".frag";
		}
		return VERTEX.getExtension();
	}
	
	@Override
	public String toString() {
		switch (this) {
		case VERTEX  : return "Vertex";
		case FRAGMENT: return "Fragment";
		}
		return VERTEX.toString();
	}
}
package game.render.shader;

import static org.lwjgl.opengl.GL20.*;

public enum ShaderType {
	VERTEX,
	FRAGMENT;
	
	public int glType() {
		switch (this) {
		case VERTEX  : return GL_VERTEX_SHADER;
		case FRAGMENT: return GL_FRAGMENT_SHADER;
		default      : return VERTEX.glType();
		}
	}
	
	public String getExtension() {
		switch (this) {
		case VERTEX  : return ".vert";
		case FRAGMENT: return ".frag";
		default      : return VERTEX.getExtension();
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		case VERTEX  : return "Vertex";
		case FRAGMENT: return "Fragment";
		default      : return VERTEX.toString();
		}
	}
}
package game;

import game.render.shader.ShaderType;

@SuppressWarnings("serial")
public class ShaderException extends Exception {
	public ShaderException(ShaderType type, String infoLog) {
		super(type.toString() + " shader compilation error:\n" + infoLog);
	}
}

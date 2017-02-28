package game.exception;

import game.render.shader.ShaderType;

public class ShaderCompilationException extends GameException {
	public ShaderCompilationException(ShaderType type, String infoLog) {
		super(type.toString() + " shader compilation error:\n" + infoLog);
	}
}

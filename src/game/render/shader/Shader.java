package game.render.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import game.Util;

public class Shader {
	private static int shadersLoaded = 0;
	public static int getShadersLoaded() {
		return shadersLoaded;
	}
	
	private static Shader currentShader = null;
	protected static Shader getCurrentShader() {
		return currentShader;
	}
	
	public static void useNullProgram() {
		glUseProgram(0);
		currentShader = null;
	}
	
	protected String shaderName;
	
	protected int vertShader;
	protected int fragShader;
	
	protected int program;
	
	public Shader(String _shaderName) {
		this.shaderName = _shaderName;
		try {
			String shaderBase = Util.getBasePath().toString() + "/shader/" + shaderName;
			
			program = glCreateProgram();
			vertShader = compileAndAttach(shaderBase, ShaderType.VERTEX, program);
			fragShader = compileAndAttach(shaderBase, ShaderType.FRAGMENT, program);
			
			glLinkProgram(program);
			if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
				throw new RuntimeException("Program linkage error:\n" + glGetProgramInfoLog(program));
			}
		} catch (Exception e) {
			System.err.println("Error: Could not load shader: " + shaderName + ": " + e.toString());
			System.exit(1);
		}
		
		shadersLoaded++;
		System.out.println("Loaded shader: " + shaderName);
	}
	
	private int compileAndAttach(String shaderBase, ShaderType type, int program) throws Exception {
		String vertSource = new String(Files.readAllBytes(Paths.get(shaderBase + type.getExtension())));
		
		int shader = glCreateShader(type.glType());
		
		glShaderSource(shader, new String(vertSource));
		
		glCompileShader(shader);
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			// Error
			throw new RuntimeException(type.toString() + " shader compilation error:\n" + glGetShaderInfoLog(shader));
		}
		
		glAttachShader(program, shader);
		
		return shader;
	}
	
	public int getUniformLocation(String name) {
		int loc = glGetUniformLocation(program, name);
		if (loc == -1)
			System.err.println("Warning: Nonexistant uniform requested: " + shaderName + ": " + name);
		return loc;
	}
	
	public int getAttribLocation(String name) {
		int loc = glGetAttribLocation(program, name);
		if (loc == -1)
			System.err.println("Warning: Nonexistant attribute requested: " + shaderName + ": " + name);
		return loc;
	}
	
	public void use() {
		if (currentShader == this) // Shader already used
			return;
		
		glUseProgram(program);
		currentShader = this;
	}
	
	public void destroy() {
		glDeleteProgram(program);
		glDeleteShader(vertShader);
		glDeleteShader(fragShader);
	}
}

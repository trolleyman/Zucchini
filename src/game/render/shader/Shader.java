package game.render.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import game.exception.ShaderCompilationException;
import game.Util;

/**
 * The base class for all OpenGL shaders.
 * <p>
 * See <a target="_top" href="https://open.gl/drawing">here</a> for an introduction.
 * 
 * @author Callum
 */
public class Shader {
	/** The number of shaders loaded. Used for debug logging only. */
	private static int shadersLoaded = 0;
	/**
	 * Returns the number of shaders currently loaded.
	 */
	public static int getShadersLoaded() {
		int loaded = shadersLoaded;
		shadersLoaded = 0;
		return loaded;
	}
	
	/** The currently used shader. This is used for optimization. */
	private static Shader currentShader = null;
	/**
	 * Returns the currently used shader.
	 */
	protected static Shader getCurrentShader() {
		return currentShader;
	}
	
	/**
	 * Uses a null shader so that no shader is currently active.
	 */
	public static void useNullProgram() {
		glUseProgram(0);
		currentShader = null;
	}
	
	/** The name of the current shader */
	protected String shaderName;
	
	/** The OpenGL id of the vertex shader */
	protected int vertShader;
	/** The OpenGL id of the fragment shader */
	protected int fragShader;
	
	/** The OpenGL id of the shader program */
	protected int program;
	
	/**
	 * Constructs a new shader with the specified shader name.
	 * @param _shaderName The shader name
	 */
	public Shader(String _shaderName) {
		this.shaderName = _shaderName;
		try {
			// Get shader base, e.g. "./resources/shader/simple"
			String shaderBase = Util.getResourcesDir() + "/shader/" + shaderName;
			
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
	
	/**
	 * Compiles and attaches the specified shader to the program specified
	 * @param shaderBase The base path of the shader
	 * @param type The type of the shader. See {@link ShaderType}.
	 * @param program The OpenGL id of the program
	 * @return The OpenGL id of the shader compiled
	 * @throws ShaderCompilationException if the shader could not be compiled
	 * @throws IOException if the shader could not be loaded
	 */
	private int compileAndAttach(String shaderBase, ShaderType type, int program) throws ShaderCompilationException, IOException {
		String vertSource = new String(Files.readAllBytes(Paths.get(shaderBase + type.getExtension())));
		
		int shader = glCreateShader(type.glType());
		
		glShaderSource(shader, new String(vertSource));
		
		glCompileShader(shader);
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			// Error
			throw new ShaderCompilationException(type, glGetShaderInfoLog(shader));
		}
		
		glAttachShader(program, shader);
		
		return shader;
	}
	
	/**
	 * Returns the uniform location of the specified uniform.
	 * See {@link org.lwjgl.opengl.GL20#glGetUniformLocation(int, CharSequence) glGetUniformLocation(int, CharSequence)}
	 * @param name The uniform's name
	 * @return The uniform's location
	 */
	public int getUniformLocation(String name) {
		int loc = glGetUniformLocation(program, name);
		if (loc == -1)
			System.err.println("Warning: Nonexistant uniform requested: " + shaderName + ": " + name);
		return loc;
	}
	
	/**
	 * Returns the attribute location of the specified uniform.
	 * See {@link org.lwjgl.opengl.GL20#glGetAttribLocation(int, CharSequence) glGetAttribLocation(int, CharSequence)}
	 * @param name The attribute's name
	 * @return The attribute's location
	 */
	public int getAttribLocation(String name) {
		int loc = glGetAttribLocation(program, name);
		if (loc == -1)
			System.err.println("Warning: Nonexistant attribute requested: " + shaderName + ": " + name);
		return loc;
	}
	
	public boolean isCurrentShader() {
		return getCurrentShader() == this;
	}
	
	/**
	 * Uses the program of the shader. See {@link org.lwjgl.opengl.GL20#glUseProgram(int) glUseProgram(int)}
	 */
	public void use() {
		if (isCurrentShader())
			return;
		
		glUseProgram(program);
		currentShader = this;
	}
	
	/**
	 * Frees all resources associated with this shader object
	 */
	public void destroy() {
		glDeleteProgram(program);
		glDeleteShader(vertShader);
		glDeleteShader(fragShader);
	}
}

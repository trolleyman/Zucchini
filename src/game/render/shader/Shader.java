package game.render.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import game.Resources;
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
		// Get shader base, e.g. "./resources/shader/simple"
		HashMap<String, byte[]> shaderData = Resources.getShaders();
		for (Entry<String, byte[]> e : shaderData.entrySet()) {
			if (!e.getKey().equals(shaderName + ShaderType.VERTEX.getExtension())) {
				continue;
			}
			String name = e.getKey().substring(0, e.getKey().indexOf(ShaderType.VERTEX.getExtension()));
			byte[] vertBytes = shaderData.get(name + ShaderType.VERTEX.getExtension());
			byte[] fragBytes = shaderData.get(name + ShaderType.FRAGMENT.getExtension());
			if (vertBytes == null) {
				System.err.println("Warning: Vertex shader does not exist for shader '" + name + "'.");
				continue;
			}
			if (fragBytes == null) {
				System.err.println("Warning: Framgent shader does not exist for shader '" + name + "'.");
				continue;
			}
			
			program = glCreateProgram();
			try {
				vertShader = compileAndAttach(vertBytes, ShaderType.VERTEX, program);
				fragShader = compileAndAttach(fragBytes, ShaderType.FRAGMENT, program);
			} catch (ShaderCompilationException ex) {
				throw new RuntimeException(ex);
			}
			
			glLinkProgram(program);
			if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
				throw new RuntimeException("Program linkage error:\n" + glGetProgramInfoLog(program));
			}
			
			shadersLoaded++;
			System.out.println("Loaded shader: " + shaderName);
		}
	}
	
	/**
	 * Compiles and attaches the specified shader to the program specified
	 * @param sourceBytes The bytes of the shader source
	 * @param type The type of the shader. See {@link ShaderType}.
	 * @param program The OpenGL id of the program
	 * @return The OpenGL id of the shader compiled
	 * @throws ShaderCompilationException if the shader could not be compiled
	 **/
	private int compileAndAttach(byte[] sourceBytes, ShaderType type, int program) throws ShaderCompilationException {
		String source = new String(sourceBytes);
		
		int shader = glCreateShader(type.glType());
		
		glShaderSource(shader, source);
		
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

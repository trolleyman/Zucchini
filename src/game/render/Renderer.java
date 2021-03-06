package game.render;

import game.InputHandler;
import game.Resources;
import game.Util;
import game.exception.ShaderCompilationException;
import game.render.shader.*;
import org.joml.MatrixStackf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer implements IRenderer {
	private static final int CIRCLE_VERTICES = 128;
	
	/** Window handle. See <a target="_top" href="http://www.glfw.org/docs/latest/window_guide.html#window_object">here</a> */
	private long window;
	
	/** If the window is fullscreen */
	private boolean fullscreen;
	
	// Simple shaders
	private SimpleShader simpleShader;
	private TextureShader textureShader;
	
	// Lighting shaders
	private PointLightShader pointLightShader;
	private SpotlightShader spotlightShader;
	private TubeLightShader tubeLightShader;
	
	// Framebuffer shaders
	private PassthroughShader passthroughShader;
	private LightingPassShader lightingPassShader;
	private GlitchPassShader glitchPassShader;
	
	// Meshes
	/** Box VAO */
	private VAO box;
	/** Textured box VAO */
	private VAO boxUV;
	/** Dynamic textured box VAO */
	private VAO boxDynamic;
	/** Dynamic textured box VBO */
	private VBO boxDynamicVBO;
	/** Dynamic textured box UVs */
	private float[] boxDynamicData = new float[12];
	
	/** The positions of the polygon */
	private VBO polygonVBO;
	/** The actual VAO of the polygon. References polygonVBO */
	private VAO polygonVAO;
	
	/** Circle VAO. This is a circle at 0,0 of radius 1, with CIRCLE_VERTICES number of vertices */
	private VAO circle;
	
	/** Next free index to use for the next temporary framebuffer */
	private int framebufferIndex;
	/** Holds the temporary framebuffers used for rendering. Reused every frame, but reset every frame. */
	private ArrayList<Framebuffer> framebuffers;
	
	/** Current input handler */
	private InputHandler ih;
	
	/** Texture bank */
	private TextureBank tb;
	
	/** Font bank */
	private FontBank fb;
	
	/** Render settings */
	private RenderSettings settings;
	/** Clone of render settings used to give to the public, to ensure that the settings are not changed outside the
	 * {@link #setRenderSettings(RenderSettings)} method. */
	private RenderSettings publicSettings;
	
	/** Current window width (in pixels) */
	private int windowW;
	/** Current window height (in pixels) */
	private int windowH;
	/** Current window width (in screen co-ordinates) */
	private int windowScreenW;
	/** Current window height (in screen co-ordinates) */
	private int windowScreenH;
	
	/** Should the game recalculate the projection matrix on the next frame? */
	private boolean dirty;
	
	/** Should the game regen shaders next frame? */
	private boolean regenShaders;
	
	/** Projection matrix */
	private MatrixStackf matProjection = new MatrixStackf(1);
	/** ModelView matrix */
	private MatrixStackf matModelView = new MatrixStackf(16);
	
	/**
	 * Construct the renderer, setting up a new hidden window. To show, use {@link #show()}
	 * @param _ih The input handler to set
	 * @param _fullscreen Should the window be fullscreen?
	 */
	public Renderer(InputHandler _ih, boolean _fullscreen) {
		settings = new RenderSettings();
		publicSettings = new RenderSettings();
		publicSettings.set(settings);
		
		// Setup input handler
		this.ih = _ih;
		this.fullscreen = _fullscreen;
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		// OpenGL window hints
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		// Debug context
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
		
		long monitor;
		if (fullscreen) {
			monitor = glfwGetPrimaryMonitor();
			GLFWVidMode mode = glfwGetVideoMode(monitor);
			glfwWindowHint(GLFW_RED_BITS, mode.redBits());
			glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
			glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
			glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
			windowW = mode.width();
			windowH = mode.height();
		} else {
			monitor = NULL;
			windowW = 1400;
			windowH = 1000;
		}
		window = glfwCreateWindow(windowW, windowH, "Zucchini", monitor, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		// Center the window
		glfwSetWindowPos(
			window,
			(vidmode.width() - windowW) / 2,
			(vidmode.height() - windowH) / 2
		);
		
		// Setup input handlers
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_F3 && action == GLFW_PRESS) {
				this.regenShaders = true;
			}
			
			this.ih.handleKey(key, scancode, action, mods);
		});
		glfwSetCharCallback(window, (window, cp) -> {
			this.ih.handleChar((char) cp);
		});
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			// Modify ypos so that coords are relative to bottom left of window.
			double xposPixel = screenToPixelCoordinates(xpos);
			double yposPixel = screenToPixelCoordinates(this.windowScreenH - ypos);
			this.ih.handleCursorPos(xposPixel, yposPixel);
		});
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			this.ih.handleMouseButton(button, action, mods);
		});
		glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
			this.ih.handleScroll(xoffset, yoffset);
		});
		glfwSetWindowSizeCallback(window, (window, w, h) -> {
			this.windowScreenW = w;
			this.windowScreenH = h;
			this.dirty = true;
		});
		glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
			this.windowW = w;
			this.windowH = h;
			this.dirty = true;
			for (Framebuffer f : framebuffers) {
				f.resize(w, h);
			}
			this.ih.handleResize(w, h);
		});
		int[] wBuf = new int[1];
		int[] hBuf = new int[1];
		glfwGetFramebufferSize(window, wBuf, hBuf);
		windowW = wBuf[0];
		windowH = hBuf[0];
		glfwGetWindowSize(window, wBuf, hBuf);
		windowScreenW = wBuf[0];
		windowScreenH = hBuf[0];
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		
		GL.createCapabilities();
		
		// Print OpenGL version
		System.out.println("Loaded OpenGL " + glGetString(GL_VERSION) + " (" + glGetString(GL_VENDOR) + ") on " + glGetString(GL_RENDERER));
		
		// Print scale
		System.out.println("DPI Scale: " + screenToPixelCoordinates(1));
		
		// Load shaders
		try {
			loadShaders();
		} catch (ShaderCompilationException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		// Load framebuffers
		framebuffers = new ArrayList<>();
		
		// Load images
		tb = new TextureBank();
		
		// Intiialize FontBank
		fb = new FontBank();
		
		// Generate meshes
		generateMeshes();
		
		// Set window icon
		setWindowIcon();
		
		// Setup render settings
		this.setRenderSettings(settings);
		
		// Set OpenGL settings
		glEnable(GL_BLEND);
		glEnable(GL_CULL_FACE);
		glEnable(GL_STENCIL);
		glEnable(GL_STENCIL_TEST);
		this.disableStencil();
		
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		recalcProjectionMatrix();
	}
	
	private double screenToPixelCoordinates(double coord) {
		double scale = this.windowW / this.windowScreenW;
		return coord * scale;
	}
	
	/**
	 * Generate the VAO meshes.
	 */
	private void generateMeshes() {
		float[] vertexPositions = {
			// t0
			0.0f, 0.0f, // BL
			1.0f, 0.0f, // BR
			0.0f, 1.0f, // TL
			// t1
			0.0f, 1.0f, // TL
			1.0f, 0.0f, // BR
			1.0f, 1.0f, // TR
		};
		float[] vertexUVs = {
			// t0
			0.0f, 1.0f, // BL
			1.0f, 1.0f, // BR
			0.0f, 0.0f, // TL
			// t1
			0.0f, 0.0f, // TL
			1.0f, 1.0f, // BR
			1.0f, 0.0f, // TR
		};
		
		VBO positions = new VBO(vertexPositions, AccessFrequency.STATIC);
		VBO uvs = new VBO(vertexUVs, AccessFrequency.STATIC);
		
		box = new VAO();
		box.addData(simpleShader, "position", positions, 2, 0, 0);
		
		boxUV = new VAO();
		boxUV.addData(textureShader, "position", positions, 2, 0, 0);
		boxUV.addData(textureShader, "uv", uvs, 2, 0, 0);
		
		boxDynamicVBO = new VBO(vertexUVs, AccessFrequency.DYNAMIC);
		boxDynamic = new VAO();
		boxDynamic.addData(textureShader, "position", positions, 2, 0, 0);
		boxDynamic.addData(textureShader, "uv", boxDynamicVBO, 2, 0, 0);
		
		polygonVBO = new VBO(new float[] {}, AccessFrequency.DYNAMIC);
		polygonVAO = new VAO();
		polygonVAO.addData(simpleShader, "position", polygonVBO, 2, 0, 0);
		
		// Generate circle data
		int len = 2 * CIRCLE_VERTICES;
		float[] circleData = new float[len];
		circleData[0] = 0.0f;
		circleData[1] = 0.0f;
		for (int i = 2; i < len-2; i += 2) {
			double ang = -((double)i-2) / (len-4) * Math.PI * 2;
			float x = (float)Math.sin(ang);
			float y = (float)Math.cos(ang);
			circleData[i  ] = x;
			circleData[i+1] = y;
		}
		circleData[len-2] = 0.0f;
		circleData[len-1] = 1.0f;
		VBO circleVBO = new VBO(circleData, AccessFrequency.STATIC);
		circle = new VAO();
		circle.addData(simpleShader, "position", circleVBO, 2, 0, 0);
	}
	
	/**
	 * Sets the current window icon
	 */
	private void setWindowIcon() {
		HashMap<String, byte[]> files = Resources.getFiles("img", (s) -> s.equals("icon.png"));
		if (files.size() == 0) {
			System.err.println("Warning: Icon could not be located");
			return;
		}
		byte[] iconData = files.values().iterator().next();
		ByteBuffer bbData = MemoryUtil.memAlloc(iconData.length);
		bbData.put(iconData);
		bbData.flip();
		
		int[] wArr = new int[1];
		int[] hArr = new int[1];
		int[] compArr = new int[1];
		
		// Load PNG
		ByteBuffer imageData = stbi_load_from_memory(bbData, wArr, hArr, compArr, 4);
		imageData.rewind();
		
		bbData.rewind();
		MemoryUtil.memFree(bbData);
		
		// Generate a GLFW image from the raw data
		GLFWImage.Buffer imageBuf = GLFWImage.calloc(1);
		imageBuf.get(0).pixels(imageData);
		imageBuf.get(0).width(wArr[0]);
		imageBuf.get(0).height(hArr[0]);
		
		// Set window icon
		glfwSetWindowIcon(window, imageBuf);
		
		MemoryUtil.memFree(imageData);
	}
	
	/**
	 * Recalculate the projection matrix
	 */
	private void recalcProjectionMatrix() {
		this.dirty = false;
		glViewport(0, 0, windowW, windowH);
		matProjection.setOrtho(0.0f, windowW, 0.0f, windowH, -1.0f, 1.0f);
	}
	
	@Override
	public void setInputHandler(InputHandler _ih) {
		this.ih = _ih;
	}
	
	@Override
	public int getWidth() {
		return windowW;
	}
	
	@Override
	public int getHeight() {
		return windowH;
	}
	
	@Override
	public void show() {
		// Make the window visible
		glfwShowWindow(window);
		this.ih.handleResize(windowW, windowH);
	}
	
	/**
	 * An interface to allow for a ShaderCompilationException to be thrown
	 * @param <T> The shader type
	 */
	private interface IShaderSupplier<T extends Shader> {
		T newShader() throws ShaderCompilationException;
	}
	/**
	 * Helper function that returns prevShader if the supplier fails to give a shader (i.e. throws a ShaderCompilationException)
	 * @param prevShader The previous shader
	 * @param load The shader supplier
	 * @param <T> The type of shader
	 * @return The new/previous shader
	 */
	private <T extends Shader> T reloadShaderHelper(T prevShader, IShaderSupplier<T> load) {
		try {
			T newShader = load.newShader();
			if (prevShader != null)
				prevShader.destroy();
			return newShader;
		} catch (ShaderCompilationException e) {
			System.err.println("Error on loading shader: " + e);
			return prevShader;
		}
	}
	
	/**
	 * Reloads all of the current shaders
	 */
	private void reloadShaders() {
		System.out.println("Reloading shaders...");
		simpleShader = reloadShaderHelper(simpleShader, SimpleShader::new);
		textureShader = reloadShaderHelper(textureShader, TextureShader::new);
		
		pointLightShader = reloadShaderHelper(pointLightShader, PointLightShader::new);
		spotlightShader = reloadShaderHelper(spotlightShader, SpotlightShader::new);
		tubeLightShader = reloadShaderHelper(tubeLightShader, TubeLightShader::new);
		
		passthroughShader = reloadShaderHelper(passthroughShader, PassthroughShader::new);
		lightingPassShader = reloadShaderHelper(lightingPassShader, LightingPassShader::new);
		glitchPassShader = reloadShaderHelper(glitchPassShader, GlitchPassShader::new);
		System.out.println(Shader.getShadersLoaded() + " shader(s) reloaded.\n");
	}
	
	/**
	 * Loads al of the current shaders
	 * @throws ShaderCompilationException on error
	 */
	private void loadShaders() throws ShaderCompilationException {
		System.out.println("Loading shaders...");
		simpleShader = new SimpleShader();
		simpleShader = new SimpleShader();
		textureShader = new TextureShader();
		
		pointLightShader = new PointLightShader();
		spotlightShader = new SpotlightShader();
		tubeLightShader = new TubeLightShader();
		
		passthroughShader = new PassthroughShader();
		lightingPassShader = new LightingPassShader();
		glitchPassShader = new GlitchPassShader();
		System.out.println(Shader.getShadersLoaded() + " shader(s) loaded.\n");
	}
	
	@Override
	public void destroy() {
		// Free the images
		tb.destroy();
		
		// Free the boxes
		box.destroy();
		boxUV.destroy();
		boxDynamic.destroy();
		polygonVAO.destroy();
		circle.destroy();
		
		// Free the shaders
		destroyShaders();
		
		// Free the framebuffers
		for (Framebuffer f : framebuffers) {
			f.destroy();
		}
		framebuffers.clear();
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	/**
	 * Destroys all of the shaders
	 */
	private void destroyShaders() {
		simpleShader.destroy();
		textureShader.destroy();
		
		pointLightShader.destroy();
		spotlightShader.destroy();
		tubeLightShader.destroy();
		
		passthroughShader.destroy();
		lightingPassShader.destroy();
		glitchPassShader.destroy();
	}
	
	private void setVSync(boolean enable) {
		if (enable)
			glfwSwapInterval(1);
		else
			glfwSwapInterval(0);
	}
	
	@Override
	public RenderSettings getRenderSettings() {
		publicSettings.set(settings);
		return publicSettings;
	}
	
	@Override
	public void setRenderSettings(RenderSettings settings) {
		if (this.settings.vSync != settings.vSync)
			this.setVSync(settings.vSync); // Update vsync settings
		
		this.settings.set(settings);
	}
	
	@Override
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	@Override
	public void beginFrame() {
		if (regenShaders) {
			reloadShaders();
			regenShaders = false;
		}
		
		if (this.dirty)
			recalcProjectionMatrix();
		
		// Reset the starting temp framebuffer
		framebufferIndex = 0;
		
		// Bind the default framebuffer
		Framebuffer.bindDefault();
		
		// Clear the default framebuffer
		clearFrame();
		
		// Clear the modelview matrix stack
		matModelView.clear();
		
		// Use the default shader
		Shader.useNullProgram();
	}
	
	@Override
	public void clearFrame() {
		setDefaultBlend();
		glStencilMask(0xFF);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT); // clear the framebuffer
		
		this.disableStencilDraw();
		this.disableStencil();
	}
	
	@Override
	public void endFrame() {
		glfwSwapBuffers(window); // swap the buffered output
		// poll for events
		glfwPollEvents();
	}
	
	@Override
	public Framebuffer getFreeFramebuffer() {
		while (framebufferIndex >= framebuffers.size()) {
			// Add framebuffer to array until framebufferIndex is in the array
			framebuffers.add(new Framebuffer(windowW, windowH));
		}
		// Get the next free framebuffer
		Framebuffer f = framebuffers.get(framebufferIndex++);
		// Clear the framebuffer
		f.bind();
		clearFrame();
		return f;
	}
	
	@Override
	public TextureBank getTextureBank() {
		return tb;
	}
	
	@Override
	public FontBank getFontBank() {
		return fb;
	}
	
	public void align(Align a, float w, float h) {
		switch (a) {
		case BL:
			break;
		case BM: matModelView.translate(w/2, 0.0f, 0.0f);
			break;
		case BR: matModelView.translate(w, 0.0f, 0.0f);
			break;
		case ML: matModelView.translate(0.0f, h/2, 0.0f);
			break;
		case MM: matModelView.translate(w/2, h/2, 0.0f);
			break;
		case MR: matModelView.translate(w, h/2, 0.0f);
			break;
		case TL: matModelView.translate(0.0f, h, 0.0f);
			break;
		case TM: matModelView.translate(w/2, h, 0.0f);
			break;
		case TR: matModelView.translate(w, h, 0.0f);
			break;
		}
	}
	
	@Override
	public MatrixStackf getModelViewMatrix() {
		return matModelView;
	}
	
	@Override
	public void drawLine(float _x0, float _y0, float _x1, float _y1, Vector4f c, float thickness) {
		// Change to pixel co-ords
		Vector3f temp = Util.pushTemporaryVector3f();
		temp.set(_x0, _y0, 0.0f).mulPosition(matModelView);
		float x0 = temp.x;
		float y0 = temp.y;
		temp.set(_x1, _y1, 0.0f).mulPosition(matModelView);
		float x1 = temp.x;
		float y1 = temp.y;
		
		float xdiff = x1 - x0;
		float ydiff = y1 - y0;
		float ang = Util.getAngle(xdiff, ydiff);
		float length = (float) Math.sqrt(xdiff*xdiff + ydiff*ydiff); // Pythag
		
		// Reset the modelview matrix so that we are using pixel co-ordinates
		getModelViewMatrix().pushMatrix();
		getModelViewMatrix().identity();
		drawBox(Align.BM, x0, y0, thickness, length, c, ang);
		getModelViewMatrix().popMatrix();
		Util.popTemporaryVector3f();
	}
	
	@Override
	public void drawBox(Align a, float x, float y, float w, float h, Vector4f c, float r) {
		matModelView.pushMatrix();
		
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-r, 0.0f, 0.0f, 1.0f);
		align(a, -w, -h);
		matModelView.scale(w, h, 1.0f);
		
		simpleShader.use();
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		
		// Draw 1x1 box (without UV)
		box.draw(GL_TRIANGLES, 6);
		matModelView.popMatrix();
	}
	
	@Override
	public void drawTexture(Texture tex, Align a, float x, float y, float w, float h, float r, Vector4f c) {
		matModelView.pushMatrix();
		//matModelView.translate(x, y, 0.0f).translate(0.0f, h, 0.0f).scale(w, -h, 1.0f);
		
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-r, 0.0f, 0.0f, 1.0f);
		align(a, -w, -h);
		matModelView.scale(w, h, 1.0f);
		
		textureShader.use();
		textureShader.setProjectionMatrix(matProjection);
		textureShader.setModelViewMatrix(matModelView);
		textureShader.setColor(c);
		textureShader.bindTexture(tex);
		
		// Draw 1x1 box (with UV)
		boxUV.draw(GL_TRIANGLES, 6);
		matModelView.popMatrix();
	}
	
	@Override
	public void drawTextureUV(
			Texture tex, Align a, float x, float y, float w, float h, float r,
			float u0, float v0, float u1, float v1, Vector4f color) {
		
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-r, 0.0f, 0.0f, 1.0f);
		align(a, -w, -h);
		matModelView.scale(w, h, 1.0f);
		
		textureShader.use();
		textureShader.setProjectionMatrix(matProjection);
		textureShader.setModelViewMatrix(matModelView);
		textureShader.bindTexture(tex);
		textureShader.setColor(color);
		
		// Upload UV data
		// t0
		boxDynamicData[ 0] = u0; boxDynamicData[ 1] = v1; // BL
		boxDynamicData[ 2] = u1; boxDynamicData[ 3] = v1; // BR
		boxDynamicData[ 4] = u0; boxDynamicData[ 5] = v0; // TL
		// t1
		boxDynamicData[ 6] = u0; boxDynamicData[ 7] = v0; // TL
		boxDynamicData[ 8] = u1; boxDynamicData[ 9] = v1; // BR
		boxDynamicData[10] = u1; boxDynamicData[11] = v0; // TR
		boxDynamicVBO.setData(boxDynamicData);
		
		// Draw 1x1 box (with UV)
		boxDynamic.draw(GL_TRIANGLES, 6);
		
		matModelView.popMatrix();
	}
	
	@Override
	public void drawTriangleFan(float[] data, float x, float y, Vector4f c) {
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		
		simpleShader.use();
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		
		polygonVBO.setData(data);
		polygonVAO.draw(GL_TRIANGLE_FAN, data.length / 2);
		
		matModelView.popMatrix();
	}

	@Override
	public void drawTriangleFan(FloatBuffer data, float x, float y, Vector4f c) {
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		
		simpleShader.use();
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		
		polygonVBO.setData(data);
		polygonVAO.draw(GL_TRIANGLE_FAN, data.remaining() / 2);
		
		matModelView.popMatrix();
	}
	
	@Override
	public void drawCircle(float x, float y, float radius, Vector4f c) {
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		matModelView.scale(radius, radius, 1.0f);
		
		simpleShader.use();
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		
		// Draw circle
		circle.draw(GL_TRIANGLE_FAN, CIRCLE_VERTICES);
		matModelView.popMatrix();
	}
	
	@Override
	public void drawPointLight(FloatBuffer data, Vector4f c, float attenuationFactor) {
		matModelView.pushMatrix();
		
		pointLightShader.use();
		pointLightShader.setProjectionMatrix(matProjection);
		pointLightShader.setModelViewMatrix(matModelView);
		pointLightShader.setColor(c);
		pointLightShader.setLightPosition(data.get(0), data.get(1));
		pointLightShader.setAttenuationFactor(attenuationFactor);
		
		// Draw triangle fan
		polygonVBO.setData(data);
		polygonVAO.draw(GL_TRIANGLE_FAN, data.remaining() / 2);
		
		matModelView.popMatrix();
	}
	
	@Override
	public void drawSpotlight(FloatBuffer data, Vector4f c, float attenuationFactor, float coneAngleMin, float coneAngleMax, float coneDirectionX, float coneDirectionY) {
		matModelView.pushMatrix();
		
		spotlightShader.use();
		spotlightShader.setProjectionMatrix(matProjection);
		spotlightShader.setModelViewMatrix(matModelView);
		spotlightShader.setColor(c);
		spotlightShader.setLightPosition(data.get(0), data.get(1));
		spotlightShader.setAttenuationFactor(attenuationFactor);
		spotlightShader.setConeAngleMin(coneAngleMin);
		spotlightShader.setConeAngleMax(coneAngleMax);
		spotlightShader.setConeDirection(coneDirectionX, coneDirectionY);
		
		// Draw triangle fan
		polygonVBO.setData(data);
		polygonVAO.draw(GL_TRIANGLE_FAN, data.remaining() / 2);
		
		matModelView.popMatrix();
	}
	
	@Override
	public void drawTubeLight(float x, float y, float angle, float length, float width, Vector4f c, float attenuationFactor) {
		matModelView.pushMatrix();
		
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-angle, 0.0f, 0.0f, 1.0f);
		align(Align.BM, -width, -length);
		matModelView.scale(width, length, 1.0f);
		
		tubeLightShader.use();
		tubeLightShader.setProjectionMatrix(matProjection);
		tubeLightShader.setModelViewMatrix(matModelView);
		tubeLightShader.setColor(c);
		tubeLightShader.setAttenuationFactor(attenuationFactor);
		
		// Draw 1x1 box (without UV)
		box.draw(GL_TRIANGLES, 6);
		
		matModelView.popMatrix();
	}
	
	private double[] xBuf = new double[1];
	private double[] yBuf = new double[1];
	@Override
	public double getMouseX() {
		glfwGetCursorPos(window, xBuf, null);
		return screenToPixelCoordinates(xBuf[0]);
	}
	@Override
	public double getMouseY() {
		glfwGetCursorPos(window, null, yBuf);
		return screenToPixelCoordinates(this.windowScreenH - yBuf[0]);
	}
	
	@Override
	public void enableStencilDraw(int i) {
		glColorMask(false, false, false, false);
		glDepthMask(false);
		glStencilMask(0xFF);
		
		glStencilFunc(GL_ALWAYS, i, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
	}
	
	@Override
	public void disableStencilDraw() {
		glColorMask(true, true, true, true);
		glDepthMask(true);
		glStencilMask(0x00);
		
		glStencilFunc(GL_ALWAYS, 0, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
	}
	
	@Override
	public void enableStencil(int i) {
		glStencilFunc(GL_EQUAL, i, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
	}
	
	@Override
	public void disableStencil() {
		glStencilFunc(GL_ALWAYS, 0, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
	}
	
	@Override
	public void drawText(Font f, String s, Align a, boolean fromBaseline, float x, float y, float scale, Vector4f color) {
		matModelView.pushMatrix();
		align(a, -f.getWidth(s, scale), -f.getHeight(scale));
		f.render(this, s, fromBaseline, x, y, scale, color);
		matModelView.popMatrix();
	}
	
	@Override
	public void drawWorldWithLighting(Framebuffer world, Framebuffer light) {
		lightingPassShader.use();
		lightingPassShader.setWorldFramebuffer(world);
		lightingPassShader.setLightFramebuffer(light);
		lightingPassShader.draw();
	}
	
	@Override
	public void drawFramebuffer(Framebuffer framebuffer) {
		this.drawFramebuffer(framebuffer, -1.0f, -1.0f, 1.0f, 1.0f);
	}
	
	@Override
	public void drawFramebuffer(Framebuffer framebuffer, float x, float y, float w, float h) {
		matModelView.pushMatrix()
				.identity()
				.translate(x, y, 0.0f)
				.scale(w, h, 1.0f);
		
		passthroughShader.use();
		passthroughShader.setFramebuffer(framebuffer);
		passthroughShader.draw(matModelView);
		
		matModelView.popMatrix();
	}
	
	@Override
	public void drawGlitchEffect(Framebuffer input, Framebuffer effect, Vector2f rDir, Vector2f gDir, Vector2f bDir) {
		glitchPassShader.use();
		glitchPassShader.setInputFramebuffer(input);
		glitchPassShader.setEffectFramebuffer(effect);
		glitchPassShader.setRedDir  (rDir, getWidth(), getHeight());
		glitchPassShader.setGreenDir(gDir, getWidth(), getHeight());
		glitchPassShader.setBlueDir (bDir, getWidth(), getHeight());
		glitchPassShader.draw();
	}
	
	@Override
	public void setDefaultBlend() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	@Override
	public void setLightingBlend() {
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ZERO, GL_ONE);
	}
}

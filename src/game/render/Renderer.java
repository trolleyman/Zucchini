package game.render;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;

import org.joml.MatrixStackf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import game.InputHandler;
import game.Util;
import game.render.shader.Shader;
import game.render.shader.SimpleShader;
import game.render.shader.TextureShader;

public class Renderer implements IRenderer {
	private static final int CIRCLE_VERTICES = 128;

	/** Window handle. See <a target="_top" href="http://www.glfw.org/docs/latest/window_guide.html#window_object">here</a> */
	private long window;
	
	/** If the window is fullscreen */
	private boolean fullscreen;
	
	// Shaders
	private SimpleShader simpleShader;
	private TextureShader textureShader;
	
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
	
	/** Current input handler */
	private InputHandler ih;
	
	/** Image bank */
	private TextureBank ib;
	
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
			windowW = 1000;
			windowH = 800;
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
		System.out.println("Loading shaders...");
		simpleShader = new SimpleShader();
		textureShader = new TextureShader();
		System.out.println(Shader.getShadersLoaded() + " shader(s) loaded.");
		
		// Load images
		ib = new TextureBank();
		
		// Generate meshes
		generateMeshes();
		
		// Enable v-sync
		this.setVSync(true);
		
		// Set OpenGL settings
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_CULL_FACE);
		
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
	public void setVSync(boolean enable) {
		if (enable)
			glfwSwapInterval(1);
		else
			glfwSwapInterval(0);
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
	
	@Override
	public void destroy() {
		// Free the images
		ib.destroy();
		
		// Free the boxes
		box.destroy();
		boxUV.destroy();
		
		// Free the shaders
		simpleShader.destroy();
		textureShader.destroy();
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	@Override
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	@Override
	public void beginFrame() {
		if (this.dirty)
			recalcProjectionMatrix();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		matModelView.clear();
		
		Shader.useNullProgram();
	}
	
	@Override
	public void endFrame() {
		glfwSwapBuffers(window); // swap the buffered output
		// poll for events
		glfwPollEvents();
	}
	
	@Override
	public TextureBank getImageBank() {
		return ib;
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
		Vector3f temp = Util.getThreadLocalVector3f();
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
	}
	
	@Override
	public void drawBox(Align a, float x, float y, float w, float h, Vector4f c, float r) {
		matModelView.pushMatrix();
		//matModelView.translate(x, y, 0.0f).scale(w, h, 1.0f);
		
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-r, 0.0f, 0.0f, 1.0f);
		align(a, -w, -h);
		matModelView.scale(w, h, 1.0f);
		
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		simpleShader.use();
		
		// Draw 1x1 box (without UV)
		box.draw(GL_TRIANGLES, 6);
		matModelView.popMatrix();
	}
	
	@Override
	public void drawTexture(Texture tex, Align a, float x, float y, float w, float h, float r) {
		matModelView.pushMatrix();
		//matModelView.translate(x, y, 0.0f).translate(0.0f, h, 0.0f).scale(w, -h, 1.0f);
		
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-r, 0.0f, 0.0f, 1.0f);
		align(a, -w, -h);
		matModelView.scale(w, h, 1.0f);
		
		textureShader.setProjectionMatrix(matProjection);
		textureShader.setModelViewMatrix(matModelView);
		textureShader.bindTexture(tex);
		textureShader.use();
		
		// Draw 1x1 box (with UV)
		boxUV.draw(GL_TRIANGLES, 6);
		matModelView.popMatrix();
	}
	
	@Override
	public void drawTextureUV(
			Texture tex, Align a, float x, float y, float w, float h, float r,
			float u0, float v0, float u1, float v1) {
		
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		matModelView.rotate(-r, 0.0f, 0.0f, 1.0f);
		align(a, -w, -h);
		matModelView.scale(w, h, 1.0f);
		
		textureShader.setProjectionMatrix(matProjection);
		textureShader.setModelViewMatrix(matModelView);
		textureShader.bindTexture(tex);
		textureShader.use();
		
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
		
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		simpleShader.use();
		
		polygonVBO.setData(data);
		polygonVAO.draw(GL_TRIANGLE_FAN, data.length / 2);
		
		matModelView.popMatrix();
	}

	@Override
	public void drawTriangleFan(FloatBuffer data, float x, float y, Vector4f c) {
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		simpleShader.use();
		
		polygonVBO.setData(data);
		polygonVAO.draw(GL_TRIANGLE_FAN, data.remaining() / 2);
		
		matModelView.popMatrix();
	}
	
	@Override
	public void drawCircle(float x, float y, float radius, Vector4f c) {
		matModelView.pushMatrix();
		matModelView.translate(x, y, 0.0f);
		matModelView.scale(radius, radius, 1.0f);
		
		simpleShader.setProjectionMatrix(matProjection);
		simpleShader.setModelViewMatrix(matModelView);
		simpleShader.setColor(c);
		simpleShader.use();
		
		// Draw circle
		circle.draw(GL_TRIANGLE_FAN, CIRCLE_VERTICES);
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
}

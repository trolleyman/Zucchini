package game.render;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import game.InputHandler;
import game.KeyboardManager;

public class Renderer implements IRenderer {
	// window handle
	private long window;
	
	// If the window is fullscreen
	private boolean fullscreen;
	
	// input handler
	private InputHandler ih;
	
	// Images loaded
	private HashMap<String, Image> images;
	
	private int windowW;
	private int windowH;
	
	// Should the game recalculate the projection matrix on the next frame?
	private boolean dirty;

	private KeyboardManager km;
	
	private IResizeCallback resizeCallback;
	
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
			this.ih.handleCursorPos(xpos, ypos);
		});
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			this.ih.handleMouseButton(button, action, mods);
		});
		glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
			this.ih.handleScroll(xoffset, yoffset);
		});
		glfwSetWindowSizeCallback(window, (window, w, h) -> {
			this.windowW = w;
			this.windowH = h;
			this.dirty = true;
			if (this.resizeCallback != null)
				this.resizeCallback.invoke(this);
		});
		
		// Setup keyboard manager
		this.km = new KeyboardManager(window);
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		GL.createCapabilities();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glDisable(GL_CULL_FACE);
		
		recalculateMatrices();
		
		loadImages();
	}
	
	private void loadImages() {
		images = new HashMap<>();
		
		// Find all .png files in directory "img/"
		Path baseDir = Paths.get(".").toAbsolutePath();
		Path imgsDirPath = Paths.get(baseDir.toString(), "img");
		File imgsDir = imgsDirPath.toFile();
		File[] imgFiles = imgsDir.listFiles((dir, name) -> {
			return name.endsWith(".png");
		});
		
		if (imgFiles == null) {
			System.out.println("No images loaded.");
			return;
		}
		
		for (File file : imgFiles) {
			Image i = new Image(file.toString());
			images.put(file.getName(), i);
		}
		System.out.println(images.size() + " image(s) loaded.");
	}
	
	private void recalculateMatrices() {
		this.dirty = false;
		glViewport(0, 0, windowW, windowH);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, windowW, windowH, 0.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
	}
	
	@Override
	public void setInputHandler(InputHandler _ih) {
		this.ih = _ih;
	}
	
	@Override
	public void setResizeCallback(IResizeCallback _resizeCallback) {
		this.resizeCallback = _resizeCallback;
	}
	
	@Override
	public KeyboardManager getKeyboardManager() {
		return km;
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
	}
	
	@Override
	public void destroy() {
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
			recalculateMatrices();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}
	
	@Override
	public void endFrame() {
		glfwSwapBuffers(window); // swap the buffered output
		// poll for events
		glfwPollEvents();
	}
	
	@Override
	public Image getImage(String name) {
		return images.get(name);
	}
	
	@Override
	public void drawImage(String _name, int _x, int _y) {
		if (!images.containsKey(_name)) {
			System.err.println("Error: Texture does not exist: " + _name);
		} else {
			glEnable(GL_TEXTURE_2D);
			Image i = images.get(_name);
			i.bind();
			int w = i.getWidth();
			int h = i.getHeight();
			
			glBegin(GL_QUADS);
			glTexCoord2f(0.0f, 0.0f); glVertex3f((float)_x  , (float)_y  , 0.0f);
			glTexCoord2f(0.0f, 1.0f); glVertex3f((float)_x  , (float)_y+h, 0.0f);
			glTexCoord2f(1.0f, 1.0f); glVertex3f((float)_x+w, (float)_y+h, 0.0f);
			glTexCoord2f(1.0f, 0.0f); glVertex3f((float)_x+w, (float)_y  , 0.0f);
			glEnd();
		}
	}
}

package game.render;

import org.joml.MatrixStackf;
import org.joml.Vector4f;

import game.InputHandler;
import game.render.Align;

/**
 * The interface for the renderer.
 * 
 * @author Callum
 */
public interface IRenderer {

	/**
	 * Sets the {@link InputHandler} that will be called when events are triggered.
	 * <p>
	 * <b>Replaces</b> the current {@link InputHandler}
	 * @param ih The {@link InputHandler}
	 */
	public void setInputHandler(InputHandler ih);
	/**
	 * Sets whether or not to enable VSync
	 * @param enable true to enable VSync
	 */
	public void setVSync(boolean enable);
	
	/**
	 * Returns the width of the renderer. This is the inner width of the window.
	 * @return Number of pixels wide
	 */
	public int getWidth();
	/**
	 * Returns the height of the renderer. This is the inner height of the window.
	 * @return Number of pixels high
	 */
	public int getHeight();
	
	/**
	 * Shows the renderer. This shows the window.
	 */
	public void show();
	/**
	 * Destroys the renderer, freeing any resources it has allocated during runtime.
	 */
	public void destroy();
	
	/**
	 * Returns whether the user has quitted the window.
	 * @return true if the user has requested the window to shut down.
	 */
	public boolean shouldClose();
	
	/**
	 * This should be called at the beginning of every frame, never at any other time.
	 */
	public void beginFrame();
	/**
	 * This should be called at the end of every frame, never at any other time.
	 */
	public void endFrame();
	
	/**
	 * Returns the {@link TextureBank} instance.
	 */
	public TextureBank getImageBank();
	
	/**
	 * Gets the ModelView matrix stack.
	 * <p>
	 * See <a target="_top" href="http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/>here</a> for more info.
	 * @return The matrix stack
	 */
	public MatrixStackf getModelViewMatrix();
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the bottom left)
	 * @param tex The texture specified. See {@link #getImageBank()}.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public default void drawTexture(Texture tex, Align a, float x, float y) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight());
	}
	/**
	 * Draws the texture specified to the screen at x,y (relative to the bottom left) with a specified
	 * width and height.
	 * @param tex The texture specified. See {@link #getImageBank()}.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 */
	public void drawTexture(Texture tex, Align a, float x, float y, float w, float h);
	
	/**
	 * Draws a solid-color box to the screen at x,y (relative to the bottom left) with a specified
	 * width and height and with a specified Color. See {@link game.ColorUtil ColorUtil}.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param c The color
	 */
	public void drawBox(Align a, float x, float y, float w, float h, Vector4f c);
}

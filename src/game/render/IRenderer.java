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
	 * Draws a line with a specified thickness and color
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y0
	 * @param c The color of the line
	 * @param thickness Thickness in pixels of the line
	 */
	public default void drawLine(float x0, float y0, float x1, float y1, Vector4f c, float thickness) {
		// TODO
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the alignment a).
	 * @param tex The texture specified. See {@link #getImageBank()}.
	 * @param a The alignment. See {@link #Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public default void drawTexture(Texture tex, Align a, float x, float y) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), 0.0f);
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the alignment a) with a specified
	 * rotation r.
	 * @param tex The texture specified. See {@link #getImageBank()}.
	 * @param a The alignment. See {@link #Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param r The rotation
	 */
	public default void drawTexture(Texture tex, Align a, float x, float y, float r) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), r);
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the alignment a) with a specified
	 * width, height and rotation.
	 * @param tex The texture specified. See {@link #getImageBank()}.
	 * @param a The alignment. See {@link #Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 */
	public default void drawTexture(Texture tex, Align a, float x, float y, float w, float h) {
		this.drawTexture(tex, a, x, y, w, h, 0.0f);
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the alignment a) with a specified
	 * width, height and rotation.
	 * @param tex The texture specified. See {@link #getImageBank()}.
	 * @param a The alignment. See {@link #Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param r The rotation
	 */
	public void drawTexture(Texture tex, Align a, float x, float y, float w, float h, float r);
	
	/**
	 * Draws a solid-color box to the screen at x,y (relative to the alignment a) with a specified
	 * width, height, Color. See {@link game.ColorUtil ColorUtil}.
	 * @param a The alignment. See {@link #Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param c The color
	 */
	public default void drawBox(Align a, float x, float y, float w, float h, Vector4f c) {
		this.drawBox(a, x, y, w, h, c, 0.0f);
	}
	
	/**
	 * Draws a solid-color box to the screen at x,y (relative to the alignment a) with a specified
	 * width, height, Color (See {@link game.ColorUtil ColorUtil}) and rotation r.
	 * @param a The alignment. See {@link #Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param c The color
	 * @param r The rotation
	 */
	public void drawBox(Align a, float x, float y, float w, float h, Vector4f c, float r);
	
}

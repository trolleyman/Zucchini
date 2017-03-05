package game.render;

import game.ColorUtil;
import game.InputHandler;
import org.joml.MatrixStackf;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

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
	void setInputHandler(InputHandler ih);
	/**
	 * Sets whether or not to enable VSync
	 * @param enable true to enable VSync
	 */
	void setVSync(boolean enable);
	
	/**
	 * Returns the width of the renderer. This is the inner width of the window.
	 * @return Number of pixels wide
	 */
	int getWidth();
	/**
	 * Returns the height of the renderer. This is the inner height of the window.
	 * @return Number of pixels high
	 */
	int getHeight();
	
	/**
	 * Shows the renderer. This shows the window.
	 */
	void show();
	/**
	 * Destroys the renderer, freeing any resources it has allocated during runtime.
	 */
	void destroy();
	
	/**
	 * Returns whether the user has quitted the window.
	 * @return true if the user has requested the window to shut down.
	 */
	boolean shouldClose();
	
	/**
	 * This should be called at the beginning of every frame, never at any other time.
	 */
	void beginFrame();
	/**
	 * This should be called at the end of every frame, never at any other time.
	 */
	void endFrame();
	
	/**
	 * Returns the {@link TextureBank} instance.
	 */
	TextureBank getTextureBank();
	
	/**
	 * Returns the {@link FontBank} instance.
	 */
	FontBank getFontBank();
	
	/**
	 * Gets the ModelView matrix stack.
	 * <p>
	 * See <a target="_top" href="http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/>here</a> for more info.
	 * @return The matrix stack
	 */
	MatrixStackf getModelViewMatrix();
	
	/**
	 * Draws a line with a specified thickness and color
	 * @param x0 First x-coordinate
	 * @param y0 First y-coordinate
	 * @param x1 Second x-coordinate
	 * @param y1 Second y-coordinate
	 * @param c The color of the line. See {@link game.ColorUtil ColorUtil}.
	 * @param thickness Thickness in pixels of the line
	 */
	void drawLine(float x0, float y0, float x1, float y1, Vector4f c, float thickness);
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the
	 * bottom left of the screen and alignment a).
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	default void drawTexture(Texture tex, Align a, float x, float y) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), 0.0f);
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the
	 * bottom left of the screen and alignment a) with a specified rotation r.
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param r The rotation
	 */
	default void drawTexture(Texture tex, Align a, float x, float y, float r) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), r);
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the
	 * bottom left of the screen and alignment a) with a specified
	 * width, height and rotation.
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 */
	default void drawTexture(Texture tex, Align a, float x, float y, float w, float h) {
		this.drawTexture(tex, a, x, y, w, h, 0.0f);
	}
	
	/**
	 * Draws the texture specified to the screen at x,y (relative to the
	 * bottom left of the screen and alignment a) with a specified
	 * width, height and rotation.
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param r The rotation
	 */
	void drawTexture(Texture tex, Align a, float x, float y, float w, float h, float r);
	
	default void drawTextureUV(Texture tex, Align a, float x, float y, float w, float h, float u0, float v0, float u1, float v1) {
		this.drawTextureUV(tex, a, x, y, w, h, 0.0f, u0, v0, u1, v1);
	}
	
	default void drawTextureUV(Texture tex, Align a, float x, float y, float w, float h, float r, float u0, float v0, float u1, float v1) {
		this.drawTextureUV(tex, a, x, y, w, h, r, u0, v0, u1, v1, ColorUtil.WHITE);
	}
	
	void drawTextureUV(Texture tex, Align a, float x, float y, float w, float h, float r, float u0, float v0, float u1, float v1, Vector4f color);
	
	default void drawText(Font f, String s, Align a, boolean fromBaseline, float x, float y, float scale) {
		this.drawText(f, s, a, fromBaseline, x, y, scale, ColorUtil.WHITE);
	}
	
	void drawText(Font f, String s, Align a, boolean fromBaseline, float x, float y, float scale, Vector4f color);
	
	/**
	 * Draws a solid-color box to the screen at x,y (relative to the
	 * bottom left of the screen and alignment a) with a specified
	 * width, height, Color. See {@link game.ColorUtil ColorUtil}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param c The color
	 */
	default void drawBox(Align a, float x, float y, float w, float h, Vector4f c) {
		this.drawBox(a, x, y, w, h, c, 0.0f);
	}
	
	/**
	 * Draws a solid-color box to the screen at x,y (relative to the
	 * bottom left of the screen and alignment a) with a specified
	 * width, height, Color (See {@link game.ColorUtil ColorUtil}) and rotation r.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param c The color
	 * @param r The rotation
	 */
	void drawBox(Align a, float x, float y, float w, float h, Vector4f c, float r);
	
	default void drawTriangleFan(float[] data, float x, float y) {
		this.drawTriangleFan(data, x, y, ColorUtil.WHITE);
	}
	
	/**
	 * Draws a triangle fan of the data provided. See GL_TRIANGLE_FAN for the details.
	 * @param data The data points in [x0, y0, x1, y1, x2, y2, ...] format.
	 * @param x The circle's centre x-coordinate
	 * @param y The circle's centre y-coordinate
	 * @param c The color of the object
	 */
	void drawTriangleFan(float[] data, float x, float y, Vector4f c);

	/**
	 * Draws a triangle fan of the data provided. See GL_TRIANGLE_FAN for the details.
	 * @param data The data points in [x0, y0, x1, y1, x2, y2, ...] format.
	 * @param x The circle's centre x-coordinate
	 * @param y The circle's centre y-coordinate
	 * @param c The color of the object
	 */
	void drawTriangleFan(FloatBuffer data, float x, float y, Vector4f c);

	default void drawCircle(float x, float y, float radius) {
		this.drawCircle(x, y, radius, ColorUtil.WHITE);
	}
	
	void drawCircle(float x, float y, float radius, Vector4f c);
	
	/**
	 * Enables stencil drawing
	 * <p>
	 * Call {@link #disableStencilDraw()} to disable the stencil buffer drawing.
	 * @param i The number to fill the buffer with
	 */
	void enableStencilDraw(int i);
	
	/**
	 * Disables stencil drawing.
	 * <p>
	 * Remember to call {@link #enableStencil(int)} after to actually enable the stencil check.
	 */
	void disableStencilDraw();
	
	/**
	 * Enables stencil checking
	 * @param i The number passed to {@link #enableStencilDraw(int)}
	 */
	void enableStencil(int i);
	
	/**
	 * Disables stencil checking
	 */
	void disableStencil();
	
	/**
	 * Gets the current mouse x-coordinate
	 */
	double getMouseX();
	/**
	 * Gets the current mouse y-coordinate
	 */
	double getMouseY();
}

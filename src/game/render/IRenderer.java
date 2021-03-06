package game.render;

import game.ColorUtil;
import game.InputHandler;
import org.joml.MatrixStackf;
import org.joml.Vector2f;
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
	 * Gets the current renderer settings
	 */
	RenderSettings getRenderSettings();
	
	/**
	 * Sets the current render settings
	 */
	void setRenderSettings(RenderSettings settings);
	
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
	 * Clears the current framebuffer.
	 */
	void clearFrame();
	
	/**
	 * This should be called at the end of every frame, never at any other time.
	 */
	void endFrame();
	
	/**
	 * Gets a free temporary framebuffer and binds it as the current framebuffer.
	 */
	Framebuffer getFreeFramebuffer();
	
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
	 * Draws the texture specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	default void drawTexture(Texture tex, Align a, float x, float y) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), 0.0f);
	}
	
	/**
	 * Draws the texture specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param c The color of the texture
	 */
	default void drawTexture(Texture tex, Align a, float x, float y, Vector4f c) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), 0.0f, c);
	}
	
	/**
	 * Draws the texture specified to the screen
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
	 * Draws the texture specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param r The rotation
	 * @param c The color of the texture
	 */
	default void drawTexture(Texture tex, Align a, float x, float y, float r, Vector4f c) {
		this.drawTexture(tex, a, x, y, tex.getWidth(), tex.getHeight(), r, c);
	}
	
	/**
	 * Draws the texture specified to the screen
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
	 * Draws the texture specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param c The color of the texture
	 */
	default void drawTexture(Texture tex, Align a, float x, float y, float w, float h, Vector4f c) {
		this.drawTexture(tex, a, x, y, w, h, 0.0f, c);
	}
	
	/**
	 * Draws the texture specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param r The rotation
	 */
	default void drawTexture(Texture tex, Align a, float x, float y, float w, float h, float r) {
		this.drawTexture(tex, a, x, y, w, h, r, ColorUtil.WHITE);
	}
	
	/**
	 * Draws the texture specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param r The rotation
	 * @param c The color of the texture
	 */
	void drawTexture(Texture tex, Align a, float x, float y, float w, float h, float r, Vector4f c);

	/**
	 * Draws the texture UV specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param u0 The first u coordinate within the texture
	 * @param v0 The first v coordinate within the texture
	 * @param u1 The second u coordinate within the texture
	 * @param v1 The second v coordinate within the texture
	 */
	default void drawTextureUV(Texture tex, Align a, float x, float y, float w, float h, float u0, float v0, float u1, float v1) {
		this.drawTextureUV(tex, a, x, y, w, h, 0.0f, u0, v0, u1, v1);
	}

	/**
	 * Draws the texture UV specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param r The rotation
	 * @param u0 The first u coordinate within the texture
	 * @param v0 The first v coordinate within the texture
	 * @param u1 The second u coordinate within the texture
	 * @param v1 The second v coordinate within the texture
	 */
	default void drawTextureUV(Texture tex, Align a, float x, float y, float w, float h, float r, float u0, float v0, float u1, float v1) {
		this.drawTextureUV(tex, a, x, y, w, h, r, u0, v0, u1, v1, ColorUtil.WHITE);
	}

	/**
	 * Draws the texture UV specified to the screen
	 * @param tex The texture specified. See {@link #getTextureBank()}.
	 * @param a The alignment. See {@link Align}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param w The width
	 * @param h The height
	 * @param r The rotation
	 * @param u0 The first u coordinate within the texture
	 * @param v0 The first v coordinate within the texture
	 * @param u1 The second u coordinate within the texture
	 * @param v1 The second v coordinate within the texture
	 * @param color The colour of the texture
	 */
	void drawTextureUV(Texture tex, Align a, float x, float y, float w, float h, float r, float u0, float v0, float u1, float v1, Vector4f color);

	/**
	 * Draw the specified string to the screen using a given font, size and pos etc.
	 * @param f The font
	 * @param s The string
	 * @param a The alignment
	 * @param fromBaseline True if it is aligned to the baseline
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param scale The scale of the text
	 */
	default void drawText(Font f, String s, Align a, boolean fromBaseline, float x, float y, float scale) {
		this.drawText(f, s, a, fromBaseline, x, y, scale, ColorUtil.WHITE);
	}

	/**
	 * Draw the specified string to the screen using a given font, size and pos etc.
	 * @param f The font
	 * @param s The string
	 * @param a The alignment
	 * @param fromBaseline True if it is aligned to the baseline
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param scale The scale of the text
	 * @param color The colour of the text
	 */
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

	/**
	 * Draws a triangle fan of the data provided. See GL_TRIANGLE_FAN for the details.
	 * @param data The data points in [x0, y0, x1, y1, x2, y2, ...] format.
	 * @param x The circle's centre x-coordinate
	 * @param y The circle's centre y-coordinate
	 */
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

	/**
	 * Draw a circle to the screen
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param radius The radius of the circle
	 */
	default void drawCircle(float x, float y, float radius) {
		this.drawCircle(x, y, radius, ColorUtil.WHITE);
	}

	/**
	 * Draw a circle ot the screen
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param radius The radius of the circle
	 * @param c The colour of the circle
	 */
	void drawCircle(float x, float y, float radius, Vector4f c);
	
	/**
	 * Draws a point light at the specified position with the specified color and attenuation.
	 * @param data The shape of the light, specified in triangle fan format.
	 * @param c The colour of the light
	 * @param attenuationFactor How the light spreads and fades with distance (a = 1 / (1 + k * d^2))
	 *                          where a is attenuation (intensity)
	 *                          k is the attenuation factor
	 *                          d is the distance
	 *                          The lower the attenuation, the further the light travels.
	 */
	void drawPointLight(FloatBuffer data, Vector4f c, float attenuationFactor);
	
	/**
	 * Draws a spotlight at the specified position with the specified color, attenuation, cone angle and direction.
	 * @param data The shape of the light, specified in triangle fan format.
	 * @param c The colour of the light
	 * @param attenuationFactor How the light spreads and fades with distance (a = 1 / (1 + k * d^2))
	 *                          where a is attenuation (intensity)
	 *                          k is the attenuation factor
	 *                          d is the distance
	 *                          The lower the attenuation, the further the light travels.
	 * @param coneAngleMin The minimum cone angle of the light
	 * @param coneAngleMax The maximum cone angle of the light
	 * @param coneDirection The direction the cone is pointing (center of the cone)
	 */
	default void drawSpotlight(FloatBuffer data, Vector4f c, float attenuationFactor, float coneAngleMin, float coneAngleMax, Vector2f coneDirection) {
		drawSpotlight(data, c, attenuationFactor, coneAngleMin, coneAngleMax, coneDirection.x, coneDirection.y);
	}
	
	/**
	 * Draws a spotlight at the specified position with the specified color, attenuation, cone angle and direction.
	 * @param data The shape of the light, specified in triangle fan format.
	 * @param c The colour of the light
	 * @param attenuationFactor How the light spreads and fades with distance (a = 1 / (1 + k * d^2))
	 *                          where a is attenuation (intensity)
	 *                          k is the attenuation factor
	 *                          d is the distance
	 *                          The lower the attenuation, the further the light travels.
	 * @param coneAngleMin The minimum cone angle of the light
	 * @param coneAngleMax The maximum cone angle of the light
	 * @param coneDirectionX The X direction the cone is pointing
	 * @param coneDirectionY The Y direction the cone is pointing
	 */
	void drawSpotlight(FloatBuffer data, Vector4f c, float attenuationFactor, float coneAngleMin, float coneAngleMax, float coneDirectionX, float coneDirectionY);
	
	/**
	 * Draws a tube light at the
	 * @param x The start x-coordinate of the tube
	 * @param y The start y-coordinate of the tube
	 * @param angle The angle of the tube direction
	 * @param length The length of the tube
	 * @param width The width of the tube
	 * @param c The color of the tube light
	 * @param attenuationFactor The attenuation factor of the light
	 */
	void drawTubeLight(float x, float y, float angle, float length, float width, Vector4f c, float attenuationFactor);
	
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
	
	/**
	 * Draws the world with lighting
	 * @param world The world framebuffer
	 * @param light The lighting framebuffer
	 */
	void drawWorldWithLighting(Framebuffer world, Framebuffer light);
	
	/**
	 * Draws a framebuffer to the currently bound framebuffer
	 * @param framebuffer The framebuffer
	 */
	void drawFramebuffer(Framebuffer framebuffer);
	
	/**
	 * Draws a framebuffer to the currently bound framebuffer with a specified position and size.
	 * The position and size are in Normalized Device Coordinates. (i.e. from -1.0f to 1.0f in both axes)
	 * @param framebuffer The framebuffer
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param h The height
	 * @param w The width
	 */
	void drawFramebuffer(Framebuffer framebuffer, float x, float y, float w, float h);
	
	/**
	 * Draws the glitch effect to the current framebuffer. Each red, green and blue pixel is transformed to
	 * a place equal to the respective color's component in the effect framebuffer multiplied by the red, green
	 * and blue directions respectively.
	 * @param input The input framebuffer
	 * @param effect The effect framebuffer. All black = no effect
	 * @param rDir The red direction of the effect
	 * @param gDir The green direction of the effect
	 * @param bDir The blue direction of the effect
	 */
	void drawGlitchEffect(Framebuffer input, Framebuffer effect, Vector2f rDir, Vector2f gDir, Vector2f bDir);
	
	/**
	 * Sets the default blend equation
	 */
	void setDefaultBlend();
	
	/**
	 * Sets the lighting blend equation
	 */
	void setLightingBlend();
}

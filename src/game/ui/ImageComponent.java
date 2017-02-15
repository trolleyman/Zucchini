package game.ui;

import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;

public class ImageComponent extends UIComponent {
	
	/** The texture that is rendered by default */
	private Texture texture;
	
	/** The image x position */
	private float x;
	/** The image y position */
	private float y;
	private Align a;
	float rot;
	
	/**
	 * Constructs an image component
	 * @param a The alignment point of the button
	 * @param x The x-coordinate of the image (bottom left of the screen to the alignment point)
	 * @param y The x-coordinate of the image (bottom left of the screen to the alignment point)
	 * @param _texture The texture to use for the image
	 * @param rot The rotation of the image in radians (clockwise)
	 */
	public ImageComponent(Align a, float x, float y, Texture _texture, float rot) {
		this.texture = _texture;
		this.x = x;
		this.y = y;
		this.a = a;
		this.rot = rot;
	}
	
	public void render(IRenderer r) {	
		r.drawTexture(texture, a, x, y, r.getWidth(), r.getHeight(), rot);
	}

	@Override
	public void update(double dt) {
		// Does nothing	
	}
	
	/**
	 * Sets the x co-ordinate of the image
	 * @param _x The x co-ordinate
	 */
	public void setX(float _x) {
		this.x = _x;
	}
	
	/**
	 * Sets the y co-ordinate of the image
	 * @param _y The y co-ordinate
	 */
	public void setY(float _y) {
		this.y = _y;
	}
	
	/**
	 * Returns the width of the image
	 */
	public int getWidth() {
		return texture.getWidth();
	}
	
	/**
	 * Returns the height of the image
	 */
	public int getHeight() {
		return texture.getHeight();
	}
	
}

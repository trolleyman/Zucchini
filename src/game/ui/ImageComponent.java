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
	
	public ImageComponent(float x, float y, Texture _texture) {
		this.texture = _texture;
		this.x = x;
		this.y = y;
	}
	
	public void render(IRenderer r) {	
		r.drawTexture(texture, Align.MM, x, y, r.getWidth(), r.getHeight());
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

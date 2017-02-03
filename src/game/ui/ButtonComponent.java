package game.ui;

import static org.lwjgl.glfw.GLFW.*;

import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;

/**
 * The UIButton is a UIComponent that acts as a button.
 * It renders differently depending on what the mouse is doing.
 * 
 * @author Callum
 */
public class ButtonComponent extends UIComponent {
	/** The function that is called when the button is clicked */
	private Runnable callback;
	
	/** The texture that is rendered by default */
	private Texture defaultTexture;
	/** The texture that is rendered when the button is hovered over */
	private Texture hoverTexture;
	/** The texture that is rendered when the button is pressed */
	private Texture pressedTexture;
	
	/** The current texture */
	private Texture currentTexture;
	
	/** The current mouse x */
	private float mx;
	/** The current mouse y */
	private float my;
	
	/** The current button x */
	private float x;
	/** The current button y */
	private float y;
	
	/** Whether the mouse has been pressed down */
	private boolean pressed = false;
	/** Whether the mouse has been released since the last update */
	private boolean released = false;
		
	/**
	 * Constructs a button
	 * @param _callback The callback that is run when the button is clicked
	 * @param _x The x-coordinate of the button
	 * @param _y The y-coordinate of the button
	 * @param _defaultTexture The default image of the button
	 * @param _hoverTexture The image drawn when the mouse hovers over the button
	 * @param _pressedTexture The image drawn when the button is pressed down
	 */
	public ButtonComponent(Runnable _callback, float _x, float _y, Texture _defaultTexture, Texture _hoverTexture, Texture _pressedTexture) {
		this.callback = _callback;
		
		this.defaultTexture = _defaultTexture;
		this.hoverTexture = _hoverTexture;
		this.pressedTexture = _pressedTexture;
		
		this.currentTexture = this.defaultTexture;
		
		this.x = _x;
		this.y = _y;
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		this.mx = (float) xpos;
		this.my = (float) ypos;
	}

	@Override
	public void handleMouseButton(int button, int action, int mods) {
		if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1) {
			this.pressed = true;
		}
		
		if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_1) {
			this.released = true;
		}
	}
	
	/**
	 * Returns whether or not the mouse is currently on the button
	 */
	private boolean isMouseOnButton() {
		return mx >= x && mx < x + currentTexture.getWidth()
			&& my >= y && my < y + currentTexture.getHeight();
	}
	
	@Override
	public void update(double dt) {
		if (this.released) {
			this.pressed = false;
			this.released = false;
			if (isMouseOnButton())
				this.callback.run();
		}
	}

	@Override
	public void render(IRenderer r) {
		if (!isMouseOnButton()) {
			currentTexture = defaultTexture;
		} else if (!this.pressed) {
			currentTexture = hoverTexture;
		} else {
			currentTexture = pressedTexture;
		}
		
		r.drawTexture(currentTexture, Align.BL, x, y);
	}
	
	/**
	 * Sets the x co-ordinate of the button
	 * @param _x The x co-ordinate
	 */
	public void setX(float _x) {
		this.x = _x;
	}
	
	/**
	 * Sets the y co-ordinate of the button
	 * @param _y The y co-ordinate
	 */
	public void setY(float _y) {
		this.y = _y;
	}
	
	/**
	 * Returns the width of the button
	 */
	public int getWidth() {
		return currentTexture.getWidth();
	}
	
	/**
	 * Returns the height of the button
	 */
	public int getHeight() {
		return currentTexture.getHeight();
	}
}

package game.ui.component;

import game.Util;
import game.render.Align;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public abstract class AbstractButtonComponent extends UIComponent {
	/** The current mouse x */
	private float mx;
	/** The current mouse y */
	private float my;
	
	public Align a;
	/** The current button x */
	public float x;
	/** The current button y */
	public float y;
	
	/** Whether the mouse has been pressed down */
	private boolean pressed = false;
	/** Whether the mouse has been released since the last update */
	private boolean released = false;
	
	public AbstractButtonComponent(Align a, float x, float y) {
		this.a = a;
		this.x = x;
		this.y = y;
	}
	
	/** Called when the button is in it's default situation */
	protected abstract void onDefault();
	/** Called when the mouse hovers over the button (i.e. the mouse is over the button) */
	protected abstract void onHover();
	/** Called when the button is pressed (i.e. the mouse button is pressed on the button) */
	protected abstract void onPressed();
	/** Called when the button is clicked (i.e. pressed + released on the button) */
	protected abstract void onClicked();
	
	protected abstract float getWidth();
	protected abstract float getHeight();
	
	protected float getMouseX() {
		return mx;
	}
	
	protected float getMouseY() {
		return my;
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
	
	private boolean isMouseOnButton() {
		return Util.isPointInRect(mx, my, a, x, y, getWidth(), getHeight());
	}
	
	@Override
	public void update(double dt) {
		if (this.released) {
			this.pressed = false;
			this.released = false;
			if (isMouseOnButton())
				this.onClicked();
		}
		
		if (!isMouseOnButton()) {
			this.onDefault();
		} else if (!this.pressed) {
			this.onHover();
		} else {
			this.onPressed();
		}
	}
}

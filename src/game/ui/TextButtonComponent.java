package game.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.joml.Vector4f;

import game.ColorUtil;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.render.Texture;

public class TextButtonComponent extends UIComponent {

	/** The function that is called when the button is clicked */
	//TODO: Is this needed?
	private Runnable callback;
	
	private boolean selected = false;
	
	private String s;
	
	private Font f;
	private Align a;
	private float scale;
	
	private Vector4f colour = ColorUtil.BLUE;
	
	/** The current mouse x */
	private float mx;
	/** The current mouse y */
	private float my;
	
	/** The current button x */
	private float x;
	/** The current button y */
	private float y;
	
	private float BOX_W;
	private float BOX_H;
	
	/** Whether the mouse has been pressed down */
	private boolean pressed = false;
	/** Whether the mouse has been released since the last update */
	private boolean released = false;
	
	public TextButtonComponent(Runnable _callback, Align _a, float _x, float _y, Font _f, String _s, float scale) {
		this.callback = _callback;
		this.a = _a;
		this.x = _x;
		this.y = _y;
		this.f = _f;
		this.s = _s;
		this.scale = scale;
		
		BOX_W = 500;
		BOX_H = 64*scale;
		
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
		return mx >= x && mx < x + BOX_W
			&& my >= y && my < y + BOX_H;
	}
	
	@Override
	public void update(double dt) {
		if (this.released) {
			this.pressed = false;
			this.released = false;
			if (isMouseOnButton())
				this.callback.run();
		}
		
		if (selected) {
			colour = ColorUtil.GREEN;
		} else if (isMouseOnButton()) {
			colour = ColorUtil.BLUE;
		} else {
			colour = ColorUtil.BLACK;
		}
	}

	@Override
	public void render(IRenderer r) {
		r.drawBox(a, x, y, BOX_W, BOX_H, colour);
		r.drawText(f, s, x+10, y+14, scale);
		
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected(boolean b) {
		selected = b;
	}
	
	public String getString() {
		return s;
	}
	
	public void setString(String s) {
		this.s = s;
	}
	
	/**
	 * Sets the y co-ordinate of the button
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
		return (int) BOX_W;
	}
	
	/**
	 * Returns the height of the button
	 */
	public int getHeight() {
		return (int) BOX_H;
	}

}

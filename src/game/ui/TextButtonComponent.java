package game.ui;

import game.ColorUtil;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class TextButtonComponent extends UIComponent {

	/** The function that is called when the button is clicked */
	private Runnable callback;

	private boolean selected = false;

	private String s;

	private Font f;
	private Align a;
	private float scale;

	private Vector4f box_colour = ColorUtil.BLACK;
    private Vector4f border_colour = ColorUtil.WHITE;

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
	private float border_width;

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


		border_width = 5;
		BOX_W = 580;
		BOX_H = 64*scale*2;

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
            border_colour = ColorUtil.WHITE;
		} else if (isMouseOnButton()) {
            border_colour = ColorUtil.LIGHT_GREY;
		} else {
            border_colour = ColorUtil.DARK_GREY;
		}
	}

	@Override
	public void render(IRenderer r) {
		r.drawBox(a, x, y, BOX_W, BOX_H, border_colour);
		r.drawBox(a, x+border_width, y+border_width, BOX_W-2*border_width, BOX_H-2*border_width, box_colour);
		r.drawText(f, s, Align.TL, x+15, y-2, scale);
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

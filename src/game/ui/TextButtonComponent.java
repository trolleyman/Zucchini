package game.ui;

import game.ColorUtil;
import game.LobbyInfo;
import game.PlayerInfo;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import org.joml.Vector4f;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;

import static org.lwjgl.glfw.GLFW.*;

public class TextButtonComponent extends UIComponent
{

	/** The function that is called when the button is clicked */
	private Runnable callback;

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

	// ** The dimensions of the box */
	private float BOX_W;
	private float BOX_H;
	private float border_width;
	// * The alignment of the box/button */
	private Align a;

	// * The font used for the text of the button */
	private Font f;
	// * The scale of the text */
	private float scale;

	/** The info of the lobby */
	private LobbyInfo lobbyInfo;

	/** the status of the button - Selected or not */
	private boolean selected = false;

	/** Whether the mouse has been pressed down */
	private boolean pressed = false;
	/** Whether the mouse has been released since the last update */
	private boolean released = false;

	private String text;

	public TextButtonComponent(Runnable _callback, Align _a, float _x, float _y, Font _f, float scale, LobbyInfo lobbyInfo)
	{

		this.callback = _callback;
		this.a = _a;
		this.x = _x;
		this.y = _y;
		this.f = _f;
		this.scale = scale;
		this.lobbyInfo = lobbyInfo;

		border_width = 5;
		BOX_W = 800;
		BOX_H = 64 * scale * 2;

	}

	public TextButtonComponent(Runnable _callback, Align _a, float height, float width, Font _f, float scale, String text)
	{

		this.callback = _callback;
		this.a = _a;
		this.x = 0;
		this.y = 0;
		this.f = _f;
		this.scale = scale;
		this.lobbyInfo = null;

		this.text = text;

		border_width = 10;
		BOX_W = height;
		BOX_H = width;

	}

	@Override
	public void handleCursorPos(double xpos, double ypos)
	{
		this.mx = (float) xpos;
		this.my = (float) ypos;
	}

	@Override
	public void handleMouseButton(int button, int action, int mods)
	{
		if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1)
		{
			this.pressed = true;
		}

		if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_1)
		{
			this.released = true;
		}
	}

	private boolean isMouseOnButton()
	{
		return mx >= x && mx < x + BOX_W && my >= y && my < y + BOX_H;
	}

	@Override
	public void update(double dt)
	{
		if (this.released)
		{
			this.pressed = false;
			this.released = false;
			if (isMouseOnButton())
				this.callback.run();
		}

		// Set the colour of the border of the box based on select or hover
		if (selected)
		{
			border_colour = ColorUtil.WHITE;
		} else if (isMouseOnButton())
		{
			border_colour = ColorUtil.LIGHT_GREY;
		} else
		{
			border_colour = ColorUtil.DARK_GREY;
		}
	}

	@Override
	public void render(IRenderer r)
	{
		// Draw the outer box (also acts as the border)
		r.drawBox(a, x, y, BOX_W, BOX_H, border_colour);
		// Draw the inner box
		r.drawBox(a, x + border_width, y + border_width, BOX_W - 2 * border_width, BOX_H - 2 * border_width, box_colour);
		// Draw the name of the lobby
		if (lobbyInfo != null)
		{
			r.drawText(f, lobbyInfo.getLobbyName(), a, false, x + 15, y - 2, scale);
			// Draw the current and max number of players
			// TODO: display correct number of players in the lobby
			r.drawText(f, lobbyInfo.getPlayerInfo().length + "/" + lobbyInfo.getMaxPlayers() + " Players", a, false, x + 460, y - 2, scale);
		} else
		{
			r.drawText(f, text, a, false, x + 4, y, scale);
		}
	}

	/**
	 * Returns whether the button is selected or not
	 * 
	 * @return boolean Is the button currently selected?
	 */
	public boolean getSelected()
	{
		return selected;
	}

	/**
	 * Sets the button to be selected or not selected
	 * 
	 * @param b
	 *            true/false based on whether you want the button to be selected
	 *            or not
	 */
	public void setSelected(boolean b)
	{
		selected = b;
	}

	/**
	 * Sets the y co-ordinate of the button
	 * 
	 * @param _x
	 *            The x co-ordinate
	 */
	public void setX(float _x)
	{
		this.x = _x;
	}

	/**
	 * Sets the y co-ordinate of the button
	 * 
	 * @param _y
	 *            The y co-ordinate
	 */
	public void setY(float _y)
	{
		this.y = _y;
	}

	/**
	 * Returns the width of the button
	 */
	public int getWidth()
	{
		return (int) BOX_W;
	}

	/**
	 * Returns the height of the button
	 */
	public int getHeight()
	{
		return (int) BOX_H;
	}

}

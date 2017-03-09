package game.ui.component;

import game.ColorUtil;
import game.LobbyInfo;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class TextButtonComponent extends AbstractButtonComponent {
	
	/** The function that is called when the button is clicked */
	private Runnable callback;
	
	private Vector4f box_colour = ColorUtil.BLACK;
    private Vector4f border_colour = ColorUtil.WHITE;

	/** The width of the box */
	private final float BOX_W;
	private final float BOX_H;
	private final float BORDER_WIDTH;

	/** The font used for the text of the button */
	private Font f;
	/** The scale of the text */
	private float scale;

	/** The info of the lobby */
	private LobbyInfo lobbyInfo;

	/** the status of the button - Selected or not */
	private boolean selected = false;

	public TextButtonComponent(Runnable _callback, Align _a, float _x, float _y, Font _f, float scale, LobbyInfo lobbyInfo) {
		super(_a, _x, _y);
		this.callback = _callback;
		this.f = _f;
		this.scale = scale;
		this.lobbyInfo = lobbyInfo;


		BORDER_WIDTH = 5;
		BOX_W = 800;
		BOX_H = 64*scale*2;

	}
	
	@Override
	protected void onDefault() {
		border_colour = ColorUtil.DARK_GREY;
	}
	
	@Override
	protected void onHover() {
		border_colour = ColorUtil.LIGHT_GREY;
	}
	
	@Override
	protected void onPressed() {
		border_colour = ColorUtil.WHITE;
	}
	
	@Override
	protected void onClicked() {
		this.setSelected(true);
		this.callback.run();
	}

	@Override
	public void render(IRenderer r) {
		Vector4f bcol = border_colour;
		if (selected) {
			bcol = ColorUtil.WHITE;
		}
		// Draw the outer box (also acts as the border)
		r.drawBox(a, x, y, BOX_W, BOX_H, bcol);
		// Draw the inner box
		r.drawBox(a, x+ BORDER_WIDTH, y+ BORDER_WIDTH, BOX_W-2* BORDER_WIDTH, BOX_H-2* BORDER_WIDTH, box_colour);
		// Draw the name of the lobby
		r.drawText(f, lobbyInfo.getLobbyName(), a, false, x+15, y-2, scale);
		// Draw the current and max number of players
		r.drawText(f, lobbyInfo.getPlayerInfo().length+"/"+lobbyInfo.getMaxPlayers()+" Players", a, false, x+460, y-2, scale);
	}

	/**
	 * Returns whether the button is selected or not
	 * @return boolean Is the button currently selected?
	 */
	public boolean getSelected() {
		return selected;
	}

	/**
	 * Sets the button to be selected or not selected
	 * @param b true/false based on whether you want the button to be selected or not
	 */
	public void setSelected(boolean b) {
		selected = b;
	}
	
	@Override
	public float getWidth() {
		return BOX_W;
	}
	
	@Override
	public float getHeight() {
		return BOX_H;
	}
}

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
	private final float INTERNAL_PADDING;
	
	/** The font used for the text of the button */
	private Font f;
	/** The scale of the text */
	private float scale;

	/** The info of the lobby */
	private LobbyInfo lobbyInfo;

	/** the status of the button - Selected or not */
	private boolean selected = false;

	/**
	 * Constructs a TextButtonComponent
	 * @param _callback The callback
	 * @param _x The x coordinate
	 * @param _y The y coordinate
	 * @param _f The font
	 * @param scale The scale of the font
	 * @param lobbyInfo The lobby info to be displayed
	 */
	public TextButtonComponent(Runnable _callback, float _x, float _y, Font _f, float scale, LobbyInfo lobbyInfo) {
		super(Align.BL, _x, _y);
		this.callback = _callback;
		this.f = _f;
		this.scale = scale;
		this.lobbyInfo = lobbyInfo;


		BORDER_WIDTH = 5;
		INTERNAL_PADDING = 12;
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
	public void onClicked() {
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
		r.drawBox(Align.BL, x, y, BOX_W, BOX_H, bcol);
		// Draw the inner box
		r.drawBox(Align.BL, x+ BORDER_WIDTH, y+ BORDER_WIDTH, BOX_W-2* BORDER_WIDTH, BOX_H-2* BORDER_WIDTH, box_colour);
		// Draw the name of the lobby
		r.drawText(f, lobbyInfo.getLobbyName(),
				Align.BL, false, x+BORDER_WIDTH+INTERNAL_PADDING, y+BORDER_WIDTH+INTERNAL_PADDING, scale);
		// Draw the current and max number of players
		r.drawText(f, lobbyInfo.getPlayerInfo().length+"/"+lobbyInfo.getMaxPlayers()+" Players",
				Align.BR, false, x+BOX_W-BORDER_WIDTH-INTERNAL_PADDING, y+BORDER_WIDTH+INTERNAL_PADDING, scale);
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

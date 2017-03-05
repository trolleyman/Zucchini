package game.ui;

import game.ColorUtil;
import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.exception.ProtocolException;
import game.render.Align;
import game.render.IRenderer;
import game.ui.component.ButtonComponent;
import game.world.ClientWorld;
import game.world.entity.Item;
import game.world.entity.Player;
import game.world.entity.weapon.Weapon;
import game.world.map.Map;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The GameUI is the UI responsible for rendering, updating the game and handling input
 * 
 * @author Abbygayle Wiggins
 */
public class GameUI extends UI implements InputPipeMulti {
	private static final Vector4f ESCAPE_BG_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 0.4f);
	
	/** The world of the game */
	private ClientWorld world;
	private float winWidth; // window width
	private float winHeight; //window height
	
	private float barWidth;
	private float barHeight;
	private float mapSize;
	
	// Escape menu buttons
	private ButtonComponent fileBtn;
	private ButtonComponent helpBtn;
	private ButtonComponent audioBtn;
	private ButtonComponent continueBtn;
	private ButtonComponent quitBtn;
	// Escape menu button sizes
	private int buttonWidth;
	private int buttonHeight;
	
	private boolean escapeMenu;
	
	private ArrayList<InputHandler> gameInputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> escapeInputHandlers = new ArrayList<>();
	private UI nextUI;
	
	/**
	 * Constructs a new GameUI
	 * @param _world The world
	 */
	public GameUI(UI _ui, ClientWorld _world) {
		super(_ui);
		this.world = _world;
		this.escapeMenu = false;
		this.nextUI = this;
		
		// Setup game
		this.gameInputHandlers.add(world);
		
		// Setup escape menu
		setupEscapeMenu();
	}
	
	private void setupEscapeMenu() {
		buttonHeight = 200;
		buttonWidth = 200;
		
		fileBtn = new ButtonComponent(() -> {},
				Align.BL, 0, 0,
				textureBank.getTexture("file.png"),
				textureBank.getTexture("file2.png"),
				textureBank.getTexture("file2.png")
		);
		
		helpBtn = new ButtonComponent(() -> {},
				Align.BL, 0, 0,
				textureBank.getTexture("help.png"),
				textureBank.getTexture("help2.png"),
				textureBank.getTexture("help2.png")
		);
		
		audioBtn = new ButtonComponent(() -> {},
				Align.BL, 0, 0,
				textureBank.getTexture("audio.png"),
				textureBank.getTexture("audio2.png"),
				textureBank.getTexture("audio2.png")
		);
		
		quitBtn = new ButtonComponent(
				() -> this.nextUI = new StartUI(this),
				Align.BL, 0, 0,
				textureBank.getTexture("quit.png"),
				textureBank.getTexture("quit2.png"),
				textureBank.getTexture("quit2.png")
		);
		
		continueBtn = new ButtonComponent(
				() -> this.escapeMenu = false,
				Align.BL, 0, 0,
				textureBank.getTexture("continue.png"),
				textureBank.getTexture("continue2.png"),
				textureBank.getTexture("continue2.png")
		);
		
		this.escapeInputHandlers.add(fileBtn);
		this.escapeInputHandlers.add(helpBtn);
		this.escapeInputHandlers.add(audioBtn);
		this.escapeInputHandlers.add(quitBtn);
		this.escapeInputHandlers.add(continueBtn);
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		if (!this.escapeMenu)
			return this.gameInputHandlers;
		else
			return this.escapeInputHandlers;
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
		 	this.escapeMenu = !this.escapeMenu;
		}
	}
	
	@Override
	public void handleResize(int w, int h) {
		InputPipeMulti.super.handleResize(w, h);
		this.winWidth = w;
		this.winHeight = h;
	}
	
	@Override
	public void update(double dt) {
		barWidth = (winWidth/3);
		barHeight = (winHeight/10);
		mapSize = (winHeight/5);
		this.world.update(dt);
		
		if (this.escapeMenu) {
			fileBtn.update(dt);
			helpBtn.update(dt);
			audioBtn.update(dt);
			quitBtn.update(dt);
			continueBtn.update(dt);
		}
	}
	
	@Override
	public void render(IRenderer r) {
		this.world.render(r);
		
		// Draw mini-map
		this.world.renderMiniMap(r, Util.HUD_PADDING, Util.HUD_PADDING, 300.0f, 300.0f, 30.0f);
		
		// Draw current ammo
		Player p = this.world.getPlayer();
		if (p != null) {
			Item i = p.getHeldItem();
			if (i != null) {
				i.renderUI(r);
			}
		}
		
		// If the game is escaped, draw the escape menu
		if (this.escapeMenu) {
			r.drawBox(Align.BL, 0.0f, 0.0f, r.getWidth(), r.getHeight(), ESCAPE_BG_COLOR);
			
			float height = winHeight - 100;
			fileBtn.setX(0);
			fileBtn.setY(height);
			helpBtn.setX(0);
			helpBtn.setY(height - (buttonHeight));
			audioBtn.setX(0);
			audioBtn.setY(height - (2*buttonHeight));;
			quitBtn.setX(0);
			quitBtn.setY(height - (3*buttonHeight));
			continueBtn.setX(0);
			continueBtn.setY(height - (4*buttonHeight));
			
			fileBtn.render(r);
			helpBtn.render(r);
			audioBtn.render(r);
			quitBtn.render(r);
			continueBtn.render(r);
		}
	}
	
	@Override
	public UI next() {
		return nextUI;
	}
	
	@Override
	public String toString() {
		return "GameUI";
	}

	@Override
	public void destroy() {
		try {
			connection.sendLobbyLeaveRequest();
		} catch (ProtocolException e) {
			// This is fine
		}
	}
}

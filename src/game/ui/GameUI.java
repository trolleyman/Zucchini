package game.ui;

import game.ColorUtil;
import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.ui.component.ScoreboardComponent;
import game.world.ClientWorld;
import game.world.PlayerScoreboardInfo;
import game.world.entity.damage.Damage;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The GameUI is the UI responsible for rendering, updating the game and handling input
 * 
 * @author Abbygayle Wiggins
 */
public class GameUI extends UI implements InputPipeMulti {
	private static final Vector4f SCOREBOARD_BACKGROUND_COLOR = new Vector4f(0.1f, 0.1f, 0.1f, 0.3f);
	
	/** The world of the game */
	private ClientWorld world;
	
	/** The window width and window height */
	private float windowW; 
	private float windowH; 
		
	/** To handle the inputs */
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> scoreboardInputHandlers = new ArrayList<>();
	
	/** The next UI class to be shown */
	private UI nextUI;
	
	/** Whether the score board should be rendered */
	private boolean scoreboardShown;
	
	/** The score board */
	private ScoreboardComponent scoreboardComponent;
	
	/**
	 * Constructs a new GameUI
	 * @param _ui The UI superclass
	 * @param _world The world
	 */
	public GameUI(UI _ui, ClientWorld _world) {
		super(_ui);
		this.world = _world;
		this.inputHandlers.add(world);
		this.scoreboardInputHandlers.add(world);
		
		nextUI = this;
		
		scoreboardComponent = new ScoreboardComponent(world.getScoreboard(), 0.0f);
		this.scoreboardInputHandlers.add(scoreboardComponent);
	}
	
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		if (scoreboardShown) {
			return scoreboardInputHandlers;
		} else {
			return inputHandlers;
		}
	}
	
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
			System.out.println("Escape pressed");
			this.nextUI = new EscapeUI(this, world);
		} else if (key == GLFW_KEY_TAB && action == GLFW_PRESS) {
			scoreboardShown = true;
		} else if (key == GLFW_KEY_TAB && action == GLFW_RELEASE) {
			scoreboardShown = false;
		}
	}
	
	@Override
	public void handleResize(int w, int h) {
		this.windowW = w;
		this.windowH = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
		this.world.update(dt);
		
		if (world.isPlayerDead())
			scoreboardShown = true;
		if (this.scoreboardShown) {
			this.scoreboardComponent.update(dt);
			scoreboardComponent.setScoreboard(world.getScoreboard());
		}
	}
	
	/** The render method - renders the world and additional features.
	 *  These features include the minimap and health bar.
	 *  @param r The instance of IRenderer
	 */
	@Override
	public void render(IRenderer r) {
		this.world.render(r);
		this.world.renderHUD(r);
		
		// === Render UI ===
		float titleScale = 2.0f;
		Font f = r.getFontBank().getFont("emulogic.ttf");
		if (scoreboardShown) {
			r.drawBox(Align.BL, 0.0f, 0.0f, r.getWidth(), r.getHeight(), SCOREBOARD_BACKGROUND_COLOR);
		}
		float y = 0.0f;
		if (world.isPlayerDead()) {
			r.drawText(f, "You are dead.", Align.TM, false, r.getWidth()/2, r.getHeight() - Util.HUD_PADDING, titleScale, ColorUtil.RED);
			PlayerScoreboardInfo p = world.getScoreboard().getPlayer(connection.getName());
			Damage d = p == null ? null : p.lastDamage;
			String s = d == null ? "Unknown" : d.type.getDeathAdjective();
			r.drawText(f, "Cause: " + s, Align.TM, false, r.getWidth()/2, r.getHeight() - Util.HUD_PADDING - 50.0f - f.getHeight(titleScale), 1.0f, ColorUtil.RED);
			
			y = r.getHeight() - Util.HUD_PADDING - f.getHeight(titleScale)*2 - 55.0f;
		} else if (scoreboardShown) {
			// Draw scoreboard
			r.drawText(f, "Scoreboard", Align.TM, false, r.getWidth() / 2, r.getHeight() - Util.HUD_PADDING, titleScale, ColorUtil.YELLOW);
			y = r.getHeight() - Util.HUD_PADDING - f.getHeight(titleScale) - 80.0f;
		}
		
		if (scoreboardShown) {
			scoreboardComponent.setStartY(y);
			scoreboardComponent.render(r);
		}
	}
	
	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		// Do nothing
	}
}

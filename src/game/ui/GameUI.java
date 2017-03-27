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
	protected ClientWorld world;
	private float windowW; //window width
	private float windowH; //window height
		
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private ArrayList<InputHandler> scoreboardInputHandlers = new ArrayList<>();
	
	private boolean destroy = false;
	private UI nextUI;
	
	protected boolean scoreboardShown;
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
		
		if (world.isPlayerDead() || world.hasPlayerWon())
			scoreboardShown = true;
		if (this.scoreboardShown) {
			this.scoreboardComponent.update(dt);
			scoreboardComponent.setScoreboard(world.getScoreboard());
		}
	}
	
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
		if (world.hasPlayerWon()) {
			r.drawText(f, "Victory!", Align.TM, false, r.getWidth()/2, r.getHeight() - Util.HUD_PADDING, titleScale, ColorUtil.GREEN);
			y = r.getHeight() - Util.HUD_PADDING - f.getHeight(titleScale) - 55.0f;
			
		} else if (world.isPlayerDead()) {
			r.drawText(f, "You are dead.", Align.TM, false, r.getWidth()/2, r.getHeight() - Util.HUD_PADDING, titleScale, ColorUtil.RED);
			PlayerScoreboardInfo p = world.getScoreboard().getPlayer(getConnection().getName());
			Damage d = p == null ? null : p.lastDamage;
			String s = d == null ? "Unknown" : d.source.readableName;
			r.drawText(f, "Killed by " + s + ".", Align.TM, false, r.getWidth()/2, r.getHeight() - Util.HUD_PADDING - 50.0f - f.getHeight(titleScale), 1.0f, ColorUtil.RED);
			
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
		
		if (world.isFinished()) {
			destroy = true;
			nextUI = new LobbyWaitUI(this, world.getLobbyName(), false);
		}
	}
	
	@Override
	public UI next() {
		return nextUI;
	}

	@Override
	public void destroy() {
		if (destroy)
			this.world.destroy();
	}
}

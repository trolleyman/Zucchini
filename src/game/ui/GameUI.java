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
	private static final Vector4f SCOREBOARD_BACKGROUND_COLOR = new Vector4f(0.1f, 0.1f, 0.1f, 0.5f);
	
	/** The world of the game */
	private ClientWorld world;
	private float winWidth; //window width
	private float winHeight; //window height
	private float barWidth;
	private float barHeight;
	private float mapSize;
	private float playerHealth;
	private float maxHealth;
	
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	
	private UI nextUI;
	
	private boolean scoreboardShown;
	private ScoreboardComponent scoreboardComponent;
	
	/**
	 * Constructs a new GameUI
	 * @param _world The world
	 */
	public GameUI(UI _ui, ClientWorld _world) {
		super(_ui);
		this.world = _world;
		this.inputHandlers.add(world);
		
		nextUI = this;
		
		scoreboardComponent = new ScoreboardComponent(world.getScoreboard(), 0.0f);
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
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
		this.winWidth = w;
		this.winHeight = h;
		InputPipeMulti.super.handleResize(w, h);
	}
	
	@Override
	public void update(double dt) {
		barWidth = (winWidth/3);
		barHeight = (winHeight/10);
		mapSize = (winHeight/5);
		this.world.update(dt);
		
		if (this.scoreboardShown || world.isPlayerDead()) {
			this.scoreboardComponent.update(dt);
			scoreboardComponent.setScoreboard(world.getScoreboard());
		}
	}
	
	@Override
	public void render(IRenderer r) {
		this.world.render(r);
		
		renderScoreboard(r);
		renderHealthBar(r);
	}
	
	private void renderScoreboard(IRenderer r) {
		float titleScale = 2.0f;
		Font f = r.getFontBank().getFont("emulogic.ttf");
		if (scoreboardShown || world.isPlayerDead()) {
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
		
		if (scoreboardShown || world.isPlayerDead()) {
			scoreboardComponent.setStartY(y);
			scoreboardComponent.render(r);
		}
	}
	
	private void renderHealthBar(IRenderer r){
		if(world.getPlayer() == null){
			maxHealth = 10.0f;
			playerHealth = 10.0f;
		}else{
			maxHealth = world.getPlayer().getMaxHealth();
			playerHealth = world.getPlayer().getHealth();
		}
		
		r.drawBox(Align.TR, winWidth - 20, winHeight - 20, (float) 30 * maxHealth, (float) 20, ColorUtil.GREEN);//max health
		r.drawBox(Align.TR, winWidth - 20, winHeight - 20, 30 * (maxHealth - playerHealth), 20, ColorUtil.RED);
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

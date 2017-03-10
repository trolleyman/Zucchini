package game.ui;

import game.ColorUtil;
import game.InputHandler;
import game.InputPipeMulti;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.ClientWorld;
import game.world.entity.Item;
import game.world.entity.Player;
import game.world.entity.weapon.Weapon;
import game.world.map.Map;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The GameUI is the UI responsible for rendering, updating the game and handling input
 * 
 * @author Abbygayle Wiggins
 */
public class GameUI extends UI implements InputPipeMulti {
	
	/** The world of the game */
	private ClientWorld world;
	private float winWidth; // window width
	private float winHeight; //window height
	private float barWidth;
	private float barHeight;
	private float mapSize;
	
	private ArrayList<InputHandler> inputHandlers = new ArrayList<>();
	private UI nextUI;
   
	/**
	 * Constructs a new GameUI
	 * @param _world The world
	 */
	public GameUI(UI _ui, ClientWorld _world) {
		super(_ui);
		this.world = _world;
		this.inputHandlers.add(world);
		
		nextUI = this;
	}
	
	@Override
	public ArrayList<InputHandler> getHandlers() {
		return this.inputHandlers;
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		InputPipeMulti.super.handleKey(key, scancode, action, mods);
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
		 	System.out.println("escape pressed");
			this.nextUI = new EscapeUI(this, world);
		} else if (key == GLFW_KEY_UP && action == GLFW_PRESS){
			System.out.println("M pressed");
			this.nextUI = new MiniMap(this, world);
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
	}

	@Override
	public void render(IRenderer r) {
		this.world.render(r);
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
		// Do nothing
	}
}

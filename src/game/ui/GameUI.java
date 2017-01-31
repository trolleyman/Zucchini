package game.ui;

import game.InputHandler;
import game.InputPipe;
import game.render.IRenderer;
import game.world.ClientWorld;
import game.world.World;

/**
 * The GameUI is the UI responsible for rendering, updating the game and handling input
 * 
 * @author Callum
 */
public class GameUI extends UI implements InputPipe {
	/** The world of the game */
	private ClientWorld world;
	
	/**
	 * Constructs a new GameUI
	 * @param renderer The renderer of the game
	 * @param _world The world
	 */
	public GameUI(ClientWorld _world) {
		super();
		this.world = _world;
	}
	
	@Override
	public InputHandler getHandler() {
		return this.world;
	}
	
	@Override
	public void update(double dt) {
		this.world.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		this.world.render(r);
	}
	
	@Override
	public UI next() {
		// TODO: Handle exiting
		return this;
	}
	
	@Override
	public String toString() {
		return "GameUI";
	}
}

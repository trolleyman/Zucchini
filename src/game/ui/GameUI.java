package game.ui;

import game.KeyboardManager;
import game.render.IRenderer;
import game.world.World;

public class GameUI extends UI {
	
	public World world;
	
	GameUI(KeyboardManager _km, World _world) {
		super(_km);
		setWorld(_world);
	}
	
	void setWorld(World _world) {
		this.world = _world;
	}
	
	@Override
	public void update(double dt) {
		
	}
	
	@Override
	public void render(IRenderer r) {
		
	}
	
	@Override
	public UI next() {
		return this;
	}
}

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
	
	@Override
	public void handleKey(int _key, int _scancode, int _action, int _mods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleChar(char _c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCursorPos(double _xpos, double _ypos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMouseButton(int _button, int _action, int _mods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleScroll(double _xoffset, double _yoffset) {
		// TODO Auto-generated method stub
		
	}
}

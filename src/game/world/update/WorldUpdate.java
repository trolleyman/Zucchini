package game.world.update;

import game.world.World;

public abstract class WorldUpdate {
	public abstract void updateWorld(World w);
	
	@Override
	protected abstract WorldUpdate clone();
}

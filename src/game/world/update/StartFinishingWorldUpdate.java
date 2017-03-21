package game.world.update;

import game.world.World;

public class StartFinishingWorldUpdate extends WorldUpdate {
	@Override
	public void updateWorld(World w) {
		w.startFinishing();
	}
}

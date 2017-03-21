package game.world.update;

import game.world.World;

public class StartTimeWorldUpdate extends WorldUpdate {
	private float startTime;
	
	public StartTimeWorldUpdate(float startTime) {
		this.startTime = startTime;
	}
	
	public float getStartTime() {
		return startTime;
	}
	
	@Override
	public void updateWorld(World w) {
		w.setStartTime(startTime);
	}
}

package game.world.update;

import game.world.World;

public class EndTimeWorldUpdate extends WorldUpdate {
	private final float endTime;
	
	public EndTimeWorldUpdate(float endTime) {
		super();
		
		this.endTime = endTime;
	}
	
	@Override
	public void updateWorld(World w) {
		w.setEndTime(this.endTime);
	}
}

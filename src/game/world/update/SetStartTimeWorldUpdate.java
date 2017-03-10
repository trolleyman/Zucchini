package game.world.update;

import game.world.World;

public class SetStartTimeWorldUpdate extends WorldUpdate {
	private float startTime;
	
	public SetStartTimeWorldUpdate(SetStartTimeWorldUpdate u) {
		this.startTime = u.startTime;
	}
	
	public SetStartTimeWorldUpdate(float startTime) {
		this.startTime = startTime;
	}
	
	@Override
	public void updateWorld(World w) {
		w.setStartTime(startTime);
	}
	
	@Override
	protected SetStartTimeWorldUpdate clone() {
		return new SetStartTimeWorldUpdate(this);
	}
}

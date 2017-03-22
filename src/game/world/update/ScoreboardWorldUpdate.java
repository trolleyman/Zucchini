package game.world.update;

import game.world.Scoreboard;
import game.world.World;

public class ScoreboardWorldUpdate extends WorldUpdate {
	private final Scoreboard scoreboard;
	
	public ScoreboardWorldUpdate(Scoreboard scoreboard) {
		super();
		this.scoreboard = scoreboard;
	}
	
	@Override
	public void updateWorld(World w) {
		Scoreboard clone = scoreboard.clone();
		w.setScoreboard(scoreboard);
		System.out.println("Scoreboard Update: " + clone);
	}
}

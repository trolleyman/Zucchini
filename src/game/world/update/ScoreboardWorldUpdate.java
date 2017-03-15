package game.world.update;

import com.google.gson.GsonBuilder;
import game.net.codec.ObjectCodec;
import game.world.Scoreboard;
import game.world.World;

public class ScoreboardWorldUpdate extends WorldUpdate {
	private final Scoreboard scoreboard;
	
	public ScoreboardWorldUpdate(Scoreboard scoreboard) {
		super();
		this.scoreboard = scoreboard;
	}
	
	public ScoreboardWorldUpdate(ScoreboardWorldUpdate update) {
		super();
		this.scoreboard = update.scoreboard;
	}
	
	@Override
	public void updateWorld(World w) {
		w.setScoreboard(scoreboard);
		System.out.println("Scoreboard Update: " + scoreboard);
	}
	
	@Override
	protected WorldUpdate clone() {
		return new ScoreboardWorldUpdate(this);
	}
}

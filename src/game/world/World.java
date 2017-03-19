package game.world;

import game.Util;
import game.world.map.Map;

/**
 * The base class for the worlds (the common functionality is located here)
 * 
 * @author Callum
 */
public abstract class World {
	/** Number of seconds to process */
	private double dtPool = 0;
	
	/** Current map */
	protected Map map;
	
	/** Entity bank */
	protected EntityBank bank;
	
	/** Time until the world starts updating */
	protected float startTime;
	
	/** Time until the game goes back to the LobbyUI screen */
	protected float endTime;
	
	/** Is the game finishing? */
	private boolean finishing;
	
	/** The current scoreboard */
	protected Scoreboard scoreboard;
	
	/**
	 * Constructs the world
	 * @param _map The map
	 * @param _bank The list of entities in the world
	 */
	protected World(Map _map, EntityBank _bank) {
		this.map = _map;
		
		this.bank = _bank;
		
		this.startTime = Util.GAME_START_WAIT_SECS;
		this.endTime = 0.0f;
		
		this.scoreboard = new Scoreboard();
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Updates the world
	 * @param dt The number of seconds since the last update
	 */
	public synchronized void update(double dt) {
		dtPool += dt;
		while (dtPool > Util.DT_PER_UPDATE) {
			this.startTime -= Util.DT_PER_UPDATE;
			if (this.startTime < 0.0f)
				this.startTime = 0.0f;
			if (isFinishing())
				this.endTime += Util.DT_PER_UPDATE;
			if (isFinished())
				break;
			
			if (!isStarting() && !isFinishing())
				scoreboard.update(Util.DT_PER_UPDATE);
			updateStep(Util.DT_PER_UPDATE);
			dtPool -= Util.DT_PER_UPDATE;
		}
	}
	
	public void startFinishing() {
		this.finishing = true;
	}
	
	public boolean isFinishing() {
		return finishing;
	}
	
	public boolean isFinished() {
		return endTime >= Util.GAME_END_WAIT_SECS;
	}
	
	public boolean isStarting() {
		return startTime > 0.0f;
	}
	
	/**
	 * An update step. This is called with a constant dt ({@link Util#DT_PER_UPDATE})
	 * @param dt The number of seconds to update the world by
	 */
	protected abstract void updateStep(double dt);
	
	public void setStartTime(float startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(float endTime) {
		this.endTime = endTime;
	}
	
	public boolean isPaused() {
		return this.startTime != 0.0f;
	}
	
	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}
}


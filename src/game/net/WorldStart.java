package game.net;

import game.world.map.Map;

public class WorldStart {
	public Map map;
	public int playerId;
	
	public WorldStart(Map map, int playerId) {
		this.map = map;
		this.playerId = playerId;
	}
}

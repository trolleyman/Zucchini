package game.world;

import game.world.map.Map;

public class UpdateArgs {
	public double dt;
	
	public EntityBank bank;
	
	public Map map;
	
	public UpdateArgs(double _dt, EntityBank _bank, Map _map) {
		this.dt = _dt;
		this.bank = _bank;
		this.map = _map;
	}
}

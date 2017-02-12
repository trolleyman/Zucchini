package game.world;

import game.audio.ServerAudioManager;
import game.world.map.Map;

public class UpdateArgs {
	public double dt;
	
	public EntityBank bank;
	
	public Map map;
	
	public ServerAudioManager audio;
	
	public UpdateArgs(double _dt, EntityBank _bank, Map _map, ServerAudioManager _audio) {
		this.dt = _dt;
		this.bank = _bank;
		this.map = _map;
		this.audio = _audio;
	}
}

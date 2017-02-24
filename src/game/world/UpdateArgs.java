package game.world;

import game.audio.IAudioManager;
import game.audio.ServerAudioManager;
import game.world.map.Map;
import game.world.physics.PhysicsWorld;

public class UpdateArgs {
	public double dt;
	
	public EntityBank bank;
	
	public PhysicsWorld physics;
	
	public Map map;
	
	public IAudioManager audio;
	
	public UpdateArgs(double _dt, EntityBank _bank, PhysicsWorld _physics, Map _map, IAudioManager _audio) {
		this.dt = _dt;
		this.bank = _bank;
		this.physics = _physics;
		this.map = _map;
		this.audio = _audio;
	}
}

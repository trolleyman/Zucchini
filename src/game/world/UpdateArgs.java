package game.world;

import game.audio.IAudioManager;
import game.audio.ServerAudioManager;
import game.net.PacketCache;
import game.net.TCPConnection;
import game.net.UDPConnection;
import game.net.server.ClientHandler;
import game.world.map.Map;

public class UpdateArgs {
	public double dt;
	
	public EntityBank bank;
	
	public Map map;
	
	public IAudioManager audio;
	
	public PacketCache packetCache;
	
	public UpdateArgs(double _dt, EntityBank _bank, Map _map, IAudioManager _audio, PacketCache _packetCache) {
		this.dt = _dt;
		this.bank = _bank;
		this.map = _map;
		this.audio = _audio;
		this.packetCache = _packetCache;
	}
}

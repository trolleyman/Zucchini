package game.ai;

import game.world.World;
import game.world.entity.Player;

public abstract class PlayerController {
	private Player player;
	
	public PlayerController(Player _player) {
		this.player = _player;
	}
	public abstract void update(World w, double dt);
	
	public Player getPlayer() {
		return player;
	}
}

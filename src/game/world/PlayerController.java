package game.world;

public abstract class PlayerController {
	private Player p;
	
	public PlayerController(Player _p) {
		this.p = _p;
	}
	public abstract void update(double dt);
}

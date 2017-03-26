package game.world.entity.update;

import game.world.entity.Entity;
import game.world.entity.Player;

/**
 * Represents when a player has turned on/off their torch
 */
public class TorchLightUpdate extends EntityUpdate {
	
	/** True if the torch should be turned on. */
	private boolean on;
	
	public TorchLightUpdate(int id, boolean on) {
		super(id, true);
		this.on = on;
	}
	
	public TorchLightUpdate(TorchLightUpdate u) {
		super(u);
		this.on = u.on;
	}
	
	@Override
	public void updateEntity(Entity e) {
		if (e != null && e instanceof Player) {
			Player p = (Player)e;
			p.setTorchOn(on);
		}
	}
	
	public boolean isOn() {
		return on;
	}
	
	@Override
	public TorchLightUpdate clone() {
		return new TorchLightUpdate(this);
	}
}

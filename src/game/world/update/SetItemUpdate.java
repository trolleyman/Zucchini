package game.world.update;

import game.world.entity.Entity;
import game.world.entity.Player;

public class SetItemUpdate extends EntityUpdate {
	private int itemID;
	
	public SetItemUpdate(SetItemUpdate update) {
		super(update);
		
		this.itemID = update.itemID;
	}
	
	public SetItemUpdate(int id, int _itemID) {
		super(id);
		
		this.itemID = _itemID;
	}
	
	@Override
	public void updateEntity(Entity e) {
		if (e instanceof Player) {
			((Player) e).setItem(this.itemID);
			System.out.println("SetItemUpdate: " + this.itemID);
		} else {
			System.err.println("Warning: SetItemUpdate on non-player");
		}
	}
	
	@Override
	public SetItemUpdate clone() {
		return new SetItemUpdate(this);
	}
}

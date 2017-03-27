package game.world.entity.update;

import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.entity.Player;

public class HeldItemUpdate extends EntityUpdate {
	private Item item;
	
	public HeldItemUpdate(int id, Item _item) {
		super(id, true);
		if (_item != null)
			this.item = _item.clone();
	}
	
	@Override
	public void updateEntity(Entity e) {
		if (e != null && e instanceof Player) {
			if (item == null)
				((Player) e).setHeldItem(null);
			else
				((Player) e).setHeldItem(item.clone());
		}
	}
}

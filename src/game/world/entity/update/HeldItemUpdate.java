package game.world.entity.update;

import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.entity.Player;

public class HeldItemUpdate extends EntityUpdate {
	private Item item;
	
	public HeldItemUpdate(int id, Item item) {
		super(id, true);
		if (item != null)
			this.item = item;
	}
	
	@Override
	public void updateEntity(Entity e) {
		if (e != null && e instanceof Player) {
			if (item == null)
				((Player) e).setHeldItem(null);
			else
				((Player) e).setHeldItem(item);
		}
	}
}

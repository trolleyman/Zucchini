package game.world.entity.update;

import game.net.ObjectCodec;
import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.entity.Player;

public class SetHeldItem extends EntityUpdate {
	private Item item;
	
	public SetHeldItem(int id, Item _item) {
		super(id, true);
		if (_item != null)
			this.item = _item.clone();
	}
	
	public SetHeldItem(SetHeldItem shi) {
		super(shi);
		if (shi.item != null)
			this.item = shi.item.clone();
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
	
	@Override
	public SetHeldItem clone() {
		return new SetHeldItem(this);
	}
}

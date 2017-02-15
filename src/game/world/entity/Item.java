package game.world.entity;

import org.joml.Vector2f;

public abstract class Item extends Entity {
	public Item(Item i) {
		super(i);
	}
	
	public Item(Vector2f position) {
		super(position);
	}
}

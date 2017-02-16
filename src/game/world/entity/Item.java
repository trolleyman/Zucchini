package game.world.entity;

import org.joml.Vector2f;

public abstract class Item extends Entity {
	public Item(Item i) {
		super(i);
	}
	
	public Item(Vector2f position) {
		super(position);
	}
	
	/** Called when the user presses the mouse button */
	public abstract void beginUse();
	
	/** Called when the user releases the mouse button */
	public abstract void endUse();
	
	@Override
	public abstract Item clone();
}

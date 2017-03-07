package game.world.entity;

import game.world.Team;
import org.joml.Vector2f;

public abstract class Item extends Entity {
	public Item(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
	}
	
	public Item(Item i) {
		super(i);
	}
	
	/** Called when the user presses the mouse button */
	public abstract void beginUse();
	
	/** Called when the user releases the mouse button */
	public abstract void endUse();
	
	@Override
	public abstract Item clone();
}

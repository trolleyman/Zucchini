package game.world.entity;

import game.world.Team;
import org.joml.Vector2f;

public abstract class Item extends Entity {
	
	protected int ownerTeam;
	
	public Item(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
		this.ownerTeam = Team.PASSIVE_TEAM;
	}
	
	public Item(Item i) {
		super(i);
		this.ownerTeam = i.ownerTeam;
	}
	
	/**
	 * Sets the owner for this item.
	 */
	public void setOwnerTeam(int ownerTeam) {
		this.ownerTeam = ownerTeam;
	}
	
	/** Called when the user presses the mouse button */
	public abstract void beginUse();
	
	/** Called when the user releases the mouse button */
	public abstract void endUse();
	
	@Override
	public abstract Item clone();
}

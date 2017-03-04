package game.world.entity;

import game.render.IRenderer;
import game.world.Team;
import org.joml.Vector2f;

public abstract class Item extends Entity {
	
	protected int ownerTeam;
	protected int ownerId;
	
	public Item(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
		this.ownerTeam = Team.PASSIVE_TEAM;
		this.ownerId = Entity.INVALID_ID;
	}
	
	public Item(Item i) {
		super(i);
		this.ownerTeam = i.ownerTeam;
		this.ownerId = i.ownerId;
	}
	
	/**
	 * Sets the owner for this item.
	 */
	public void setOwnerTeam(int ownerTeam) {
		this.ownerTeam = ownerTeam;
	}
	
	/**
	 * Sets the owner for this item.
	 */
	public void setOwner(int ownerId) {
		this.ownerId = ownerId;
	}
	
	/** Called when the user presses the mouse button */
	public abstract void beginUse();
	
	/** Called when the user releases the mouse button */
	public abstract void endUse();
	
	/** Called to render information about the item to the screen */
	public abstract void renderUI(IRenderer r);
	
	@Override
	public abstract Item clone();
}

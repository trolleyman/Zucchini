package game.world.entity;

import com.google.gson.annotations.SerializedName;
import game.render.IRenderer;
import game.world.Team;
import game.world.entity.damage.DamageSource;
import org.joml.Vector2f;

public abstract class Item extends Entity {
	protected DamageSource owner;
	
	public Item(Vector2f position) {
		super(Team.PASSIVE_TEAM, position);
		owner = new DamageSource();
	}
	
	public Item(Item i) {
		super(i);
		owner = i.owner.clone();
	}
	
	public boolean isHeld() {
		return owner.entityId != Entity.INVALID_ID;
	}
	
	/**
	 * Sets the owner for this item. Entity can be null, in which case the source will be reset to Unknown.
	 */
	public void setOwner(Entity e) {
		if (e == null) {
			if (owner.entityId != Entity.INVALID_ID)
				owner = new DamageSource();
		} else if (owner.entityId != e.getId())
			owner = new DamageSource(e);
	}
	
	/** Called when the user presses the mouse button */
	public abstract void beginUse();
	
	/** Returns true if the item is currently being used (for weapons, if they are firing) */
	public abstract boolean isUsing();
	
	/** Called when the user releases the mouse button */
	public abstract void endUse();
	
	public abstract float aiValue();
	
	/** Called to render information about the item to the screen */
	public abstract void renderUI(IRenderer r);
	
	@Override
	public abstract Item clone();

	@Override
	public abstract String toString();
}

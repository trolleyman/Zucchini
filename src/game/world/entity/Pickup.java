package game.world.entity;

import game.render.IRenderer;
import game.world.EntityBank;
import game.world.UpdateArgs;
import org.joml.Vector2f;

/**
 * A pickup is an item found on the floor.
 */
public class Pickup extends Entity {
	private Item item;
	private transient double time = 0.0f;
	
	public Pickup(Pickup p) {
		super(p);
	}
	
	public Pickup(Vector2f position, Item _item) {
		super(position);
		this.item = _item;
	}
	
	public Item pickup(EntityBank bank) {
		bank.removeEntityCached(this.getId());
		bank.updateEntityCached(item);
		return item;
	}
	
	@Override
	public void update(UpdateArgs ua) {}
	
	@Override
	public void clientUpdate(double dt) {
		super.clientUpdate(dt);
		this.time += dt;
	}
	
	@Override
	public void render(IRenderer r) {
		// Render the contained item
		this.item.position
				.set(this.position)
				.add(0.0f, (float) Math.sin(time) * 0.2f);
		this.item.render(r);
	}
	
	@Override
	public Pickup clone() {
		return new Pickup(this);
	}
}
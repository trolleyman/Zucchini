package game.world.entity;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.light.PointLight;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * A pickup is an item found on the floor.
 */
public class Pickup extends Entity {
	private Item item;
	private transient double time = 0.0f;
	
	private transient PointLight light;
	
	public Pickup(Vector2f position, Item _item) {
		super(Team.PASSIVE_TEAM, position);
		this.item = _item;
		if (this.item != null) {
			this.item.setOwner(null);
			this.item.endUse();
		}
	}
	
	public Item getItem() {
		return item;
	}
	
	private void generateLight() {
		this.light = new PointLight(position, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), 1.1f, false);
	}
	
	private void setParams() {
		this.item.position
				.set(this.position)
				.add(0.0f, (float) Math.sin(time * 4.0f) * 0.1f);
		if (light == null)
			generateLight();
		this.light.position.set(position);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		setParams();
		this.item.update(ua);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		this.time += ua.dt;
		setParams();
		this.item.clientUpdate(ua);
		this.light.clientUpdate(ua);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		setParams();
		this.light.color.w = 0.6f;
		this.light.renderLight(r, map);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		// Render the contained item
		setParams();
		this.item.render(r, map);
		this.light.color.w = 0.1f;
		this.light.render(r, map);
	}
}

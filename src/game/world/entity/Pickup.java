package game.world.entity;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * A pickup is an item found on the floor.
 */
public class Pickup extends Entity {
	private Item item;
	private transient double time = 0.0f;
	
	private PointLight light;
	
	public Pickup(Vector2f position, Item _item) {
		super(Team.PASSIVE_TEAM, position);
		this.item = _item;
		if (this.item != null) {
			this.item.setOwner(Entity.INVALID_ID);
			this.item.setOwnerTeam(Team.INVALID_TEAM);
		}
	}
	
	public Pickup(Pickup p) {
		super(p);
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
		this.light.position.set(position);
	}
	
	@Override
	public void update(UpdateArgs ua) {}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		this.time += ua.dt;
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		// Render the contained item
		if (light == null)
			generateLight();
		setParams();
		this.light.render(r, map);
		this.item.render(r, map);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		super.renderLight(r, map);
		
		if (light == null)
			generateLight();
		setParams();
		this.light.renderLight(r, map);
		this.item.renderLight(r, map);
	}
	
	@Override
	public Pickup clone() {
		return new Pickup(this);
	}
}

package game.world.entity;

import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageSource;
import game.world.entity.damage.DamageType;
import game.world.entity.light.PointLight;
import game.world.entity.update.DamageUpdate;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Explosion extends Entity {
	private static final Vector4f EXPLOSION_COLOR = new Vector4f(1.0f, 0.53f, 0.17f, 0.0f);
	
	/** The damage suffered at distance 1. The maxDamage at distance is determined by the inverse square law */
	private transient float maxDamage;
	
	/** The radius of the explosion */
	private transient float radius;
	
	/** The entity that caused this explosion */
	private DamageSource source;
	
	private PointLight light;
	
	private float startAttenuationFactor;
	private float endAttenuationFactor;
	
	public Explosion(Vector2f pos, DamageSource source, float _maxDamage, float _radius) {
		super(Team.PASSIVE_TEAM, pos);
		this.maxDamage = _maxDamage;
		this.radius = _radius;
		this.source = source;
		
		constructLight();
	}
	
	private void constructLight() {
		this.startAttenuationFactor = LightUtil.getAttenuationFactor(radius, 0.1f);
		this.endAttenuationFactor = LightUtil.getAttenuationFactor(radius, 0.8f);
		this.light = new PointLight(position, EXPLOSION_COLOR, startAttenuationFactor, false);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		this.addHealth(-(float) ua.dt);
	}
	
	@Override
	public void update(UpdateArgs ua) {
		if (this.getHealth() >= this.getMaxHealth()) {
			// Damage nearby entities
			ArrayList<Entity> entities = ua.bank.getEntitiesNear(this.position.x, this.position.y, this.radius);
			for (Entity e : entities) {
				if (e.getTeam() != Team.PASSIVE_TEAM) {
					float d2 = e.position.distanceSquared(this.position);
					float fdamage = maxDamage / Math.min(maxDamage, d2);
					Damage damage = new Damage(source, DamageType.EXPLOSION_DAMAGE, fdamage);
					ua.bank.updateEntityCached(new DamageUpdate(e.getId(), damage));
				}
			}
		}
		this.addHealth(-(float) ua.dt);
	}
	
	private void setLightParams() {
		float p = (1 - getHealth() / getMaxHealth());
		this.light.attenuationFactor = p * (startAttenuationFactor - endAttenuationFactor) + startAttenuationFactor;
		this.light.color.w = (1 - p) * 2.0f - 1.0f;
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		//r.drawCircle(position.x, position.y, radius, ColorUtil.GREEN);
		
		if (this.light == null)
			constructLight();
		setLightParams();
		this.light.render(r, map);
	}
	
	@Override
	public void renderLight(IRenderer r, Map map) {
		super.renderLight(r, map);
		
		if (this.light == null)
			constructLight();
		setLightParams();
		this.light.renderLight(r, map);
	}
	
	@Override
	public float getMaxHealth() {
		return 1.0f;
	}
}

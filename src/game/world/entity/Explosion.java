package game.world.entity;

import game.ColorUtil;
import game.Util;
import game.render.IRenderer;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.damage.Damage;
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
	
	/** The player that caused this explosion */
	private int fromId;
	/** The team that caused this explostion */
	private int fromTeam;
	
	private PointLight light;
	
	private float startAttenuationFactor;
	private float endAttenuationFactor;
	
	public Explosion(Vector2f pos, int fromId, int fromTeam, float _damage, float _radius) {
		super(Team.PASSIVE_TEAM, pos);
		this.maxDamage = _damage;
		this.radius = _radius;
		this.fromId = fromId;
		this.fromTeam = fromTeam;
		
		constructLight();
	}
	
	public Explosion(Explosion e) {
		super(e);
		this.maxDamage = e.maxDamage;
		this.radius = e.radius;
		this.fromId = e.fromId;
		this.fromTeam = e.fromTeam;
		
		this.light = e.light;
	}
	
	private void constructLight() {
		this.startAttenuationFactor = LightUtil.getAttenuationFactor(radius, 0.1f);
		this.endAttenuationFactor = LightUtil.getAttenuationFactor(radius, 0.8f);
		this.light = new PointLight(position, EXPLOSION_COLOR, startAttenuationFactor, false);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
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
					Damage damage = new Damage(fromId, fromTeam, DamageType.EXPLOSION_DAMAGE, fdamage);
					ua.bank.updateEntityCached(new DamageUpdate(e.getId(), damage));
				}
			}
		}
		Damage damage = new Damage(Entity.INVALID_ID, Team.INVALID_TEAM, DamageType.UNKNOWN_DAMAGE, (float)ua.dt);
		ua.bank.updateEntityCached(new DamageUpdate(this.getId(), damage));
	}
	
	private void setLightParams() {
		float p = (1 - getHealth() / getMaxHealth());
		this.light.attenuationFactor = p * (startAttenuationFactor - endAttenuationFactor) + startAttenuationFactor;
		this.light.color.w = (1-p) * 1.2f;
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
	protected float getMaxHealth() {
		return 0.7f;
	}
	
	@Override
	public Explosion clone() {
		return new Explosion(this);
	}
}

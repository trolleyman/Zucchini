package game.world.entity.weapon;

import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.update.HealthUpdate;
import game.world.entity.update.SetHeldItem;
import org.joml.Vector2f;

import java.util.ArrayList;

public class Knife extends Weapon {
	private static final float COOLDOWN_TIME = 0.2f;
	private static final float KNIFE_RANGE = 0.8f;
	private static final float KNIFE_FOV = (float)Math.toRadians(140);
	
	private static final float STAB_ANIMATION_TIME = 0.1f;
	private static final float STAB_ANIMATION_LENGTH = 0.25f;
	
	private float stabTime;
	
	private boolean stabbed;
	
	public Knife(Knife k) {
		super(k);
		this.stabTime = k.stabTime;
		this.stabbed = k.stabbed;
	}
	
	public Knife(Vector2f position) {
		super(position, -1, true, COOLDOWN_TIME, 1, COOLDOWN_TIME);
		this.stabTime = 0.0f;
		this.stabbed = false;
	}
	
	@Override
	public void render(IRenderer r) {
		float mag = 0.0f;
		if (this.stabbed) {
			mag = STAB_ANIMATION_LENGTH * (float)Math.sin((this.stabTime / STAB_ANIMATION_TIME) * Math.PI);
		}
		
		float x = position.x + mag * Util.getDirX(angle);
		float y = position.y + mag * Util.getDirY(angle);
		
		Texture t = r.getTextureBank().getTexture("knife.png");
		float ratio = t.getHeight() / (float)t.getWidth();
		float w = 0.08f;
		r.drawTexture(t, Align.BM, x, y, w, w * ratio, angle);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		
		if (this.stabbed) {
			this.stabTime += ua.dt;
			if (this.stabTime > STAB_ANIMATION_TIME) {
				this.stabTime = 0.0f;
				this.stabbed = false;
			}
		}
	}
	
	@Override
	public void update(UpdateArgs ua) {
		if (this.stabbed) {
			this.stabTime += ua.dt;
			if (this.stabTime > STAB_ANIMATION_TIME) {
				this.stabTime = 0.0f;
				this.stabbed = false;
			}
		}
		
		super.update(ua);
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		// Reload
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		ArrayList<Entity> es = ua.bank.getEntitiesNear(position.x, position.y, KNIFE_RANGE);
		Entity closest = null;
		float closestDistanceSq = 0.0f;
		for (Entity e : es) {
			if (!Team.isHostileTeam(this.ownerTeam, e.getTeam()))
				continue;
			
			float angleTo = Util.getAngle(position.x, position.y, e.position.x, e.position.y);
			if (Util.getAngleDiff(angleTo, angle) < KNIFE_FOV / 2)
				continue;
			
			// Is in fov - get closest
			if (closest == null) {
				closest = e;
				closestDistanceSq = closest.position.distanceSquared(this.position);
			} else {
				float eDistanceSq = e.position.distanceSquared(this.position);
				if (eDistanceSq < closestDistanceSq) {
					closest = e;
					closestDistanceSq = eDistanceSq;
				}
			}
		}
		
		if (closest != null) {
			System.out.println("Weapon: Knifed " + closest.getId() + " (" + closest + ")");
			ua.bank.updateEntityCached(new HealthUpdate(closest.getId(), -5.0f));
		}
		this.stabbed = true;
		ua.bank.updateEntityCached(new SetHeldItem(this.ownerId, this.clone()));
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		Texture t = r.getTextureBank().getTexture("knifeBullet.png");
		r.drawTextureUV(t, Align.BR, x, y, t.getWidth(), t.getHeight()*p,
				0.0f, 1-p, 1.0f, 1.0f);
		
		x -= t.getWidth();
		x -= 10.0f;
		return x;
	}
	
	@Override
	public Knife clone() {
		return new Knife(this);
	}
}

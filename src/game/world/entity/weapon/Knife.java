package game.world.entity.weapon;

import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.Team;
import game.world.UpdateArgs;
import game.world.entity.Entity;
import game.world.entity.damage.Damage;
import game.world.entity.damage.DamageType;
import game.world.entity.update.DamageUpdate;
import game.world.entity.update.HeldItemUpdate;
import game.world.map.Map;
import org.joml.Vector2f;

import java.util.ArrayList;

public class Knife extends Weapon {
	private static final float COOLDOWN_TIME = 0.2f;
	private static final float KNIFE_RADIUS = 0.4f;
	private static final float KNIFE_OFFSET = 0.4f;
	
	private static final float STAB_ANIMATION_TIME = 0.1f;
	private static final float STAB_ANIMATION_LENGTH = 0.25f;
	
	private float stabTime;
	
	private boolean stabbed;
	
	public Knife(Vector2f position) {
		super(position, -1, true, COOLDOWN_TIME, 1, COOLDOWN_TIME);
		this.stabTime = 0.0f;
		this.stabbed = false;
	}
	
	private float getDamageCentreX() {
		return position.x + Util.getDirX(angle) * KNIFE_OFFSET;
	}
	
	private float getDamageCentreY() {
		return position.y + Util.getDirY(angle) * KNIFE_OFFSET;
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		float mag = 0.0f;
		if (this.stabbed) {
			mag = STAB_ANIMATION_LENGTH * (float) Math.sin((this.stabTime / STAB_ANIMATION_TIME) * Math.PI);
		}
		
		float x = position.x + mag * Util.getDirX(angle);
		float y = position.y + mag * Util.getDirY(angle);
		
		Texture t = r.getTextureBank().getTexture("knife.png");
		float ratio = t.getHeight() / (float) t.getWidth();
		float w = 0.08f;
		r.drawTexture(t, Align.BM, x, y, w, w * ratio, angle);
		
		//r.drawCircle(getDamageCentreX(), getDamageCentreY(), KNIFE_RADIUS, new Vector4f(1.0f, 0.0f, 0.0f, 0.3f));
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		if (this.stabbed) {
			this.stabTime += ua.dt;
			if (this.stabTime > STAB_ANIMATION_TIME) {
				this.stabTime = 0.0f;
				this.stabbed = false;
			}
		}
		super.clientUpdate(ua);
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
	protected void fire(UpdateArgs ua, float angle) {
		ArrayList<Entity> es = ua.bank.getEntitiesNear(getDamageCentreX(), getDamageCentreY(), KNIFE_RADIUS);
		for (Entity e : es) {
			if (!Team.isHostileTeam(owner.teamId, e.getTeam()))
				continue;
			
			// Add damage
			System.out.println("[Game]: Weapon: Knifed " + e.getReadableName());
			Damage d = new Damage(owner, DamageType.KNIFE_DAMAGE, 5.0f);
			ua.bank.updateEntityCached(new DamageUpdate(e.getId(), d));
		}
		
		ua.audio.play("slash.wav", 1f, this.position);
		
		this.stabbed = true;
		ua.bank.updateEntityCached(new HeldItemUpdate(owner.entityId, this));
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		Texture t = r.getTextureBank().getTexture("knifeBullet.png");
		r.drawTextureUV(t, Align.BR, x, y, t.getWidth(), t.getHeight() * p,
				0.0f, 1 - p, 1.0f, 1.0f);
		
		x -= t.getWidth();
		x -= 10.0f;
		return x;
	}
	
	@Override
	public String toString() {
		return "Knife";
	}
	
	@Override
	public float aiValue() {
		return 0;
	}
	
	@Override
	public boolean isUseless() {
		return true;
	}
}

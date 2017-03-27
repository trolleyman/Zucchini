package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import game.world.map.Map;
import org.joml.Vector2f;

public class SilencedPistol extends Weapon {
	private transient int reloadSoundID = -1;
	
	public SilencedPistol(Vector2f position, int ammo) {
		super(position, ammo, true, 0.3f, 7, 2.0f,
				(float) Math.toRadians(0.1f), (float) Math.toRadians(1.0f), (float) Math.toRadians(0.5f), (float) Math.toRadians(0.2f));
	}
	
	@Override
	protected void fire(UpdateArgs ua, float fangle) {
		// Calculate position
		Vector2f bulletPos = new Vector2f(Util.getDirX(angle), Util.getDirY(angle)).mul(getHeight()).add(this.position);
		
		// Play audio
		ua.audio.play("bullet_whizz_silent.wav", 0.5f, new Vector2f(bulletPos));
		
		// Add bullets to entity bank
		// TODO: ua.bank.addEntityCached(new GunshotEffect(new Vector2f(bulletPos)));
		ua.bank.addEntityCached(new HandgunBullet(bulletPos, owner, fangle));
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID == -1) {
			System.out.println("Reloading silenced pistol...");
			this.reloadSoundID = ua.audio.play("gun_reload[2sec].wav", 0.6f, this.position);
		} else {
			ua.audio.updateSourcePos(this.reloadSoundID, this.position);
		}
	}
	
	@Override
	protected void endReload(UpdateArgs ua) {
		this.reloadSoundID = -1;
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		Align a = isHeld() ? Align.BM : Align.MM;
		Texture tex = r.getTextureBank().getTexture("Weapon_SilencedPistol.png");
		float ratio = getHeight() / tex.getHeight();
		r.drawTexture(tex, a, position.x, position.y, ratio * tex.getWidth(), getHeight(), this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.WHITE);
		
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	private float getHeight() {
		return isHeld() ? 0.35f : 0.4f;
	}
	
	@Override
	public String toString() {
		return "Silenced Pistol";
	}
	
	@Override
	public boolean isUseless() {
		return this.ammo < 2;
	}
	
	@Override
	public float aiValue() {
		return 1;
	}
}

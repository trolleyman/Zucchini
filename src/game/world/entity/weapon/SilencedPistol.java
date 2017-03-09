package game.world.entity.weapon;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;

public class SilencedPistol extends Weapon {
	private int reloadSoundID = -1;
	
	public SilencedPistol(SilencedPistol g) {
		super(g);
	}
	
	public SilencedPistol(Vector2f position) {
		super(position, true, 0.1f, 7, 2.0f);
	}

	@Override
	protected void fire(UpdateArgs ua) {
		ua.audio.play("bullet_whizz_silent.wav", 0.1f, this.position);
		// Add bullets to entity bank
		ua.bank.addEntityCached(new HandgunBullet(new Vector2f(position), this.ownerTeam, angle));
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID == -1) {
			System.out.println("Reloading silenced pistol...");
			this.reloadSoundID = ua.audio.play("gun_reload[2sec].wav", 0.1f, this.position);
		}else{
			ua.audio.updateSourcePos(this.reloadSoundID, this.position);
		}
	}
	
	@Override
	protected void endReload(UpdateArgs ua) {
		this.reloadSoundID = -1;
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.2f, 0.2f, ColorUtil.BLACK, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.WHITE);
		
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	public SilencedPistol clone() {
		return new SilencedPistol(this);
	}
}
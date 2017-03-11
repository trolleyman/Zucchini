package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;
import org.joml.Vector2f;

public class PumpActionShotgun extends Weapon {
	private static final int SHOTS_PER_SHELL = 8;
	private static final float SPREAD = (float)Math.toRadians(8.0f);
	private int reloadSoundID = -1;
	
	public PumpActionShotgun(PumpActionShotgun s) {
		super(s);
	}
	
	public PumpActionShotgun(Vector2f position, int ammo) {
		super(position, ammo, true, 0.8f, 8, 4.0f, (float)Math.toRadians(1.0f));
	}
	
	@Override
	public void render(IRenderer r) {
		Align a = isHeld() ? Align.BM : Align.MM;
		r.drawBox(a, position.x, position.y, 0.1f, getHeight(), ColorUtil.YELLOW, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.WHITE);
		
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		Vector2f muzzlePos = new Vector2f().set(Util.getDirX(angle), Util.getDirY(angle)).mul(getHeight()).add(this.position);;
		
		// Fire series of bullets
		for (int i = 0; i < SHOTS_PER_SHELL; i++) {
			float fang = angle + ((float)Math.random() * 2 - 1.0f) * SPREAD;
			fang = Util.normalizeAngle(fang);
			ua.bank.addEntityCached(new PumpActionShotgunBullet(new Vector2f(muzzlePos), this.ownerId, this.ownerTeam, fang));
		}
		ua.audio.play("pump-shotgun-shot.wav",1.0f, this.position);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID  == -1) {
			// System.out.println("Reloading pump action shotgun...");
			this.reloadSoundID = ua.audio.play("pump-shotgun-reload[4sec].wav", 0.6f, this.position);
		}else{
			ua.audio.updateSourcePos(this.reloadSoundID, this.position);
		}
	}
	
	@Override
	protected void endReload(UpdateArgs ua) {
		this.reloadSoundID = -1;
	}
	
	private float getHeight() {
		return 0.45f;
	}
	
	@Override
	public PumpActionShotgun clone() {
		return new PumpActionShotgun(this);
	}
}

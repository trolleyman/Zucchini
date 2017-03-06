package game.world.entity.weapon;

import game.Util;
import game.render.Texture;
import game.world.UpdateArgs;
import org.joml.Vector2f;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;

public class Handgun extends Weapon {
	public Handgun(Handgun g) {
		super(g);
	}
	
	public Handgun(Vector2f position, int ammo) {
		super(position, ammo, true, 0.1f, 8, 2.0f,
				(float)Math.toRadians(0.1f), (float)Math.toRadians(1.0f), (float)Math.toRadians(0.5f), (float)Math.toRadians(0.2f));
	}
	
	@Override
	protected void fire(UpdateArgs ua, float fangle) {
		// Calculate position
		Vector2f bulletPos = new Vector2f(Util.getDirX(angle), Util.getDirY(angle)).mul(getHeight()).add(this.position);
		
		// Play audio
		ua.audio.play("handgunshot.wav", 0.5f, new Vector2f(bulletPos));
		
		// Add bullets to entity bank
		// TODO: ua.bank.addEntityCached(new GunshotEffect(new Vector2f(bulletPos)));
		ua.bank.addEntityCached(new HandgunBullet(bulletPos, this.ownerTeam, fangle));
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		ua.audio.play("gun_reload[2sec].wav", 1.0f,this.position);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.BM, position.x, position.y, 0.2f, getHeight(), ColorUtil.PINK, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.WHITE);
		
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	private float getHeight() {
		return 0.2f;
	}
	
	@Override
	public Handgun clone() {
		return new Handgun(this);
	}
}

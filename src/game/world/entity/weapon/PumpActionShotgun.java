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
	
	public PumpActionShotgun(PumpActionShotgun s) {
		super(s);
	}
	
	public PumpActionShotgun(Vector2f position) {
		super(position, true, 0.3f, 8, 4.0f, (float)Math.toRadians(1.0f));
	}
	
	@Override
	public void render(IRenderer r) {
		// TODO: Render shotgun texture
		r.drawBox(Align.MM, position.x, position.y, 0.1f, 0.45f, ColorUtil.YELLOW, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.WHITE);
		
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		// TODO: Play shotgun reload sound
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		// Fire series of bullets
		for (int i = 0; i < SHOTS_PER_SHELL; i++) {
			float fang = angle + ((float)Math.random() * 2 - 1.0f) * SPREAD;
			fang = Util.normalizeAngle(fang);
			ua.bank.addEntityCached(new PumpActionShotgunBullet(position, this.ownerTeam, fang));
		}
		
		// TODO: Play shotgun fire sound
	}
	
	@Override
	public PumpActionShotgun clone() {
		return new PumpActionShotgun(this);
	}
}

package game.world.entity.weapon;

import org.joml.Vector2f;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;

public class LaserGun extends Weapon{

	public LaserGun(LaserGun g){
		super(g);
	}

	public LaserGun(Vector2f position) {
		super(position, true, 0.5f, 1, 0.5f);
	}

	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.RED);
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}

	@Override
	protected void fire(UpdateArgs ua) {
		ua.bank.addEntityCached(new HandgunBullet(new Vector2f(position), this.ownerTeam, angle));
		ua.audio.play("lasergun-fire.wav", 1.0f, this.position);
	}

	@Override
	protected void startReload(UpdateArgs ua) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void endReload(UpdateArgs ua) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LaserGun clone() {
		return new LaserGun(this);
	}

	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.15f, 0.35f, ColorUtil.RED, this.angle);
	}
	

}

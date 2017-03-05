package game.world.entity.weapon;

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
	
	public Handgun(Vector2f position) {
		super(position, true, 0.1f, 8, 2.0f,
				(float)Math.toRadians(0.1f), (float)Math.toRadians(1.0f), (float)Math.toRadians(0.5f), (float)Math.toRadians(0.2f));
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		ua.audio.play("handgunshot.wav", 0.5f,this.position);
		System.out.println("BANG!");
		// Add bullets to entity bank
		ua.bank.addEntityCached(new HandgunBullet(new Vector2f(position), this.ownerTeam, angle));
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		ua.audio.play("gun_reload[2sec].wav", 1.0f,this.position);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.2f, 0.2f, ColorUtil.PINK, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 20.0f, 40.0f * p, ColorUtil.WHITE);
		
		x -= 20.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	public Handgun clone() {
		return new Handgun(this);
	}
}

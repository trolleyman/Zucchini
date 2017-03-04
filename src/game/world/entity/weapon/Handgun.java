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
		super(position, true, 0.1f, 8, 2.0f);
	}

	@Override
	protected void fire(UpdateArgs ua) {
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
	protected float renderBullet(IRenderer r, float x, float y) {
		Texture t = r.getTextureBank().getTexture("handgunBullet.png");
		r.drawTexture(t, Align.BR, x, y);
		
		x -= t.getWidth();
		x -= 10.0f;
		return x;
	}
	
	@Override
	public Handgun clone() {
		return new Handgun(this);
	}
}

package game.world.entity;

import java.util.ArrayList;

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
		ua.audio.play("handgunshot.wav", 0.9f);
		System.out.println("BANG!");
		// Add bullets to entity bank
		ua.bank.updateEntityCached(new HandgunBullet(new Vector2f(position), angle));
	}

	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.2f, 0.2f, ColorUtil.PINK);
	}

	@Override
	public Handgun clone() {
		return new Handgun(this);
	}
}

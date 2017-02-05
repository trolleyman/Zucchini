package game.world.entity;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;

public class Handgun extends SemiAutoWeapon {
	public Handgun(Handgun g) {
		super(g);
	}
	
	public Handgun(Vector2f position) {
		super(position, 0.1f, 8, 2.0f);
	}

	@Override
	protected Entity fire() {
		return new HandgunBullet(position, angle);
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

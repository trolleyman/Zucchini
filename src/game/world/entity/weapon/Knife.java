package game.world.entity.weapon;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import game.world.entity.Item;
import org.joml.Vector2f;

public class Knife extends Weapon {
	private static final float COOLDOWN_TIME = 0.3f;
	
	public Knife(Knife k) {
		super(k);
	}
	
	public Knife(Vector2f position) {
		super(position, -1, true, COOLDOWN_TIME, 1, COOLDOWN_TIME);
	}
	
	@Override
	public void render(IRenderer r) {
		Texture t = r.getTextureBank().getTexture("knife.png");
		float ratio = t.getHeight() / (float)t.getWidth();
		float w = 0.08f;
		r.drawTexture(t, Align.BM, position.x, position.y, w, w * ratio, angle);
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		// Reload
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		// TODO
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		Texture t = r.getTextureBank().getTexture("knifeBullet.png");
		r.drawTextureUV(t, Align.BR, x, y, t.getWidth(), t.getHeight()*p,
				0.0f, 1-p, 1.0f, 1.0f);
		
		x -= t.getWidth();
		x -= 10.0f;
		return x;
	}
	
	@Override
	public Knife clone() {
		return new Knife(this);
	}
}

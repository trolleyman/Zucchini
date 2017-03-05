package game.world.entity.weapon;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class RocketLauncher extends Weapon {
	private static final Vector4f COLOR = new Vector4f(0.0f, 0.4f, 0.0f, 1.0f);
	
	private int reloadSoundID = -1;
	
	public RocketLauncher(RocketLauncher rl) {
		super(rl);
	}
	
	public RocketLauncher(Vector2f position) {
		super(position, true, 0.0f, 1, 5.0f);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.1f, 0.5f, COLOR, this.angle);
	}
	
	@Override
	protected void fire(UpdateArgs ua) {
		System.out.println("Whoosh! Rocket fired!");
		ua.audio.play("rocket-launcher.wav", 0.5f, this.position);
		ua.bank.addEntityCached(new Rocket(position, this.ownerTeam, this.angle));
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID == -1) {
			System.out.println("Reloading rocket launcher...");
			this.reloadSoundID = ua.audio.play("rocket_reload[5sec].wav", 1.0f, this.position);
		}else{
			ua.audio.updateSourcePos(this.reloadSoundID, this.position);
		}
	}
	
	@Override
	protected void endReload(UpdateArgs ua) {
		this.reloadSoundID = -1;
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 25.0f, 120.0f * p, ColorUtil.WHITE);
		
		x -= 25.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	public RocketLauncher clone() {
		return new RocketLauncher(this);
	}
}

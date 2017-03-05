package game.world.entity.weapon;

import game.render.Texture;
import game.world.UpdateArgs;
import org.joml.Vector2f;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;

public class Handgun extends Weapon {
	private int reloadSoundID = -1;
	
	public Handgun(Handgun g) {
		super(g);
	}
	
	public Handgun(Vector2f position) {
		super(position, true, 0.1f, 8, 2.0f);
	}

	@Override
	protected void fire(UpdateArgs ua) {
		ua.audio.play("handgunshot.wav", 0.5f,this.position);
		// Add bullets to entity bank
		ua.bank.addEntityCached(new HandgunBullet(new Vector2f(position), this.ownerTeam, angle));
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID == -1) {
			System.out.println("Reloading handgun...");
			this.reloadSoundID = ua.audio.play("gun_reload[2sec].wav", 0.6f, this.position);
		}else{
			ua.audio.updateSourcePos(this.reloadSoundID, this.position);
		}
	}
	
	@Override
	protected void endReload(UpdateArgs ua) {
		this.reloadSoundID = -1;
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

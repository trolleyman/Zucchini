package game.world.entity.weapon;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import org.joml.Vector2f;

public class MachineGun extends Weapon {
	public MachineGun(MachineGun g) {
	super(g);
}
	
	public MachineGun(Vector2f position) {
		super(position, false, 0.05f, 30, 2.0f);
	}
	
	@Override
	protected void fire(UpdateArgs ua) {
		ua.audio.play("handgunshot.wav", 0.5f,this.position);
		System.out.println("BANG!");
		// Add bullets to entity bank
		ua.bank.addEntityCached(new MachineGunBullet(new Vector2f(position), this.ownerTeam, angle));
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		ua.audio.play("gun_reload[2sec].wav", 1.0f,this.position);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.2f, 0.2f, ColorUtil.CYAN, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y) {
		Texture t = r.getTextureBank().getTexture("machineGunBullet.png");
		r.drawTexture(t, Align.BR, x, y);
		
		x -= t.getWidth();
		x -= 5.0f;
		return x;
	}
	
	@Override
	public MachineGun clone() {
		return new MachineGun(this);
	}
}

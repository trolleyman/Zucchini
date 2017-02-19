package game.world.entity;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
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
		ua.audio.play("handgunshot.wav", 1.0f);
		System.out.println("BANG!");
		// Add bullets to entity bank
		ua.bank.addEntityCached(new MachineGunBullet(new Vector2f(position), this.ownerTeam, angle));
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		ua.audio.play("gun_reload[2sec].wav", 1.0f);
	}
	
	@Override
	public void render(IRenderer r) {
		r.drawBox(Align.MM, position.x, position.y, 0.2f, 0.2f, ColorUtil.CYAN);
	}
	
	@Override
	public MachineGun clone() {
		return new MachineGun(this);
	}
}

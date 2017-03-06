package game.world.entity.weapon;

import game.ColorUtil;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.Player;
import org.joml.Vector2f;

public class MachineGun extends Weapon {
	/** Where the line of sight intersects with the map */
	private transient Vector2f lineOfSightIntersecton = new Vector2f();
	
	public MachineGun(MachineGun g) {
	super(g);
}
	
	public MachineGun(Vector2f position, int ammo) {
		super(position, ammo, false, 0.05f, 30, 2.0f,
				(float)Math.toRadians(0.5f), (float)Math.toRadians(5.0f), (float)Math.toRadians(0.2f), (float)Math.toRadians(1.0f));
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		ua.audio.play("handgunshot.wav", 0.5f, this.position);
		// Add bullets to entity bank
		ua.bank.addEntityCached(new MachineGunBullet(new Vector2f(position), this.ownerTeam, angle));
	}
	
	@Override
	protected void reload(UpdateArgs ua) {
		ua.audio.play("gun_reload[2sec].wav", 1.0f,this.position);
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		
		float x = position.x + Player.LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
		float y = position.y + Player.LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
		
		if (ua.map.intersectsLine(position.x, position.y, x, y, lineOfSightIntersecton) == null)
			lineOfSightIntersecton.set(x, y);
	}
	
	@Override
	public void render(IRenderer r) {
		if (lineOfSightIntersecton == null) {
			lineOfSightIntersecton = new Vector2f();
			float x = position.x + Player.LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
			float y = position.y + Player.LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
			lineOfSightIntersecton.set(x, y);
		}
		// Draw laser sight
		r.drawLine(position.x, position.y, lineOfSightIntersecton.x, lineOfSightIntersecton.y, ColorUtil.RED, 1.0f);
		
		// Draw weapon
		r.drawBox(Align.MM, position.x, position.y, 0.2f, 0.2f, ColorUtil.CYAN, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 10.0f, 70.0f * p, ColorUtil.WHITE);
		
		x -= 10.0f;
		x -= 10.0f;
		return x;
	}
	
	@Override
	public MachineGun clone() {
		return new MachineGun(this);
	}
}

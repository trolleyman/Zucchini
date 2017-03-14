package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.Player;
import game.world.map.Map;
import org.joml.Vector2f;

public class MachineGun extends Weapon {
	private transient int reloadSoundID = -1;
	
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
	protected void fire(UpdateArgs ua, float fangle) {
		// Calculate bullet position
		Vector2f muzzlePos = new Vector2f();
		getMuzzlePos(muzzlePos);
		
		// Play audio
		ua.audio.play("handgunshot.wav", 0.5f, new Vector2f(muzzlePos));
		
		// Add bullets to entity bank
		ua.bank.addEntityCached(new MachineGunBullet(muzzlePos, this.ownerId, this.ownerTeam, fangle));
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID == -1) {
			System.out.println("Reloading machine gun...");
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
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		
		float x = position.x + Player.LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
		float y = position.y + Player.LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
		
		if (ua.map.intersectsLine(position.x, position.y, x, y, lineOfSightIntersecton) == null)
			lineOfSightIntersecton.set(x, y);
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		if (lineOfSightIntersecton == null) {
			lineOfSightIntersecton = new Vector2f();
			float x = position.x + Player.LINE_OF_SIGHT_MAX * (float)Math.sin(angle);
			float y = position.y + Player.LINE_OF_SIGHT_MAX * (float)Math.cos(angle);
			lineOfSightIntersecton.set(x, y);
		}
		// Draw laser sight
		Vector2f muzzlePos = Util.pushTemporaryVector2f();
		getMuzzlePos(muzzlePos);
		r.drawLine(position.x, position.y, lineOfSightIntersecton.x, lineOfSightIntersecton.y, ColorUtil.RED, 1.0f);
		Util.popTemporaryVector2f();
		
		// Draw weapon
		Align a = isHeld() ? Align.BM : Align.MM;
		r.drawBox(a, position.x, position.y, 0.2f, getHeight(), ColorUtil.CYAN, this.angle);
	}
	
	@Override
	protected float renderBullet(IRenderer r, float x, float y, float p) {
		r.drawBox(Align.BR, x, y, 10.0f, 70.0f * p, ColorUtil.WHITE);
		
		x -= 10.0f;
		x -= 10.0f;
		return x;
	}
	
	private void getMuzzlePos(Vector2f dest) {
		float h = isHeld() ? getHeight() : getHeight()/2;
		dest.set(Util.getDirX(angle), Util.getDirY(angle)).mul(h).add(this.position);
	}
	
	private float getHeight() {
		return 0.2f;
	}
	
	@Override
	public MachineGun clone() {
		return new MachineGun(this);
	}
}

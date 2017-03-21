package game.world.entity.weapon;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.world.UpdateArgs;
import game.world.map.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class RocketLauncher extends Weapon {
	private transient int reloadSoundID = -1;
	
	public RocketLauncher(RocketLauncher rl) {
		super(rl);
	}
	
	public RocketLauncher(Vector2f position, int ammo) {
		super(position, ammo, true, 0.0f, 1, 5.0f, (float)Math.toRadians(4.0f));
	}
	
	@Override
	public void render(IRenderer r, Map map) {
		Align a = isHeld() ? Align.BM : Align.MM;
		Texture tex = r.getTextureBank().getTexture("Weapon_RocketLauncher.png");
		float ratio = getHeight() / tex.getHeight();
		r.drawTexture(tex, a, position.x, position.y, ratio*tex.getWidth(), getHeight(), this.angle);
	}
	
	@Override
	protected void fire(UpdateArgs ua, float angle) {
		Vector2f muzzlePos = new Vector2f().set(Util.getDirX(angle), Util.getDirY(angle)).mul(getHeight()).add(this.position);;
		
		// System.out.println("[Game]: Whoosh! Rocket fired!");
		ua.audio.play("rocket-launcher.wav", 0.5f, this.position);
		ua.bank.addEntityCached(new Rocket(muzzlePos, this.ownerId, this.ownerTeam, angle));
	}
	
	@Override
	public void update(UpdateArgs ua) {
		super.update(ua);
	}
	
	@Override
	protected void startReload(UpdateArgs ua) {
		if (this.reloadSoundID == -1) {
			// System.out.println("Reloading rocket launcher...");
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
	
	private float getHeight() {
		return isHeld() ? 0.5f : 0.75f;
	}
	
	@Override
	public RocketLauncher clone() {
		return new RocketLauncher(this);
	}

	@Override
	public String toString() {
		return "Rocket Launcher";
	}

	@Override
	public float aiValue() {
		// TODO Auto-generated method stub
		return 0;
	}
}

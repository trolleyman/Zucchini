package game.world.entity.weapon;

import com.google.gson.annotations.SerializedName;
import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import game.render.Texture;
import game.world.EntityBank;
import game.world.UpdateArgs;
import game.world.entity.Item;
import game.world.entity.update.HeldItemUpdate;
import org.joml.Vector2f;

/**
 * A weapon is something that the player can hold, and fire.
 * @author Callum
 */
public abstract class Weapon extends Item {
	@SerializedName("ccdn")
	private float currentCooldown = 0.0f;
	/** Current shots left in clip */
	@SerializedName("cshots")
	private int currentShots;
	@SerializedName("rdg")
	private boolean reloading = false;
	
	/** Has fireStart been called since the last update? */
	private transient boolean fire = false;
	
	/** The number of ammunition left */
	protected int ammo;
	
	@SerializedName("semi")
	private boolean semiAuto;
	
	@SerializedName("cdn")
	private float cooldown;
	private int shotsPerMag;
	@SerializedName("rdgTime")
	private float reloadingTime;
	
	@SerializedName("dv")
	private float deviation;
	@SerializedName("dvMn")
	private float deviationMin;
	@SerializedName("dvMx")
	private float deviationMax;
	@SerializedName("dvI")
	private float deviationInc;
	@SerializedName("dvD")
	private float deviationDecay;
	
	/**
	 * Constructs a weapon. The deviation is set to 0.
	 * @param position       Position of the weapon in the world
	 * @param _cooldown      The minimum time in seconds between each shot
	 * @param _shotsPerMag   The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 */
	public Weapon(Vector2f position, int _ammo, boolean _semiAuto, float _cooldown, int _shotsPerMag, float _reloadingTime) {
		this(position, _ammo, _semiAuto, _cooldown, _shotsPerMag, _reloadingTime, 0.0f);
	}
	
	/**
	 * Constructs a weapon. The deviation is constant.
	 * @param position       Position of the weapon in the world
	 * @param _ammo          The current ammunition held
	 * @param _semiAuto      If the weapon is semi-auto or not
	 * @param _cooldown      The minimum time in seconds between each shot
	 * @param _shotsPerMag   The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 * @param deviation      The deviation.
	 */
	public Weapon(Vector2f position, int _ammo, boolean _semiAuto, float _cooldown, int _shotsPerMag, float _reloadingTime, float deviation) {
		this(position, _ammo, _semiAuto, _cooldown, _shotsPerMag, _reloadingTime, deviation, deviation, 0.0f, 0.0f);
	}
	
	/**
	 * Constructs a weapon.
	 * @param position        Position of the weapon in the world
	 * @param _ammo           The current ammunition held
	 * @param _semiAuto       If the weapon is semi-auto or not
	 * @param _cooldown       The minimum time in seconds between each shot
	 * @param _shotsPerMag    The number of shots in a clip
	 * @param _reloadingTime  The time it takes to reload the weapon
	 * @param _deviationMin   The starting deviation, in radians
	 * @param _deviationMax   The maximum deviation, in radians
	 * @param _deviationInc   How much the deviation is incremented by each shot, in radians
	 * @param _deviationDecay How much the deviation decays by per second, in radians
	 */
	public Weapon(Vector2f position, int _ammo, boolean _semiAuto, float _cooldown, int _shotsPerMag, float _reloadingTime, float _deviationMin, float _deviationMax, float _deviationInc, float _deviationDecay) {
		super(position);
		this.ammo = _ammo;
		this.semiAuto = _semiAuto;
		
		this.cooldown = _cooldown;
		this.shotsPerMag = _shotsPerMag;
		this.currentShots = this.shotsPerMag;
		this.reloadingTime = _reloadingTime;
		this.deviation = _deviationMin;
		this.deviationMin = _deviationMin;
		this.deviationMax = _deviationMax;
		this.deviationInc = _deviationInc;
		this.deviationDecay = _deviationDecay;
	}
	
	@Override
	public void clientUpdate(UpdateArgs ua) {
		super.clientUpdate(ua);
		this.currentCooldown -= ua.dt;
		if (this.currentCooldown < 0.0f)
			this.currentCooldown = 0.0f;
	}
	
	@Override
	public void update(UpdateArgs ua) {
		boolean updated = false;
		if (this.fire && this.ammo == 0 && this.currentShots == 0) {
			//System.out.println("[Game]: [Weapon]: *Click*: Out of ammo");
			ua.audio.play("no-ammo-click.wav", 1.0f, this.position);
			this.currentCooldown = this.cooldown;
			this.fire = false;
		} else if (this.fire && this.currentCooldown <= 0.0f) {
			updated = true;
			// Fire!!!
			//System.out.println("BANG: Deviation: " + deviation);
			float fangle = angle + ((float) Math.random() * 2 - 1.0f) * deviation;
			fangle = Util.normalizeAngle(fangle);
			this.fire(ua, fangle);
			// Increment deviation
			this.deviation += deviationInc;
			
			// Decrement shots left
			this.currentShots--;
			if (this.currentShots == 0) {
				// Reload				
				this.doReload(ua.bank);
				
			} else {
				this.currentCooldown = this.cooldown;
			}
		}
		
		if (reloading) {
			this.startReload(ua);
		}
		
		if (this.semiAuto)
			this.fire = false;
		
		// Update deviation
		this.deviation = Math.max(deviationMin, Math.min(deviationMax, deviation - (float) ua.dt * deviationDecay));
		
		// Update cooldown
		this.currentCooldown = Math.max(0.0f, currentCooldown - (float) ua.dt);
		if (this.currentCooldown <= 0.0f && this.reloading) {
			this.endReload(ua);
			this.reloading = false;
			updated = true;
		}
		
		if (updated)
			ua.bank.updateEntityCached(new HeldItemUpdate(this.owner.entityId, this));
	}
	
	public void doReload(EntityBank bank) {
		if (this.ammo == -1 && this.shotsPerMag == 1)
			return;
		
		// Update cooldown and marker variable
		if (this.ammo != 0) {
			this.currentCooldown = this.reloadingTime;
			this.reloading = true;
			
			// Empty current mag into ammo pool if not empty
			if (this.ammo != -1 && this.currentShots != 0) {
				this.ammo += this.currentShots;
			}
			
			// Update current mag shots
			if (this.ammo != -1 && this.ammo < this.shotsPerMag)
				this.currentShots = this.ammo;
			else
				this.currentShots = this.shotsPerMag;
			
			// Update ammo
			if (this.ammo != -1)
				this.ammo = Math.max(0, this.ammo - this.shotsPerMag);
		}
		bank.updateEntityCached(new HeldItemUpdate(this.owner.entityId, this));
	}
	
	public boolean isReloading() {
		return reloading;
	}
	
	@Override
	public void renderUI(IRenderer r) {
		float x = r.getWidth() - Util.HUD_PADDING;
		float y = Util.HUD_PADDING;
		
		if (this.ammo == -1) {
			Texture t = r.getTextureBank().getTexture("infinity.png");
			r.drawTexture(t, Align.BR, x, y);
			x -= t.getWidth();
		} else {
			int a;
			if (!this.reloading) a = this.ammo;
			else
				a = this.ammo + this.currentShots - (int) Math.ceil((1 - (this.currentCooldown / this.reloadingTime)) * this.currentShots);
			
			Font f = r.getFontBank().getFont("emulogic.ttf");
			String s = "" + a;
			r.drawText(f, s, Align.BR, false, x, y, 1.0f, ColorUtil.WHITE);
			x -= f.getWidth(s, 1.0f);
		}
		x -= 20.0f;
		
		float p;
		if (!this.reloading) p = this.currentShots;
		else p = (1 - (this.currentCooldown / this.reloadingTime)) * this.currentShots;
		
		for (int i = 0; i < currentShots; i++) {
			if (this.reloading) {
				if (i + 1 < p)
					x = renderBullet(r, x, y, 1.0f);
				else if (i < p)
					x = renderBullet(r, x, y, Math.min(1.0f, p - i));
				else
					break;
			} else {
				x = renderBullet(r, x, y, 1.0f);
			}
		}
	}
	
	/**
	 * Renders a bullet for the UI, and returns the next x position to go to.
	 * @param p The proportion of the bullet to render. [0,1]
	 */
	protected abstract float renderBullet(IRenderer r, float x, float y, float p);
	
	@Override
	public void beginUse() {
		this.fire = true;
	}
	
	@Override
	public boolean isUsing() {
		if (semiAuto)
			return fire;
		else
			return fire && !reloading;
	}
	
	@Override
	public void endUse() {
		this.fire = false;
	}
	
	protected abstract void fire(UpdateArgs ua, float angle);
	
	protected void startReload(UpdateArgs ua) {
	}
	
	protected void endReload(UpdateArgs ua) {
	}
}

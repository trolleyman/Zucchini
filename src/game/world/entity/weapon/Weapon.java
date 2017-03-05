package game.world.entity.weapon;

import game.Util;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.Item;
import game.world.entity.update.SetHeldItem;
import org.joml.Vector2f;

/**
 * A weapon is something that the player can hold, and fire.
 * 
 * @author Callum
 */
public abstract class Weapon extends Item {
	private float currentCooldown = 0.0f;
	/** Current shots left in clip */
	private int currentShots;
	private boolean reloading = false;
	
	/** Has fireStart been called since the last update? */
	private transient boolean fire = false;
	
	private boolean semiAuto;
	
	private float cooldown;
	private int shots;
	private float reloadingTime;
	
	private float deviation;
	private float deviationMin;
	private float deviationMax;
	private float deviationInc;
	private float deviationDecay;
	
	public Weapon(Weapon g) {
		super(g);
		
		this.currentCooldown = g.currentCooldown;
		this.currentShots = g.currentShots;
		this.reloading = g.reloading;
		
		this.semiAuto = g.semiAuto;
		
		this.cooldown = g.cooldown;
		this.shots = g.shots;
		this.reloadingTime = g.reloadingTime;
		
		this.fire = g.fire;
		
		this.deviation = g.deviation;
		this.deviationMin = g.deviationMin;
		this.deviationMax = g.deviationMax;
		this.deviationInc = g.deviationInc;
		this.deviationDecay = g.deviationDecay;
	}
	
	/**
	 * Constructs a weapon. The deviation is set to 0.
	 * @param position Position of the weapon in the world
	 * @param _cooldown The minimum time in seconds between each shot
	 * @param _shots The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 */
	public Weapon(Vector2f position, boolean _semiAuto, float _cooldown, int _shots, float _reloadingTime) {
		this(position, _semiAuto, _cooldown, _shots, _reloadingTime, 0.0f);
	}
	
	/**
	 * Constructs a weapon. The deviation is constant.
	 * @param position Position of the weapon in the world
	 * @param _cooldown The minimum time in seconds between each shot
	 * @param _shots The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 * @param deviation The deviation.
	 */
	public Weapon(Vector2f position, boolean _semiAuto, float _cooldown, int _shots, float _reloadingTime, float deviation) {
		this(position, _semiAuto, _cooldown, _shots, _reloadingTime, deviation, deviation, 0.0f, 0.0f);
	}
	
	/**
	 * Constructs a weapon.
	 * @param position Position of the weapon in the world
	 * @param _cooldown The minimum time in seconds between each shot
	 * @param _shots The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 * @param _deviationMin The starting deviation, in radians
	 * @param _deviationMax The maximum deviation, in radians
	 * @param _deviationInc How much the deviation is incremented by each shot, in radians
	 * @param _deviationDecay How much the deviation decays by per second, in radians
	 */
	public Weapon(Vector2f position, boolean _semiAuto, float _cooldown, int _shots, float _reloadingTime, float _deviationMin, float _deviationMax, float _deviationInc, float _deviationDecay) {
		super(position);		
		this.semiAuto = _semiAuto;
		
		this.cooldown = _cooldown;
		this.shots = _shots;
		this.currentShots = this.shots;
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
		if (this.fire && this.currentCooldown <= 0.0f) {
			updated = true;
			// Fire!!!
			//System.out.println("BANG: Deviation: " + deviation);
			float fangle = angle + ((float)Math.random() * 2 - 1.0f) * deviation;
			fangle = Util.normalizeAngle(fangle);
			this.fire(ua, fangle);
			// Increment deviation
			this.deviation += deviationInc;
			
			// Decrement shots left
			this.currentShots--;
			if (this.currentShots == 0) {
				// Reload
				System.out.println("Reloading...");
				this.reload(ua);
				this.currentCooldown = this.reloadingTime;
				this.reloading = true;
				this.currentShots = this.shots;
			} else {
				this.currentCooldown = this.cooldown;
			}
		}
		if (this.semiAuto)
			this.fire = false;
		
		// Update deviation
		this.deviation = Math.max(deviationMin, Math.min(deviationMax, deviation - (float)ua.dt * deviationDecay));
		
		// Update cooldown
		this.currentCooldown = Math.max(0.0f, currentCooldown - (float)ua.dt);
		if (this.currentCooldown <= 0.0f && this.reloading) {
			System.out.println("Reloaded.");
			this.reloading = false;
			updated = true;
		}
		
		if (updated)
			ua.bank.updateEntityCached(new SetHeldItem(this.ownerId, this.clone()));
	}
	
	@Override
	public void renderUI(IRenderer r) {
		float x = r.getWidth() - Util.HUD_PADDING;
		float y = Util.HUD_PADDING;
		float p;
		if (!this.reloading) p = this.shots;
		else p = (1 - (this.currentCooldown / this.reloadingTime)) * this.shots;
		
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
	public void endUse() {
		this.fire = false;
	}
	
	protected abstract void fire(UpdateArgs ua, float angle);
	
	protected abstract void reload(UpdateArgs ua);
	
	@Override
	public abstract Weapon clone();
}

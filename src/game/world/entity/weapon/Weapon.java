package game.world.entity.weapon;

import game.Util;
import game.render.IRenderer;
import game.world.UpdateArgs;
import game.world.entity.Item;
import game.world.update.SetHeldItem;
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
	
	/**for sound*/
	private transient int reloadSoundID = -1;
	
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
	}
	
	/**
	 * Constructs a semi-auto weapon, with a cooldown
	 * @param position Position of the weapon in the world
	 * @param _cooldown The minimum time in seconds between each shot
	 * @param _shots The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 */
	public Weapon(Vector2f position, boolean _semiAuto, float _cooldown, int _shots, float _reloadingTime) {
		super(position);		
		this.semiAuto = _semiAuto;
		
		this.cooldown = _cooldown;
		this.shots = _shots;
		this.currentShots = this.shots;
		this.reloadingTime = _reloadingTime;
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
			this.fire(ua);
			
			// Decrement shots left
			this.currentShots--;
			if (this.currentShots == 0) {
				// Reload
				System.out.println("Reloading...");
				this.startReload(ua);
				
				this.currentCooldown = this.reloadingTime;
				this.reloading = true;
				this.currentShots = this.shots;
			} else {
				this.currentCooldown = this.cooldown;
			}
		}
		//System.out.println("cooldown: " + this.currentCooldown);
		if (this.semiAuto)
			this.fire = false;
		
		//update reload sound position
		if(this.reloading){
			this.startReload(ua);
		}
		
		this.currentCooldown = Math.max(0.0f, currentCooldown - (float)ua.dt);
		if (this.currentCooldown <= 0.0f && this.reloading) {
			System.out.println("Reloaded.");
			this.endReload(ua);
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
		if (!this.reloading)
			p = this.shots;
		else
			p = (1 - (this.currentCooldown / this.reloadingTime)) * this.shots;
		
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
	
	protected abstract void fire(UpdateArgs ua);
	
	protected abstract void startReload(UpdateArgs ua);
	
	protected abstract void endReload(UpdateArgs ua);
	
	@Override
	public abstract Weapon clone();
}

package game.world.entity;

import game.render.IRenderer;
import game.world.UpdateArgs;
import org.joml.Vector2f;

import java.util.ArrayList;

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
	public void update(UpdateArgs ua) {
		if (this.fire && this.currentCooldown <= 0.0f) {
			// Fire!!!
			this.fire(ua);
			
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
		//System.out.println("cooldown: " + this.currentCooldown);
		if (this.semiAuto)
			this.fire = false;
		
		this.currentCooldown = Math.max(0.0f, currentCooldown - (float)ua.dt);
		if (this.currentCooldown <= 0.0f && this.reloading) {
			System.out.println("Reloaded.");
			this.reloading = false;
		}
	}
	
	@Override
	public void beginUse() {
		this.fire = true;
	}
	
	@Override
	public void endUse() {
		this.fire = false;
	}
	
	protected abstract void fire(UpdateArgs ua);
	
	protected abstract void reload(UpdateArgs ua);
	
	@Override
	public abstract Weapon clone();
}

package game.world.entity;

import java.util.ArrayList;

import org.joml.Vector2f;

import game.audio.AudioManager;
import game.world.EntityBank;
import game.world.UpdateArgs;

public abstract class SemiAutoWeapon extends Weapon {
	private ArrayList<Entity> toFire = new ArrayList<>();
	
	private float currentCooldown = 0.0f;
	/** Current shots left in clip */
	private int currentShots;
	private boolean reloading = false;
	
	/** Has fireStart been called since the last update? */
	private boolean fire = false;
	
	private float cooldown;
	private int shots;
	private float reloadingTime;
	
	public SemiAutoWeapon(SemiAutoWeapon g) {
		super(g);
		
		this.toFire = new ArrayList<>();
		for (Entity e : g.toFire) {
			this.toFire.add(e.clone());
		}
		
		this.currentCooldown = g.currentCooldown;
		this.currentShots = g.currentShots;
		this.reloading = g.reloading;
		
		this.cooldown = g.cooldown;
		this.shots = g.shots;
		this.reloadingTime = g.reloadingTime;
	}
	
	/**
	 * Constructs a semi-auto weapon, with a cooldown
	 * @param position Position of the weapon in the world
	 * @param _cooldown The minimum time in seconds between each shot
	 * @param _shots The number of shots in a clip
	 * @param _reloadingTime The time it takes to reload the weapon
	 */
	public SemiAutoWeapon(Vector2f position, float _cooldown, int _shots, float _reloadingTime) {
		super(position);
		
		this.cooldown = _cooldown;
		this.shots = _shots;
		this.currentShots = this.shots;
		this.reloadingTime = _reloadingTime;
	}

	@Override
	public void update(UpdateArgs ua) {
		if (this.fire && this.currentCooldown <= 0.0f) {
			// Fire!!!
			ua.audio.play("handgunshot.wav", 1.0f);
			System.out.println("BANG!");
			// Add bullets to entity bank
			for (Entity e : this.fire())
				ua.bank.updateEntityCached(e);
			
			// Decrement shots left
			this.currentShots--;
			if (this.currentShots == 0) {
				// Reload
				System.out.println("Reloading...");
				this.currentCooldown = this.reloadingTime;
				this.reloading = true;
				this.currentShots = this.shots;
			}
		}
		this.fire = false;
		
		this.currentCooldown = Math.max(0.0f, currentCooldown - (float)ua.dt);
		if (this.currentCooldown <= 0.0f && this.reloading) {
			System.out.println("Reloaded.");
			this.reloading = false;
		}
	}

	@Override
	public void fireBegin() {
		this.fire = true;
	}

	@Override
	public void fireEnd() {
		
	}
	
	protected abstract Entity[] fire();
}

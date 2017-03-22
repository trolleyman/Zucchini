package game.world.entity.damage;

public class Damage {
	/** The source of the damage */
	public DamageSource source;
	/** What type the damage. */
	public DamageType type;
	/** The amount of damage this damage applied */
	public float amount;
	
	public Damage(DamageSource source, DamageType type, float amount) {
		this.source = source;
		this.type = type;
		this.amount = amount;
	}
}

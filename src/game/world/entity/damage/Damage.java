package game.world.entity.damage;

public class Damage {
	/** What player this damage originated from. TODO: Currently unused */
	public int ownerId;
	/** What team this damage originated from. TODO: Currently unused */
	public int ownerTeam;
	/** What type the damage. TODO: Currently unused */
	public DamageType type;
	/** The amount of damage this damage applied */
	public float amount;
	
	public Damage(int ownerId, int fromTeam, DamageType type, float amount) {
		this.ownerId = ownerId;
		this.ownerTeam = fromTeam;
		this.type = type;
		this.amount = amount;
	}
}

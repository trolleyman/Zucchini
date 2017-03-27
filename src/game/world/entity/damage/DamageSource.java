package game.world.entity.damage;

import game.world.Team;
import game.world.entity.Entity;
import game.world.entity.Player;

/**
 * Represents where a damage originated from
 */
public class DamageSource {
	/** The entity ID of the damage source */
	public int entityId;
	/** The team ID of the damage source */
	public int teamId;
	/** Is the source of the damage a player? */
	public boolean isPlayer;
	/** The readable name of the entity that caused the damages */
	public String readableName;
	
	/**
	 * Generates a "null" DamageSource, with an invalid source entity ID and team ID.
	 */
	public DamageSource() {
		this((Entity) null);
	}
	
	/**
	 * Generates a DamageSource, using the specified entity as the source
	 */
	public DamageSource(Entity e) {
		if (e == null) {
			entityId = Entity.INVALID_ID;
			teamId = Team.INVALID_TEAM;
			isPlayer = false;
			readableName = "unknown";
		} else {
			entityId = e.getId();
			teamId = e.getTeam();
			isPlayer = e instanceof Player;
			readableName = e.getReadableName();
		}
	}
}

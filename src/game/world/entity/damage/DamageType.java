package game.world.entity.damage;

import game.world.Team;
import game.world.entity.Entity;
import game.world.entity.Player;
import org.joml.Vector2f;

/**
 * Enum that represents what type of damage has been taken/given.
 */
public enum DamageType {
	UNKNOWN_DAMAGE(
			"%1 killed %2 somehow",
			new String[] {"%1 committed suicide", "%1 somehow managed to kill themselves"}),
	BULLET_DAMAGE(
			new String[] {"%1 murdered %2 in cold blood", "%1 executed %2", "%1 killed %2", "%1 assasinated %2", "%2 was dispatched by %1",
				"%1 ensured %2 will never see their family again"},
			"%1 blew their brains out"),
	LASER_DAMAGE(
			new String[] {"%1 reduced %2 to ashes", "%1 exterminated %2", "%2 was erased by %1", "%1 evicerated %2"},
			new String[] {"%1 shot their laser in the wrong direction", "%1 snuffed themselves out"}),
	EXPLOSION_DAMAGE(
			"%1 blew %2 up",
			"%1 blew themselves to smithereens"),
	KNIFE_DAMAGE(
			new String[] {"%1 knifed %2", "%1 stabbed %2"},
			"%1 slit their own throats");
	
	/**
	 * The description formats used when one entity has killed another
	 */
	private String[] descs;
	/**
	 * The description formats used when an entity had killed itself
	 */
	private String[] suicideDescs;
	
	DamageType(String desc, String suicideDesc) {
		this(new String[] {desc}, new String[] {suicideDesc});
	}
	
	DamageType(String[] descs, String suicideDesc) {
		this(descs, new String[] {suicideDesc});
	}
	
	DamageType(String desc, String[] suicideDescs) {
		this(new String[] {desc}, suicideDescs);
	}
	
	DamageType(String[] descs, String[] suicideDescs) {
		this.descs = descs;
		this.suicideDescs = suicideDescs;
	}
	
	private String getRandomDesc() {
		return descs[(int) (Math.random() * descs.length)];
	}
	private String getRandomSuicideDesc() {
		return suicideDescs[(int) (Math.random() * suicideDescs.length)];
	}
	
	/**
	 * Gets a human-readable description of the damage taken by the entity e.
	 * The returned string has a full stop.
	 * @param from The entity bank that has given the damage. Can be null.
	 * @param to The entity that has taken damage
	 */
	public String getDescription(Entity from, Entity to) {
		String ret;
		if (from == null) {
			ret = to.getReadableName() + " died of unknown causes.";
		} else if (from.getId() == to.getId()) {
			// Suicide
			ret = getRandomSuicideDesc().replace("%1", to.getReadableName()) + '.';
		} else {
			// Homicide
			ret = getRandomDesc().replace("%1", from.getReadableName());
			ret = ret.replace("%2", to.getReadableName());
			ret += '.';
		}
		// Ensure that the first character is a capital letter
		return ret;
	}
	
	public static void main(String[] args) {
		Entity a = new Player(Team.FIRST_PLAYER_TEAM, new Vector2f(), "A", null);
		a.setId(0);
		Entity b = new Player(Team.FIRST_PLAYER_TEAM, new Vector2f(), "B", null);
		b.setId(1);
		for (DamageType dt : DamageType.values()) {
			for (int i = 0; i < 10; i++)
				System.out.println(dt.name() + " desc: " + dt.getDescription(a, b));
			for (int i = 0; i < 3; i++)
				System.out.println(dt.name() + " suic: " + dt.getDescription(a, a));
		}
	}
}

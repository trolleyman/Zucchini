package game.world.entity.damage;

public enum DamageType {
	UNKNOWN_DAMAGE("killed somehow"),
	BULLET_DAMAGE("shot"),
	LASER_DAMAGE("reduced to ashes"),
	EXPLOSION_DAMAGE("blown up");
	
	private String adjective;
	
	DamageType(String adjective) {
		this.adjective = adjective;
	}
	
	public String getAdjective() {
		return adjective;
	}
}

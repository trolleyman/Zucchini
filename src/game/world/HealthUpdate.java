package game.world;

public class HealthUpdate {
	public int id;
	public float health;
	
	public HealthUpdate(int _id, float _health) {
		this.id = _id;
		this.health = _health;
	}
	
	public HealthUpdate(HealthUpdate hu) {
		this.id = hu.id;
		this.health = hu.health;
	}

	@Override
	public HealthUpdate clone() {
		return new HealthUpdate(this);
	}
}

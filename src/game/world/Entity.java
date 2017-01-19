package game.world;

public abstract class Entity {

	Vector2 position;
	Vector2 velocity;
	
	void translate(Vector2 v);
	void addVelocity(Vector2 v);
	void update(double dt);

	
}

package game.world.map;

import java.util.ArrayList;

public class CollisionTestMap extends Map {
	public CollisionTestMap() {
		super(new ArrayList<>(), 5.0f);
		
		float x0 = 0.0f;
		float y0 = 0.0f;
		
		float x1 = 5.0f;
		float y1 = 5.0f;
		
		float x2 = 1.5f;
		float y2 = 1.5f;
		
		float x3 = 2.5f;
		float y3 = 2.5f;
		
		this.walls.add(new Wall(x0, y0, x0, y1));
		this.walls.add(new Wall(x0, y0, x1, y0));
		
		this.walls.add(new Wall(x0, y1, x1, y1));
		this.walls.add(new Wall(x1, y0, x1, y1));
		
		this.walls.add(new Wall(x2, y2, x3, y3));
	}
}

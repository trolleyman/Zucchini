package game.world.map;

import java.util.ArrayList;

public class SimpleMap extends Map {
	public SimpleMap() {
		super(new ArrayList<>(), 5.0f, 1);
		
		float x0 = -1.0f;
		float y0 = -1.0f;
		
		float x1 = 5.0f;
		float y1 = 5.0f;
		
		float x2 = 2.0f;
		float y2 = 2.0f;
		
		float x3 = 4.0f;
		float y3 = 4.0f;
		
		this.walls.add(new Wall(x0, y0, x1, y0));
		this.walls.add(new Wall(x0, y0, x0, y1));
		
		this.walls.add(new Wall(x0, y1, x1, y1));
		this.walls.add(new Wall(x1, y0, x1, y1));
		
		this.walls.add(new Wall(x2, y2, x3, y3));
	}
}

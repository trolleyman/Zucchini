package game.world;

import java.util.ArrayList;

import org.joml.Vector2f;

public class Map {
	private ArrayList<Vector2f> lines;
	
	protected Map(ArrayList<Vector2f> _lines) {
		this.lines = _lines;
	}
	
	/**
	 * Returns the point at which the line given intersects with the map.
	 * @param v0 The start of the line
	 * @param v1 The end of the line
	 * @return null if there was no intersection
	 */
	public Vector2f intersects(Vector2f v0, Vector2f v1) {
		// TODO
		throw new UnsupportedOperationException();
	}
}


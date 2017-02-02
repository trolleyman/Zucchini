package game.world;

import org.joml.Vector2f;

import game.ColorUtil;
import game.render.IRenderer;

/**
 * Represents a specified map.
 * 
 * @author Callum
 */
public class Map {
	/** The "walls" of the map that entities can collide with */
	private float[] lines;
	
	/**
	 * Construct a map with the specified "wall"
	 */
	protected Map(float[] _lines) {
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
	
	/**
	 * Render the map
	 * @param r The renderer
	 */
	public void render(IRenderer r) {
		for (int i = 0; i < lines.length - 3; i += 4) {
			float x0 = lines[i  ];
			float y0 = lines[i+1];
			float x1 = lines[i+2];
			float y1 = lines[i+3];
			
			float x = Math.min(x0, x1);
			float y = Math.min(y0, y1);
			
			float w = Math.abs(x0 - x1);
			float h = Math.abs(y0 - y1);
			
			r.drawBox(x, y, w+0.1f, h+0.1f, ColorUtil.RED);
		}
	}
}


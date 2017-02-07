package game.world;

import org.joml.Vector2f;

public class PhysicsUtil {
	/**
	 * Finds an intersection point between two lines.
	 * <p>
	 * First line is 0->1
	 * <br>
	 * Second line is 2->3
	 * @return null if there was no intersection, otherwise the point of intersection
	 */
	public static Vector2f intersectLineLine(
			float p0_x, float p0_y, float p1_x, float p1_y,
			float p2_x, float p2_y, float p3_x, float p3_y) {
		
		float s1_x, s1_y, s2_x, s2_y;
		s1_x = p1_x - p0_x;		s1_y = p1_y - p0_y;
		s2_x = p3_x - p2_x;		s2_y = p3_y - p2_y;
		
		float s, t;
		s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
		t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
		
		if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
		{
			// Collision detected
			Vector2f ret = new Vector2f();
			
			ret.x = p0_x + (t * s1_x);
			ret.y = p0_y + (t * s1_y);
			
			return ret;
		}
		
		return null; // No collision
	}
	
	/**
	 * Returns p1 if p1 is closer to p0 than p2, or p2 otherwise
	 * <p>
	 * This function handles nulls.
	 */
	public static Vector2f getClosest(Vector2f p0, Vector2f p1, Vector2f p2) {
		return getClosest(p0.x, p0.y, p1, p2);
	}
	
	/**
	 * Returns p1 if p1 is closer to p0 than p2, or p2 otherwise
	 * <p>
	 * This function handles nulls.
	 */
	public static Vector2f getClosest(float p0_x, float p0_y, Vector2f p1, Vector2f p2) {
		if (p1 == null) {
			return p2;
		} else if (p2 == null) {
			return p1;
		} else {
			// Check if p1 is closer to p0 than p2
			float p1d = p1.distanceSquared(p0_x, p0_y);
			float p2d = p2.distanceSquared(p0_x, p0_y);
			
			if (p1d < p2d) {
				return p1;
			} else {
				return p2;
			}
		}
	}
}

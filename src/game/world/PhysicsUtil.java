package game.world;

import org.joml.Vector2f;

public class PhysicsUtil {
	
	/**
	 * Finds an intersection point between two lines.
	 * <p>
	 * First line is 0->1
	 * <br>
	 * Second line is 2->3
	 * @param dest Where to store the intersection result. Can be null, in which case, the function
	 *             will allocate a new Vector2f.
	 * @return null if there was no intersection, otherwise the point of intersection
	 */
	public static Vector2f intersectLineLine(
			float p0_x, float p0_y, float p1_x, float p1_y, // Line 1
			float p2_x, float p2_y, float p3_x, float p3_y, // Line 2
			Vector2f dest) {
		
		float s1_x, s1_y, s2_x, s2_y;
		s1_x = p1_x - p0_x;		s1_y = p1_y - p0_y;
		s2_x = p3_x - p2_x;		s2_y = p3_y - p2_y;
		
		float s, t;
		s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
		t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
		
		if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
		{
			// Collision detected
			if (dest == null)
				dest = new Vector2f();
			
			dest.x = p0_x + (t * s1_x);
			dest.y = p0_y + (t * s1_y);
			
			return dest;
		}
		return null; // No collision
	}
	
	/**
	 * This function calculates the intersection point between a line and a circle.
	 * @param x0 The x-coordinate of the centre of the circle
	 * @param y0 The y-coordinate of the centre of the circle
	 * @param radius The radius of the circle
	 * @param x1 The first x-coordinate of the line
	 * @param y1 The first y-coordinate of the line
	 * @param x2 The second x-coordinate of the line
	 * @param y2 The second y-coordinate of the line
	 * @param dest Where to store the result of the intersection. If null, allocates a new result.
	 * @return null if there was no intersection, the intersection point if there was (the mean of the
	 *         two points if there were two intersection points)
	 */
	public static Vector2f intersectCircleLine(
			float x0, float y0, float radius, // Circle
			float x1, float y1, float x2, float y2, // Line
			Vector2f dest) {
		
		// See http://stackoverflow.com/a/1079478
		float ac_x = x0 - x1;
		float ac_y = y0 - y1;
		
		float ab_x = x2 - x1;
		float ab_y = y2 - y1;
		
		// |ab|^2
		float ab2 = ab_x*ab_x + ab_y*ab_y;
		// |ab|
		float ab = (float) Math.sqrt(ab2);
		
		// |ad| = dot(ac, ab) / |ab|
		float ad = ac_x*ab_x/ab + ac_y*ab_y/ab;
		
		if (ad < 0.0f)
			return null;
		
		if (ab2 < ad*ad)
			return null;
		
		// d = a + ad * (ab/|ab|)
		float abn_x = ab_x / ab;
		float abn_y = ab_y / ab;
		float d_x = x1 + ad * abn_x;
		float d_y = y1 + ad * abn_y;
		
		// |cd|^2
		float cd_x = x0 - d_x;
		float cd_y = y0 - d_y;
		float cd2 = cd_x*cd_x + cd_y*cd_y;
		if (cd2 > radius*radius)
			return null;
		
		if (dest == null)
			dest = new Vector2f();
		
		dest.set(d_x, d_y);
		return dest;
	}
	
	/**
	 * Returns p1 if p1 is closer to p0 than p2, or p2 otherwise
	 * <p>
	 * This function handles nulls correctly.
	 */
	public static Vector2f getClosest(Vector2f p0, Vector2f p1, Vector2f p2) {
		return getClosest(p0.x, p0.y, p1, p2);
	}
	
	/**
	 * Returns p1 if p1 is closer to p0 than p2, or p2 otherwise
	 * <p>
	 * This function handles nulls correctly.
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

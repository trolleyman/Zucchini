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
		
		// See http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
		float vx = y2-y1;
		float vy = x1-x2;
		
		float rx = x1-x0;
		float ry = y1-y0;
		
		float rdotv = rx*vx + ry*vy;
		
		if (rdotv <= radius) {
			// intersection
			
			// w = vector from x0,y0 to the closest point on the line
			float wx = vx*rdotv;
			float wy = vy*rdotv;
			
			// u = closest point on the line
			float ux = x0+wx;
			float uy = y0+wy;
			
			if (dest == null)
				dest = new Vector2f();
			
			dest.set(ux, uy);
			return dest;
		} else {
			// no intersection
			return null;
		}
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

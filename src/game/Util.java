package game;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.joml.Vector3f;

/**
 * Class used for utility methods/constants that don't have a proper home anywhere.
 * 
 * @author Callum
 */
public class Util {
	/** The number of nanoseconds per second */
	public static long NANOS_PER_SECOND = 1_000_000_000;
	
	/**
	 * Updates Per Second
	 * <p>
	 * This is the number of times per second that the server updates the world.
	 */
	public static final double UPS = 120;
	/** Number of nanoseconds per update. This is calculated from the {@link #UPS} */
	public static final long NANOS_PER_UPDATE = (long) (NANOS_PER_SECOND / UPS);
	/** Time in seconds per update. This is calculated from the {@link #NANOS_PER_SECOND} */
	public static final double DT_PER_UPDATE = NANOS_PER_UPDATE / (double) NANOS_PER_SECOND;
	
	/**
	 * Snapshot Updates Per Second
	 * <p>
	 * This is the number of times per second that the server sends data to the client, and the number
	 * of times per second the client sends input data to the server.
	 */
	public static final double SUPS = 10;
	/** Number of nanoseconds per update. This is calculated from the {@link #SUPS} */
	public static final long NANOS_PER_SNAPSHOT_UPDATE = (long) (NANOS_PER_SECOND / SUPS);
	/** Time in seconds per update. This is calculated from the {@link #NANOS_PER_SNAPSHOT_UPDATE} */
	public static final double DT_PER_SNAPSHOT_UPDATE = NANOS_PER_SNAPSHOT_UPDATE / (double) NANOS_PER_SECOND;
	
	/**
	 * Debug prints a key to stdout
	 */
	public static void printKey(int key, int scancode, int action, int mods) {
		String actionStr = "";
		switch (action) {
		case GLFW_PRESS  : actionStr = "pressed "; break;
		case GLFW_RELEASE: actionStr = "released"; break;
		case GLFW_REPEAT : actionStr = "repeated"; break;
		}
		
		String modsStr = "";
		if ((mods & GLFW_MOD_SHIFT) != 0)
			modsStr += "SHIFT ";
		if ((mods & GLFW_MOD_CONTROL) != 0)
			modsStr += "CTRL ";
		if ((mods & GLFW_MOD_ALT) != 0)
			modsStr += "ALT ";
		if ((mods & GLFW_MOD_SUPER) != 0)
			modsStr += "SUPER ";
		
		if (!modsStr.equals(""))
			modsStr = "Mods: " + modsStr;
		
		String keyName = glfwGetKeyName(key, scancode);
		if (keyName == null)
			keyName = key + ":" + scancode;
		
		System.out.println(String.format("Key %s: %-8s %s", actionStr, keyName, modsStr));
	}
	
	/**
	 * Returns the base path from which all resources are found.
	 * <p>
	 * TODO: Currently the current directory, but this should change to be relative to a certain class
	 *       so that this application can be run from anywhere.
	 * TODO: Also this function should check if certain directories exist. (img, shader, etc.)
	 */
	public static Path getBasePath() {
		return Paths.get(".").toAbsolutePath();
	}
	
	private static final ThreadLocal<Vector3f> vector3f = new ThreadLocal<Vector3f>() {
		@Override
		protected Vector3f initialValue() { return new Vector3f(); }
	};
	
	/**
	 * Gets the thread local temporary Vector3f
	 */
	public static Vector3f getThreadLocalVector3f() {
		return vector3f.get();
	}
	
	/**
	 * Gets the angle between two points, relative clockwise to the up vector.
	 * @param x0 x-coordinate of the first point
	 * @param y0 y-coordinate of the first point
	 * @param x1 x-coordinate of the second point
	 * @param y1 y-coordinate of the second point
	 */
	public static float getAngle(float x0, float y0, float x1, float y1) {
		return (float) Util.getAngle((double)x0, (double)y0, (double)x1, (double)y1);
	}
	
	/**
	 * Gets the angle between two points, relative clockwise to the up vector.
	 * @param x0 x-coordinate of the first point
	 * @param y0 y-coordinate of the first point
	 * @param x1 x-coordinate of the second point
	 * @param y1 y-coordinate of the second point
	 */
	public static double getAngle(double x0, double y0, double x1, double y1) {
		double x = x1-x0;
		double y = y1-y0;
		return Util.getAngle(x, y);
	}
	
	/**
	 * Gets the angle between 0,0 and the point specified, relative clockwise to the up vector.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public static float getAngle(float x, float y) {
		return (float) Util.getAngle((double) x, (double) y);
	}
	
	/**
	 * Gets the angle between 0,0 and the point specified, relative clockwise to the up vector.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public static double getAngle(double x, double y) {
		double angle = Math.atan(x/y);
		if (!Double.isFinite(angle)) { // Check for NaNs, infinities etc.
			angle = 0.0f;
		} else if (y < 0.0f) {
			angle = Math.PI + angle;
		}
		return angle;
	}
}

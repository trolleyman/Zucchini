package game;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import game.render.Align;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Class used for utility methods/constants that don't have a proper home anywhere.
 * 
 * @author Callum
 */
public class Util {
	/** A small float */
	public static final float EPSILON = 0.000001f;
	
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
	public static final double SUPS = 60;
	/** Number of nanoseconds per update. This is calculated from the {@link #SUPS} */
	public static final long NANOS_PER_SNAPSHOT_UPDATE = (long) (NANOS_PER_SECOND / SUPS);
	/** Time in seconds per update. This is calculated from the {@link #NANOS_PER_SNAPSHOT_UPDATE} */
	public static final double DT_PER_SNAPSHOT_UPDATE = NANOS_PER_SNAPSHOT_UPDATE / (double) NANOS_PER_SECOND;
	
	// These values will be different for release
	public static final int LOBBY_WAIT_SECS = 3;
	public static final int GAME_START_WAIT_SECS = 3;
	
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
	public static String getBasePath() {
		return "./";
	}
	
	public static String getResourcesDir() {
		return Util.getBasePath() + '/' + "resources/";
	}
	
	// ======= Vector3f Stack =======
	private static final ThreadLocal<ArrayList<Vector3f>> vector3fStack = new ThreadLocal<ArrayList<Vector3f>>() {
		@Override
		protected ArrayList<Vector3f> initialValue() { return new ArrayList<>(); }
	};
	private static final ThreadLocal<Integer> vector3fStackSize = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() { return 0; }
	};
	
	/**
	 * Pushes a new temporary Vector3f onto the stack
	 * @returns the new temporary Vector3f
	 */
	public static Vector3f pushTemporaryVector3f() {
		ArrayList<Vector3f> stack = vector3fStack.get();
		int stackSize = vector3fStackSize.get();
		if (stack.size() == stackSize) {
			stack.add(new Vector3f());
		}
		Vector3f v = stack.get(stackSize);
		v.zero();
		stackSize++;
		vector3fStackSize.set(stackSize);
		if (stackSize > 256) {
			System.err.println("Warning: Vector3f stack size: " + stackSize);
		}
		return v;
	}
	
	/**
	 * Pops a temporary Vector3f off of the stack
	 */
	public static void popTemporaryVector3f() {
		int stackSize = vector3fStackSize.get();
		stackSize--;
		vector3fStackSize.set(stackSize);
	}
	
	// ======= Vector2f Stack =======
	private static final ThreadLocal<ArrayList<Vector2f>> vector2fStack = new ThreadLocal<ArrayList<Vector2f>>() {
		@Override
		protected ArrayList<Vector2f> initialValue() { return new ArrayList<>(); }
	};
	private static final ThreadLocal<Integer> vector2fStackSize = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() { return 0; }
	};
	
	/**
	 * Pushes a new temporary Vector2f onto the stack
	 * @returns the new temporary Vector2f
	 */
	public static Vector2f pushTemporaryVector2f() {
		ArrayList<Vector2f> stack = vector2fStack.get();
		int stackSize = vector2fStackSize.get();
		if (stack.size() == stackSize) {
			stack.add(new Vector2f());
		}
		Vector2f v = stack.get(stackSize);
		v.zero();
		stackSize++;
		vector2fStackSize.set(stackSize);
		if (stackSize > 256) {
			System.err.println("Warning: Vector2f stack size: " + stackSize);
		}
		return v;
	}
	
	/**
	 * Pops a temporary Vector2f off of the stack
	 */
	public static void popTemporaryVector2f() {
		int stackSize = vector2fStackSize.get();
		stackSize--;
		vector2fStackSize.set(stackSize);
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
		return normalizeAngle(angle);
	}
	
	public static float getDirX(float angle) {
		return (float)Math.sin(angle);
	}
	
	public static float getDirY(float angle) {
		return (float)Math.cos(angle);
	}
	
	public static float normalizeAngle(float angle) {
		angle %= Math.PI * 2;
		if (angle < 0.0f) {
			angle += Math.PI * 2;
		}
		return angle;
	}
	
	public static double normalizeAngle(double angle) {
		angle %= Math.PI * 2;
		if (angle < 0.0f) {
			angle += Math.PI * 2;
		}
		return angle;
	}
	
	public static boolean isDebugRenderMode() {
		return false;
	}
	
	/**
	 * Transforms x so that it is relative to the bottom-left of the object, rather than the alignment
	 */
	public static float alignToWorldX(Align a, float x, float w) {
		switch (a) {
			case BL:case ML:case TL: return x;
			case BM:case MM:case TM: return x - w/2.0f;
			case BR:case MR:case TR: return x - w;
		}
		return x;
	}
	
	/**
	 * Transforms y so that it is relative to the bottom-left of the object, rather than the alignment
	 */
	public static float alignToWorldY(Align a, float y, float h) {
		switch (a) {
			case BL:case BM:case BR: return y;
			case ML:case MM:case MR: return y - h/2.0f;
			case TL:case TM:case TR: return y - h;
		}
		return y;
	}
	
	/**
	 * Returns true if the point x,y is in the rectangle specified by the Align a, and rx, ry, rw, and rh
	 */
	public static boolean isPointInRect(float x, float y, Align a, float rx, float ry, float rw, float rh) {
		rx = alignToWorldX(a, rx, rw);
		ry = alignToWorldY(a, ry, rh);
		
		return x >= rx && x < rx + rw
		    && y >= ry && y < ry + rh;
	}
	
	/**
	 * This is the minimum length of a lobby name
	 */
	public static final int MIN_LOBBY_NAME_LENGTH = 3;
	/**
	 * This is the maximum length of a lobby name
	 */
	public static final int MAX_LOBBY_NAME_LENGTH = 32;
	
	/**
	 * Returns true if the character entered is valid for a lobby name
	 */
	public static boolean isValidLobbyNameChar(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c)
				|| (c >= ' ' && c <= '~');
	}
	
	/**
	 * Returns true if the lobby name entered is valid
	 */
	public static boolean isValidLobbyName(String s) {
		if (s == null)
			return false;
		if (s.length() < MIN_LOBBY_NAME_LENGTH || s.length() > MAX_LOBBY_NAME_LENGTH)
			return false;
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!isValidLobbyNameChar(c))
				return false;
		}
		return true;
	}
	
	/**
	 * This is the minimum length of a name
	 */
	public static final int MIN_NAME_LENGTH = 3;
	/**
	 * This is the maximum length of a name
	 */
	public static final int MAX_NAME_LENGTH = 16;
	
	/**
	 * Returns true if the character entered is valid for a name
	 */
	public static boolean isValidNameChar(char c) {
		return Character.isLetterOrDigit(c);
	}
	
	/**
	 * Returns true if the name entered is valid
	 */
	public static boolean isValidName(String s) {
		if (s == null)
			return false;
		if (s.length() < MIN_NAME_LENGTH || s.length() > MAX_NAME_LENGTH)
			return false;
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!isValidNameChar(c))
				return false;
		}
		return true;
	}
	
	public static final int DEFAULT_MIN_PLAYERS = 1;
	public static final int DEFAULT_MAX_PLAYERS = 4;
	
	public static float HUD_PADDING = 50.0f;
}

package game;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.FloatBuffer;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Map.Entry;

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
	
	/** General HUD padding value for UI elements */
	public static final float HUD_PADDING = 50.0f;
	
	/** Number of seconds to wait before the lobby enters the game */
	public static final int LOBBY_WAIT_SECS = 3;
	/** Number of seconds to wait before the game starts upon entering the game */
	public static final int GAME_START_WAIT_SECS = 3;
	/** Number of seconds to wait before the game goes back to the lobby screen upon the game ending */
	public static final float GAME_END_WAIT_SECS = 10.0f;
	
	private static final int STACK_SIZE_WARNING_LEN = 2048;

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
		if (stackSize > STACK_SIZE_WARNING_LEN) {
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
		if (stackSize < 0) {
			vector3fStackSize.set(0);
			throw new IndexOutOfBoundsException();
		} else {
			vector3fStackSize.set(stackSize);
		}
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
		if (stackSize > STACK_SIZE_WARNING_LEN) {
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
		if (stackSize < 0) {
			vector2fStackSize.set(0);
			throw new IndexOutOfBoundsException();
		} else {
			vector2fStackSize.set(stackSize);
		}
	}
	
	/**
	 * Gets the angle between two points, relative clockwise to the up vector.
	 * @param x0 x-coordinate of the first point
	 * @param y0 y-coordinate of the first point
	 * @param x1 x-coordinate of the second point
	 * @param y1 y-coordinate of the second point
	 */
	public static float getAngle(float x0, float y0, float x1, float y1) {
		float x = x1-x0;
		float y = y1-y0;
		return Util.getAngle(x, y);
	}
	
	/**
	 * Gets the angle between 0,0 and the point specified, relative clockwise to the up vector.
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public static float getAngle(float x, float y) {
		float angle = (float)Math.atan(x/y);
		if (!Float.isFinite(angle)) { // Check for NaNs, infinities etc.
			angle = 0.0f;
		} else if (y < 0.0f) {
			angle = (float)Math.PI + angle;
		}
		return normalizeAngle(angle);
	}
	
	/**
	 * Gets the non-reflex angle between two angles
	 */
	public static float getAngleDiff(float x, float y) {
		float diff = Math.abs(x - y);
		if (diff > Math.PI)
			diff = (float)(2*Math.PI) - diff;
		return diff;
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
		return (Character.isAlphabetic(c) || Character.isDigit(c)
				|| (c >= ' ' && c <= '~')) && !Character.isUpperCase(c);
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
		return Character.isLetterOrDigit(c) && !Character.isUpperCase(c);
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
	
	/**
	 * Sorts the float buffer specified
	 */
	public static void sortFloatBuffer(FloatBuffer buf) {
		sortFloatBuffer(buf, 0, buf.limit()-1);
	}
	
	/**
	 * Sorts the float buffer specified, from index left to index right.
	 */
	public static void sortFloatBuffer(FloatBuffer buf, int left, int right) {
		if (right > left) {
			int i = left;
			int j = right;
			float tmp;
			
			// Get pivot
			float v = buf.get((i + j)/2);
			
			// Sort so that x<v is on the left and x>v is on the right
			do {
				while (buf.get(i) < v)
					i++;
				while (buf.get(j) > v)
					j--;
				
				if (i <= j) {
					// Swap i and j
					tmp = buf.get(i);
					buf.put(i, buf.get(j));
					buf.put(j, tmp);
					i++;
					j--;
				}
			} while (i <= j);
			
			// Recurse
			if (left < j) sortFloatBuffer(buf, left, j);
			if (i < right) sortFloatBuffer(buf, i, right);
		}
	}
	
	/**
	 * Reverses a float buffer
	 */
	public static void reverseFloatBuffer(FloatBuffer buf) {
		for (int i = 0, j = buf.limit()-1; i < j; i++, j--) {
			// Swap element
			float tmp = buf.get(i);
			buf.put(i, buf.get(j));
			buf.put(j, tmp);
		}
	}
	
	/**
	 * Removes similar floats from a sorted (ascending) FloatBuffer.
	 * @param diff If two consecutive floats are < diff apart, the second float is removed
	 */
	public static void removeSimilarFloats(FloatBuffer buf, float diff) {
		int i = 0;
		int j = 1;
		while (true) {
			while (j < buf.limit() && buf.get(j) - buf.get(i) < diff) {
				j++;
			}
			
			i++;
			if (j >= buf.limit())
				break;
			
			buf.put(i, buf.get(j));
			j++;
		}
		buf.limit(i);
	}
	
	/**
	 * Gets the last message in a throwable chain
	 * @param t The throwable
	 */
	public static String getLastMessage(Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage();
	}
}

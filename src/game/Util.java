package game;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.file.Path;
import java.nio.file.Paths;

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
	public static final double SUPS = 30;
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
}

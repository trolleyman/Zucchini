package game;

import org.joml.Vector4f;

/**
 * Color util class. I know it's ColoUr, but I'm following the American convention here so it's
 * consistent with Java's standard library.
 * 
 * We're using Vector4f's so that their values can be rewritten.
 * 
 * @author Callum
 */
public class ColorUtil {
	public static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector4f BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
	
	public static final Vector4f RED   = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Vector4f GREEN = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Vector4f BLUE  = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
}

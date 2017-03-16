package game;

import org.joml.Vector4f;

/**
 * Color util class. I know it's Colo<b>u</b>r, but I'm following the American convention here so it's
 * consistent with Java's standard library.
 * 
 * We're using Vector4f's so that their values can be rewritten.
 * 
 * @author Callum
 */
public class ColorUtil {
	public static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Vector4f BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Vector4f TRANSPARENT = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
	
	public static final Vector4f RED   = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Vector4f GREEN = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Vector4f BLUE  = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
	
	public static final Vector4f YELLOW = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
	public static final Vector4f PINK   = new Vector4f(1.0f, 0.0f, 1.0f, 1.0f);
	public static final Vector4f CYAN   = new Vector4f(0.0f, 1.0f, 1.0f, 1.0f);
	
	public static final Vector4f LIGHT_GREY = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
	public static final Vector4f DARK_GREY = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
}

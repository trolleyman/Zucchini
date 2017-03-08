package game.world.entity;

import org.joml.Vector4f;

/**
 * Utilities for lights.
 *
 * Constants for temperatures taken from
 * <a href="https://www.c4dcafe.com/ipb/forums/topic/54680-rgb-kelvin-values-for-popular-light-types/">
 *     https://www.c4dcafe.com/ipb/forums/topic/54680-rgb-kelvin-values-for-popular-light-types/
 * </a>
 */
public class LightUtil {
	public static final Vector4f LIGHT_CANDLE_1900               = new Vector4f(255/255.0f, 147/255.0f, 41/255.0f, 1.0f);
	public static final Vector4f LIGHT_40W_TUNGSTEN_2600         = new Vector4f(255/255.0f, 197/255.0f, 143/255.0f, 1.0f);
	public static final Vector4f LIGHT_100W_TUNGSTEN_2850        = new Vector4f(255/255.0f, 214/255.0f, 170/255.0f, 1.0f);
	public static final Vector4f LIGHT_HALOGEN_3200              = new Vector4f(255/255.0f, 241/255.0f, 224/255.0f, 1.0f);
	public static final Vector4f LIGHT_CARBON_ARC_5200           = new Vector4f(255/255.0f, 250/255.0f, 244/255.0f, 1.0f);
	public static final Vector4f LIGHT_HIGH_NOON_SUN_5400        = new Vector4f(255/255.0f, 255/255.0f, 251/255.0f, 1.0f);
	public static final Vector4f LIGHT_DIRECT_SUNLIGHT_6000      = new Vector4f(255/255.0f, 255/255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_OVERCAST_SKY_7000         = new Vector4f(201/255.0f, 226/255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_CLEAR_BLUE_SKY_20000      = new Vector4f(64 /255.0f, 156/255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_WARM_FLUORESCENT          = new Vector4f(255/255.0f, 244/255.0f, 229/255.0f, 1.0f);
	public static final Vector4f LIGHT_STANDARD_FLUORESCENT      = new Vector4f(244/255.0f, 255/255.0f, 250/255.0f, 1.0f);
	public static final Vector4f LIGHT_COOL_WHITE_FLUORESCENT    = new Vector4f(212/255.0f, 235/255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_FULL_SPECTRUM_FLUORESCENT = new Vector4f(255/255.0f, 244/255.0f, 242/255.0f, 1.0f);
	public static final Vector4f LIGHT_GROW_LIGHT_FLUORESCENT    = new Vector4f(255/255.0f, 239/255.0f, 247/255.0f, 1.0f);
	public static final Vector4f LIGHT_BLACK_LIGHT_FLUORESCENT   = new Vector4f(167/255.0f, 0  /255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_MERCURY_VAPOR             = new Vector4f(216/255.0f, 247/255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_SODIUM_VAPOR              = new Vector4f(255/255.0f, 209/255.0f, 178/255.0f, 1.0f);
	public static final Vector4f LIGHT_METAL_HALIDE              = new Vector4f(242/255.0f, 252/255.0f, 255/255.0f, 1.0f);
	public static final Vector4f LIGHT_HIGH_PRESSURE_SODIUM      = new Vector4f(255/255.0f, 183/255.0f, 76 /255.0f, 1.0f);
}

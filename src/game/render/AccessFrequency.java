package game.render;

/**
 * Specifies the frequency of access to the VBO
 * 
 * @author Callum
 */
public enum AccessFrequency {
	/** The data will be modified once and drawn many times */
	STATIC,
	/** The data will be modified many times and drawn many times */
	DYNAMIC;
}

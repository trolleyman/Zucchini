package game.render;

/**
 * Which debug framebuffer to render
 */
public enum DebugFramebuffer {
	/** Draw no debug framebuffer */
	NONE,
	/** Draw the stencil buffer */
	STENCIL,
	/** Draw the world, unlit, with no effects */
	WORLD,
	/** Draw the lighting on it's own */
	LIGHTING,
	/** Draw the glitch framebuffer */
	GLITCH,
}

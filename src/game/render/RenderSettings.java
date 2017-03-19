package game.render;

public class RenderSettings {
	/** Is vsync enabled? */
	public boolean vSync;
	/** Whether to draw the line of sight stencil so that only the player can see in front of them */
	public boolean drawLineOfSightStencil;
	/** Only draw the lighting framebuffer */
	public boolean debugDrawLightingFramebuffer;
	/** Draw the debug lines of the line of sight */
	public boolean debugDrawLineOfSightLines;
	/** Whether or not the font is drawn debug style */
	public boolean debugDrawFont;
	
	public RenderSettings() {
		vSync = true;
		drawLineOfSightStencil = true;
		debugDrawLightingFramebuffer = false;
		debugDrawLineOfSightLines = false;
		debugDrawFont = false;
	}
	
	public RenderSettings(RenderSettings s) {
		this.set(s);
	}
	
	/**
	 * Sets the current settings to the settings specified
	 */
	public void set(RenderSettings s) {
		vSync = s.vSync;
		drawLineOfSightStencil = s.drawLineOfSightStencil;
		debugDrawLightingFramebuffer = s.debugDrawLightingFramebuffer;
		debugDrawLineOfSightLines = s.debugDrawLineOfSightLines;
		debugDrawFont = s.debugDrawFont;
	}
	
	@Override
	public RenderSettings clone() {
		return new RenderSettings(this);
	}
}

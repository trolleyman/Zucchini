package game.render;

public class RenderSettings {
	/** Is vsync enabled? */
	public boolean vSync;
	/** Should the line of sight stencil be drawn? */
	public boolean drawLineOfSightStencil;
	/** Should the glitch effect be drawn? */
	public boolean drawGlitchEffect;
	/** Only draw the lighting framebuffer */
	public boolean debugDrawLightingFramebuffer;
	/** Draw the debug lines of the line of sight */
	public boolean debugDrawLineOfSightLines;
	
	public RenderSettings() {
		vSync = true;
		drawLineOfSightStencil = true;
		drawGlitchEffect = true;
		debugDrawLightingFramebuffer = false;
		debugDrawLineOfSightLines = false;
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
		drawGlitchEffect = s.drawGlitchEffect;
		debugDrawLightingFramebuffer = s.debugDrawLightingFramebuffer;
		debugDrawLineOfSightLines = s.debugDrawLineOfSightLines;
	}
	
	@Override
	protected RenderSettings clone() {
		return new RenderSettings(this);
	}
}

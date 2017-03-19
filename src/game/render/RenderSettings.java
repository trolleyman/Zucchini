package game.render;

public class RenderSettings {
	/** Is vsync enabled? */
	public boolean vSync;
	/** Should the line of sight stencil be drawn? */
	public boolean drawLineOfSightStencil;
	/** Should the glitch effect be drawn? */
	public boolean drawGlitchEffect;
	/** Draw the debug lines of the line of sight */
	public boolean debugDrawLineOfSightLines;
	/** Draw the specified framebuffer on the screen in a snall box on the screen */
	public DebugFramebuffer debugDrawFramebuffer;
	
	public RenderSettings() {
		vSync = true;
		drawLineOfSightStencil = true;
		drawGlitchEffect = false;
		debugDrawLineOfSightLines = false;
		debugDrawFramebuffer = DebugFramebuffer.NONE;
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
		debugDrawLineOfSightLines = s.debugDrawLineOfSightLines;
		debugDrawFramebuffer = s.debugDrawFramebuffer;
	}
	
	@Override
	protected RenderSettings clone() {
		return new RenderSettings(this);
	}
}

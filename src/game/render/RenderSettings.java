package game.render;

public class RenderSettings {
	/** Is vsync enabled? */
	public boolean vSync;
	/** Whether to draw the line of sight stencil so that only the player can see in front of them */
	public boolean drawLineOfSightStencil;
	/** Only draw the lighting framebuffer */
	public boolean debugDrawLightingFramebuffer;
	
	public RenderSettings() {
		vSync = true;
		drawLineOfSightStencil = false;
		debugDrawLightingFramebuffer = false;
	}
	
	public RenderSettings(RenderSettings s) {
		vSync = s.vSync;
		drawLineOfSightStencil = s.drawLineOfSightStencil;
		debugDrawLightingFramebuffer = s.debugDrawLightingFramebuffer;
	}
	
	@Override
	protected RenderSettings clone() {
		return new RenderSettings(this);
	}
}

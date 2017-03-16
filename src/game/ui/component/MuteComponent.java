package game.ui.component;

import game.ColorUtil;
import game.audio.AudioManager;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import org.joml.Vector4f;

public class MuteComponent extends AbstractButtonComponent {
	private static final float BORDER_WIDTH = 5.0f;
	private AudioManager audio;
	
	private Texture currentTex;
	private boolean showMute;
	private boolean justClicked = false;
	private boolean hover = false;
	private boolean pressed = false;
	
	public MuteComponent(Align a, float x, float y, AudioManager audio) {
		super(a, x, y);
		this.audio = audio;
		showMute = audio.isMuted();
	}
	
	@Override
	public void update(double dt) {
		super.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		if (showMute)
			currentTex = r.getTextureBank().getTexture("Volume-Mute.png");
		else if (audio.getVolume() < 0.3f)
			currentTex = r.getTextureBank().getTexture("Volume-Low.png");
		else if (audio.getVolume() < 0.7f)
			currentTex = r.getTextureBank().getTexture("Volume-Medium.png");
		else
			currentTex = r.getTextureBank().getTexture("Volume-High.png");
		r.drawBox(a, x, y, getWidth(), getHeight(), ColorUtil.WHITE);
		r.drawBox(a, x+BORDER_WIDTH, y+BORDER_WIDTH, currentTex.getWidth(), currentTex.getHeight(), ColorUtil.BLACK);
		float tx = x + BORDER_WIDTH;
		float ty = y + BORDER_WIDTH;
		if (pressed) {
			tx += 3.0f;
			ty -= 3.0f;
		}
		if (hover || pressed)
			r.drawTexture(currentTex, a, tx, ty, new Vector4f(0.8f, 0.8f, 0.8f, 1.0f));
		else
			r.drawTexture(currentTex, a, tx, ty);
	}
	
	@Override
	protected void onDefault() {
		showMute = audio.isMuted();
		hover = false;
		pressed = false;
	}
	
	@Override
	protected void onHover() {
		hover = true;
		pressed = false;
	}
	
	@Override
	protected void onPressed() {
		hover = false;
		pressed = true;
	}
	
	@Override
	protected void onClicked() {
		// Toggle mute
		if (audio.isMuted()) {
			System.out.println("[Audio]: Unmuted");
			audio.unMute();
		} else {
			System.out.println("[Audio]: Muted");
			audio.mute();
		}
		showMute = audio.isMuted();
	}
	
	@Override
	protected float getWidth() {
		return (currentTex == null ? 0 : currentTex.getWidth()) + BORDER_WIDTH*2;
	}
	
	@Override
	protected float getHeight() {
		return (currentTex == null ? 0 : currentTex.getHeight()) + BORDER_WIDTH*2;
	}
}

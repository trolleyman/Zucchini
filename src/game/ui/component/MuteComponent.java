package game.ui.component;

import game.ColorUtil;
import game.audio.AudioManager;
import game.render.Align;
import game.render.IRenderer;
import game.render.Texture;
import game.render.TextureBank;
import org.joml.Vector4f;

public class MuteComponent extends AbstractButtonComponent {
	private static final float BORDER_WIDTH = 5.0f;
	private static final Vector4f MUTE_PRESSED_COLOR = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
	private AudioManager audio;
	
	private Texture currentTex;
	private boolean hover = false;
	private boolean pressed = false;
	
	public MuteComponent(Align a, float x, float y, AudioManager audio, TextureBank bank) {
		super(a, x, y);
		this.audio = audio;
		setCurrentTexture(bank);
	}
	
	private void setCurrentTexture(TextureBank bank) {
		if (audio.isMuted())
			currentTex = bank.getTexture("Volume-Mute.png");
		else if (audio.getVolume() < 0.3f)
			currentTex = bank.getTexture("Volume-Low.png");
		else if (audio.getVolume() < 0.7f)
			currentTex = bank.getTexture("Volume-Medium.png");
		else
			currentTex = bank.getTexture("Volume-High.png");
	}
	
	@Override
	public void update(double dt) {
		super.update(dt);
	}
	
	@Override
	public void render(IRenderer r) {
		setCurrentTexture(r.getTextureBank());
		r.drawBox(a, x, y, getWidth(), getHeight(), ColorUtil.WHITE);
		r.drawBox(a, x+BORDER_WIDTH, y+BORDER_WIDTH, currentTex.getWidth(), currentTex.getHeight(), ColorUtil.BLACK);
		float tx = x + BORDER_WIDTH;
		float ty = y + BORDER_WIDTH;
		if (pressed) {
			tx += 3.0f;
			ty -= 3.0f;
		}
		if (hover || pressed)
			r.drawTexture(currentTex, a, tx, ty, MUTE_PRESSED_COLOR);
		else
			r.drawTexture(currentTex, a, tx, ty);
	}
	
	@Override
	protected void onDefault() {
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
		if (audio.isMuted())
			audio.unMute();
		else
			audio.mute();
	}
	
	@Override
	public float getWidth() {
		return (currentTex == null ? 0 : currentTex.getWidth()) + BORDER_WIDTH*2;
	}
	
	@Override
	public float getHeight() {
		return (currentTex == null ? 0 : currentTex.getHeight()) + BORDER_WIDTH*2;
	}
}

package game.ui.component;

import game.ColorUtil;
import game.Util;
import game.audio.AudioManager;
import game.render.Align;
import game.render.IRenderer;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class VolumeComponent extends UIComponent {
	private static final float BORDER_WIDTH = 5.0f;
	private static final float BAR_HEIGHT = 10.0f;
	
	private float x;
	private float y;
	private float w;
	private float h;
	
	private AudioManager audio;
	
	private boolean hover = false;
	private boolean grabbed = false;
	
	private double mx;
	private double my;
	
	public VolumeComponent(float x, float y, float w, float h, AudioManager audio) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.audio = audio;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public void handleCursorPos(double xpos, double ypos) {
		this.mx = xpos;
		this.my = ypos;
	}
	
	@Override
	public void handleMouseButton(int button, int action, int mods) {
		if (button == GLFW_MOUSE_BUTTON_1) {
			if (action == GLFW_PRESS) {
				// Check if mouse is in rect
				if (Util.isPointInRect((float)mx, (float)my, Align.BL, x, y, w, h))
					this.grabbed = true;
			} else if (action == GLFW_RELEASE) {
				this.grabbed = false;
			}
		}
	}
	
	@Override
	public void update(double dt) {
		if (Util.isPointInRect((float)mx, (float)my, Align.BL, x, y, w, h)) {
			hover = true;
		} else {
			hover = false;
		}
		
		if (grabbed) {
			float logicalHeight = h - 2 * BORDER_WIDTH - BAR_HEIGHT;
			float logicalY = y + BORDER_WIDTH + BAR_HEIGHT / 2;
			float p = ((float) my - logicalY) / logicalHeight;
			p = Math.min(1.0f, Math.max(0.0f, p));
			audio.setVolume(p);
		}
	}
	
	@Override
	public void render(IRenderer r) {
		// Setup colors
		Vector4f innerBarColor;
		Vector4f currentVolumeBarColor;
		if (grabbed) {
			currentVolumeBarColor = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
		} else if (hover) {
			currentVolumeBarColor = new Vector4f(0.6f, 0.6f, 0.6f, 1.0f);
		} else {
			currentVolumeBarColor = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
		}
		if (audio.isMuted()) {
			innerBarColor = new Vector4f(0.5f, 0.0f, 0.0f, 1.0f);
		} else {
			innerBarColor = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
		}
		
		// Draw background box
		r.drawBox(Align.BL, x, y, w, h, ColorUtil.WHITE);
		r.drawBox(Align.BL, x+BORDER_WIDTH, y+BORDER_WIDTH, w-BORDER_WIDTH*2, h-BORDER_WIDTH*2, ColorUtil.BLACK);
		
		// Draw current volume bar
		float volumeBarH = h - 2*BORDER_WIDTH - BAR_HEIGHT;
		float volumeY = y + BORDER_WIDTH + BAR_HEIGHT/2 + (audio.getVolume() * volumeBarH);
		r.drawBox(Align.BL, x+BORDER_WIDTH, y+BORDER_WIDTH, w-BORDER_WIDTH*2, audio.getVolume() * (h-BORDER_WIDTH*2), innerBarColor);
		r.drawBox(Align.ML, x+BORDER_WIDTH, volumeY, w-BORDER_WIDTH*2, BAR_HEIGHT, currentVolumeBarColor);
	}
	
	public float getWidth() {
		return w;
	}
	
	public float getHeight() {
		return h;
	}
}

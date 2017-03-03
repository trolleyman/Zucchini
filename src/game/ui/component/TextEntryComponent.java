package game.ui.component;

import static org.lwjgl.glfw.GLFW.*;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.IRenderer;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.function.Predicate;

public class TextEntryComponent extends AbstractButtonComponent {
	private Font f;
	private Predicate<Character> isValidChar;
	private Runnable submitFunc;
	
	public float w;
	public float h;
	
	private Vector4f boxColour = ColorUtil.BLACK;
	private Vector4f borderColour = ColorUtil.WHITE;
	
	private final float BORDER_WIDTH = 5;
	
	private ArrayList<Character> currentString;
	private float maxLength;
	private int cursorPos = 0;
	
	/**
	 * Constructs a new TextEntryComponent
	 * @param f The font to be used
	 * @param isValidChar Function returning true if the character given is valid.
	 * @param submitFunc Function that is called when the user presses the ENTER key.
	 */
	public TextEntryComponent(Font f, Predicate<Character> isValidChar, Runnable submitFunc, int maxLength, Align a, float x, float y, float w, float h) {
		super(a, x, y);
		this.f = f;
		this.isValidChar = isValidChar;
		this.submitFunc = submitFunc;
		
		this.currentString = new ArrayList<>(maxLength);
		this.maxLength = maxLength;
		
		this.w = w;
		this.h = h;
	}
	
	@Override
	public void render(IRenderer r) {
		// Draw the outer box (also acts as the border)
		r.drawBox(a, x, y, w, h, borderColour);
		// Draw the inner box
		r.drawBox(a, x+BORDER_WIDTH, y+BORDER_WIDTH, w-2*BORDER_WIDTH, h-2*BORDER_WIDTH, boxColour);
		// Draw the current string
		float ny = Util.alignToWorldY(Align.BL, h/2.0f, f.getHeight(1.0f));
		r.drawText(f, getString(), a, false, x + BORDER_WIDTH + 10.0f, ny, 1.0f);
	}
	
	private void out(String msg) {
		System.out.println("TextEntry Info: " + msg);
	}
	
	@Override
	public void handleChar(char c) {
		if (!this.isValidChar.test(c)) {
			out("'" + c + "' is not a valid char.");
		} else if (currentString.size() >= maxLength) {
			out("String is at max length: " + getStringWithPos());
		} else {
			// Insert at i
			this.currentString.add(cursorPos, c);
			out("Added '" + c + "' @ " + cursorPos + ": " + getStringWithPos());
			cursorPos++;
		}
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS || action == GLFW_REPEAT) {
			if (key == GLFW_KEY_LEFT) {
				if (cursorPos > 0)
					cursorPos--;
				out("Cursor <- : " + getStringWithPos());
			} else if (key == GLFW_KEY_RIGHT) {
				if (cursorPos < currentString.size())
					cursorPos++;
				out("Cursor -> : " + getStringWithPos());
			} else if (key == GLFW_KEY_HOME || key == GLFW_KEY_PAGE_UP) {
				cursorPos = 0;
				out("Cursor Home: " + getStringWithPos());
			} else if (key == GLFW_KEY_END || key == GLFW_KEY_PAGE_DOWN) {
				cursorPos = currentString.size();
				out("Cursor End: " + getStringWithPos());
			} else if (key == GLFW_KEY_ENTER) {
				out("Submit: '" + getString() + "'");
				submitFunc.run();
			}
		}
	}
	
	@Override
	protected void onDefault() {
		borderColour = ColorUtil.WHITE;
	}
	
	@Override
	protected void onHover() {
		borderColour = ColorUtil.LIGHT_GREY;
	}
	
	@Override
	protected void onPressed() {
		borderColour = ColorUtil.DARK_GREY;
	}
	
	@Override
	protected void onClicked() {
		float mx = getMouseX();
		float my = getMouseY();
		
		FontAdvancer fa = f.getAdvancer();
		
	}
	
	private String getStringWithPos() {
		StringBuilder s = new StringBuilder(currentString.size());
		for (int i = 0; i < currentString.size(); i++) {
			if (cursorPos == i)
				s.append('|');
			s.append(currentString.get(i));
		}
		if (cursorPos == currentString.size())
			s.append('|');
		return s.toString();
	}
	
	public String getString() {
		StringBuilder s = new StringBuilder(currentString.size());
		for (Character c : currentString)
			s.append(c);
		return s.toString();
	}
	
	@Override
	protected float getWidth() {
		return w;
	}
	
	@Override
	protected float getHeight() {
		return h;
	}
}

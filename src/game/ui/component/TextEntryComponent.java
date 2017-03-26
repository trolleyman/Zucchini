package game.ui.component;

import static org.lwjgl.glfw.GLFW.*;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.FontAdvancer;
import game.render.IRenderer;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBTTAlignedQuad;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class TextEntryComponent extends AbstractButtonComponent {
	private static final float BORDER_WIDTH = 5;
	private static final float INNER_PADDING = 5;
	
	private final Font f;
	private final float scale;
	private final Predicate<Character> isValidChar;
	private final Runnable submitFunc;
	private final Runnable onFocus;
	private final CharacterConverter characterConverter;
	
	public float w;
	public float h;
	
	private Vector4f boxColour = ColorUtil.BLACK;
	private Vector4f borderColour = ColorUtil.WHITE;
	
	private ArrayList<Character> currentString;
	private float maxLength;
	private int cursorPos = 0;
	
	private double time = 0.0;
	private boolean enabled = true;
	
	/**
	 * Constructs a new TextEntryComponent
	 * @param f The font to be used
	 * @param scale The scale of the font
	 * @param isValidChar Function returning true if the character given is valid.
	 * @param submitFunc Function that is called when the user presses the ENTER key.
	 * @param maxLength The max length of the input
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param w The width of the text entry
	 */
	public TextEntryComponent(Font f, float scale, Predicate<Character> isValidChar, Runnable submitFunc, int maxLength, float x, float y, float w) {
		this(f, scale, isValidChar, submitFunc, maxLength, () -> {}, x, y, w);
	}
	
	/**
	 * Constructs a new TextEntryComponent
	 * @param f The font to be used
	 * @param isValidChar Function returning true if the character given is valid.
	 * @param submitFunc Function that is called when the user presses the ENTER key.
	 * @param onFocus Function that is called when the user clicks on the entry.
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param w The width of the text entry
	 */
	public TextEntryComponent(Font f, float scale, Predicate<Character> isValidChar, Runnable submitFunc, int maxLength, Runnable onFocus, float x, float y, float w) {
		this(f, scale, isValidChar, submitFunc, maxLength, onFocus, (c) -> c, x, y, w);
	}
	
	/**
	 * Constructs a new TextEntryComponent
	 * @param f The font to be used
	 * @param isValidChar Function returning true if the character given is valid.
	 * @param submitFunc Function that is called when the user presses the ENTER key.
	 * @param onFocus Function that is called when the user clicks on the entry.
	 * @param characterConverter Function that is called to convert a character whenever a character is entered.
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param w The width of the text entry
	 */
	public TextEntryComponent(Font f, float scale, Predicate<Character> isValidChar, Runnable submitFunc, int maxLength, Runnable onFocus, CharacterConverter characterConverter, float x, float y, float w) {
		super(Align.BL, x, y);
		this.f = f;
		this.scale = scale;
		this.isValidChar = isValidChar;
		this.submitFunc = submitFunc;
		this.onFocus = onFocus;
		this.characterConverter = characterConverter;
		
		this.currentString = new ArrayList<>(maxLength);
		this.maxLength = maxLength;
		
		this.w = w;
		this.h = BORDER_WIDTH*2 + INNER_PADDING*2 + f.getHeight(scale);
	}
	
	@Override
	public void update(double dt) {
		super.update(dt);
		
		this.time += dt;
	}
	
	@Override
	public void render(IRenderer r) {
		if (enabled) {
			borderColour = ColorUtil.WHITE;
		} else {
			borderColour = ColorUtil.DARK_GREY;
		}
		
		String s = getString();
		int trucLen = getTruncStringLen();
		String trunc = getTruncatedString();
		
		// Draw the outer box (also acts as the border)
		r.drawBox(Align.BL, x, y, w, h, borderColour);
		// Draw the inner box
		r.drawBox(Align.BL, x+ BORDER_WIDTH, y+ BORDER_WIDTH, w-2* BORDER_WIDTH, h-2* BORDER_WIDTH, boxColour);
		// Draw the current string
		r.drawText(f, trunc, Align.BL, false, x + BORDER_WIDTH + INNER_PADDING, y + BORDER_WIDTH + INNER_PADDING, scale);
		// Draw the cursor
		if ((int) (time * 2) % 2 == 0 && cursorPos <= trucLen && enabled) {
			// Calculate cursor position
			FontAdvancer fa = f.getAdvancer(x + BORDER_WIDTH + INNER_PADDING + 1.0f, 0.0f, scale);
			
			for (int i = 0; i < cursorPos;) {
				int c = s.codePointAt(i);
				fa.advance(c);
				i += Character.charCount(c);
			}
			
			float dx = fa.getX();
			r.drawBox(Align.BL, dx, y + BORDER_WIDTH + 5.0f, 3.0f, f.getHeight(scale), ColorUtil.WHITE);
			
			fa.free();
		}
	}

	/**
	 * Prints output for debug
	 * @param msg The message
	 */
	private void out(String msg) {
		System.out.println("TextEntry Info: " + msg);
	}
	
	private void modified() {
		time = 0.0f;
	}
	
	@Override
	public void handleChar(char c) {
		if (!enabled) {
			return;
		}
		c = characterConverter.convert(c);
		
		if (!this.isValidChar.test(c)) {
			out("'" + c + "' is not a valid char.");
		} else if (currentString.size() >= maxLength) {
			out("String is at max length: " + getStringWithPos());
		} else {
			// Insert at i
			this.currentString.add(cursorPos++, c);
			out("Add '" + c + "' @ " + (cursorPos-1) + ": " + getStringWithPos());
			modified();
		}
	}
	
	@Override
	public void handleKey(int key, int scancode, int action, int mods) {
		if (!enabled) {
			return;
		}
		if (action == GLFW_PRESS || action == GLFW_REPEAT) {
			if (key == GLFW_KEY_LEFT) {
				if (cursorPos > 0) {
					cursorPos--;
					modified();
				}
				out("Cursor <-  : " + getStringWithPos());
			} else if (key == GLFW_KEY_RIGHT) {
				if (cursorPos < currentString.size()) {
					cursorPos++;
					modified();
				}
				out("Cursor ->  : " + getStringWithPos());
			} else if (key == GLFW_KEY_DELETE) {
				if (cursorPos < currentString.size()) {
					currentString.remove(cursorPos);
					modified();
				}
				out("Delete     : " + getStringWithPos());
			} else if (key == GLFW_KEY_BACKSPACE) {
				if (cursorPos > 0) {
					currentString.remove(--cursorPos);
					modified();
				}
				out("Backspace  : " + getStringWithPos());
			} else if (key == GLFW_KEY_HOME || key == GLFW_KEY_PAGE_UP) {
				cursorPos = 0;
				modified();
				out("Cursor Home: " + getStringWithPos());
			} else if (key == GLFW_KEY_END || key == GLFW_KEY_PAGE_DOWN) {
				cursorPos = currentString.size();
				modified();
				out("Cursor End : " + getStringWithPos());
			} else if (key == GLFW_KEY_ENTER) {
				out("Submit: '" + getString() + "'");
				submitFunc.run();
			} else if (key == GLFW_KEY_V && mods == GLFW_MOD_CONTROL) {
				// Paste
				try {
					String s = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					if (s != null) {
						// Paste string into box
						for (int i = 0; i < s.length(); i++) {
							char c = s.charAt(i);
							handleChar(c);
						}
					}
				} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
					// Ignore
				}
			}
		}
	}
	
	@Override
	protected void onDefault() {}
	
	@Override
	protected void onHover() {}
	
	@Override
	protected void onPressed() {}
	
	@Override
	public void onClicked() {
		float mx = getMouseX();
		float my = getMouseY();
		
		float tpad = INNER_PADDING+BORDER_WIDTH;
		float dx = mx - x - tpad;
		float dy = my - y - tpad;
		
		if (dx < 0 || dx > w-tpad || dy < 0 || dy > h-tpad) {
			// Not clicked
			return;
		}
		
		onFocus.run();
		
		FontAdvancer fa = f.getAdvancer(0.0f, 0.0f, scale);
		
		float xPrev = 0.0f;
		String s = this.getString();
		for (int i = 0; i < s.length();) {
			int c = s.codePointAt(i);
			
			STBTTAlignedQuad q = fa.advance(c);
			if (q == null)
				continue;
			
			float nx = fa.getX();
			float mid = (nx + xPrev) / 2.0f;
			if (dx < mid) {
				cursorPos = i;
				modified();
				break;
			}
			if (dx < nx) {
				cursorPos = i + 1;
				modified();
				break;
			}
			
			xPrev = fa.getX();
			i += Character.charCount(c);
		}
		
		fa.free();
	}

	/**
	 * Gets the string with a position for the text entry cursor
	 * @return The string
	 */
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

	/**
	 * Gets the current string
	 * @return The string
	 */
	public String getString() {
		StringBuilder s = new StringBuilder(currentString.size());
		for (Character c : currentString)
			s.append(c);
		return s.toString();
	}

	/**
	 * Gets the width of the string
	 * @param num The number of characters
	 * @return The length (float)
	 */
	private float getWidthTruncatedString(int num) {
		FontAdvancer fa = f.getAdvancer(0.0f, 0.0f, scale);
		for (int i = 0; i < num; i++) {
			fa.advance(currentString.get(i));
		}
		if (num <= currentString.size())
		for (int i = 0; i < 3; i++)
			fa.advance((int)'.');
		float w = fa.getX();
		fa.free();
		return w;
	}

	/**
	 * Gets the inner width of the text entry
	 * @return The width
	 */
	private float getInnerWidth() {
		return w - BORDER_WIDTH*2 - INNER_PADDING*2;
	}

	/**
	 * Returns the number of characters in the string
	 * @return The number of characters
	 */
	private int getTruncStringLen() {
		float maxWidth = getInnerWidth();
		for (int num = currentString.size(); num >= 0; num--) {
			float tw = getWidthTruncatedString(num);
			if (tw < maxWidth)
				return num;
		}
		return 0;
	}

	/**
	 * Gets the truncated string
	 * @return The truncated string
	 */
	public String getTruncatedString() {
		float maxWidth = getInnerWidth();
		float tw = getWidthTruncatedString(currentString.size());
		if (tw < maxWidth) {
			return getString();
		} else {
			// Get max num chars to display
			float num = getTruncStringLen();
			
			// Truncate to i
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < num; i++) {
				b.append(currentString.get(i));
			}
			for (int i = 0; i < 3; i++) {
				b.append('.');
			}
			return b.toString();
		}
	}
	
	@Override
	public float getWidth() {
		return w;
	}
	
	@Override
	public float getHeight() {
		return h;
	}

	/**
	 * Set the width of the text entry
	 * @param width The width
	 */
	public void setWidth(float width) {
		this.w = width;
	}

	/**
	 * Set the height of the text entry
	 * @param height The height
	 */
	public void setHeight(float height) {
		this.h = height;
	}

	/**
	 * Sets whether the text entry component is enabled
	 * @param enabled The boolean to set it to
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns whether the text entry is enabled
	 * @return Is it enabled?
	 */
	public boolean isEnabled() {
		return enabled;
	}
}

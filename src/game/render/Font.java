package game.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

import java.nio.ByteBuffer;

import game.ColorUtil;
import game.Util;
import org.joml.Vector4f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Font {
	
	private static final int FIRST_CHAR = 32;
	private static final int NUM_CHARS = 96;
	
	private STBTTFontinfo fontInfo;
	private STBTTBakedChar.Buffer cdata;
	
	private HashMap<Integer, Integer> charToIndex;
	
	private Texture text;
	
	private int BITMAP_W = 512;
	private int BITMAP_H = 512;
	
	private int[] ascent = new int[1];
	private int[] descent = new int[1];
	private int[] lineGap = new int[1];
	
	private final int pixelHeight;

	/**
	 * Constructs a font using the given data (default pixel height of 64)
	 * @param data The data
	 */
	public Font(byte[] data) {
		this(data, 64);
	}

	/**
	 * Constructs a font using the given data and a pixel height
	 * @param data The data
	 * @param _pixelHeight The pixel height
	 */
	public Font(byte[] data, int _pixelHeight) {
		ByteBuffer ttf = MemoryUtil.memAlloc(data.length);
		ttf.put(data);
		ttf.flip();
		
		this.pixelHeight = _pixelHeight;
		
		charToIndex = new HashMap<>();
		
		for (int i = 0; i < NUM_CHARS; i++) {
			charToIndex.put(i + FIRST_CHAR, i);
		}
		
		fontInfo = STBTTFontinfo.malloc();
		cdata = STBTTBakedChar.malloc(NUM_CHARS);
		
		stbtt_InitFont(fontInfo, ttf);
		ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
		stbtt_BakeFontBitmap(ttf, pixelHeight, bitmap, BITMAP_W, BITMAP_H, FIRST_CHAR, cdata);
		
		bitmap.rewind();
		
		ByteBuffer rgba = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H * 4);
		
		for (int i = 0; i < BITMAP_W*BITMAP_H; i++) {
			rgba.put((byte) -1);
			rgba.put((byte) -1);
			rgba.put((byte) -1);
			rgba.put(bitmap.get());
		}
		rgba.rewind();
		
		text = new Texture(rgba, BITMAP_W, BITMAP_H, GL_RGBA);
		
		MemoryUtil.memFree(rgba);
		MemoryUtil.memFree(bitmap);
		ttf.rewind();
		MemoryUtil.memFree(ttf);
		
		stbtt_GetFontVMetrics(fontInfo, ascent, descent, lineGap);
	}

	/**
	 * Gets the texture produced
	 * @return The texture
	 */
	public Texture getTexture() {
		return this.text;
	}

	/**
	 * Returns whether the char is cached
	 * @param c The char
	 * @return Is the char cached
	 */
	private boolean isCharCached(int c) {
		return charToIndex.containsKey(c);
	}

	/**
	 * Get the index of a given char
	 * @param c The character
	 * @return The index
	 */
	private int getCharIndex(int c) {
		return charToIndex.getOrDefault(c, -1);
	}

	/**
	 * Get the width of a string given a string and a scale
	 * @param s The string
	 * @param scale The scale
	 * @return The length (float)
	 */
	public float getWidth(String s, float scale) {
		FontAdvancer fa = getAdvancer(0, 0, scale);
		
		for (int i = 0; i < s.length();) {
			int c = s.codePointAt(i);
			
			fa.advance(c);
			
			i += Character.charCount(c);
		}
		
		float w = fa.getX();
		fa.free();
		return w;
	}

	/**
	 * Gets the height of the font given the scale
	 * @param scale The scale
	 * @return The height
	 */
	public float getHeight(float scale) {
//		return 0.0f;
		return pixelHeight * scale;
//		return (ascent.get(0) - descent.get(0)) * scale;
	}

	/**
	 * Gets the font advancer given all of the font information
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param scale The scale
	 * @return The font advancer
	 */
	public FontAdvancer getAdvancer(float x, float y, float scale) {
		return new FontAdvancer(scale, this::isCharCached, this::getCharIndex, cdata, x, y, BITMAP_W, BITMAP_H);
	}

	/**
	 * Render a given string
	 * @param r The renderer
	 * @param s The string
	 * @param fromBaseline The baseline
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param scale The scale
	 * @param color The colour of the text
	 */
	public void render(Renderer r, String s, boolean fromBaseline, float x, float y, float scale, Vector4f color) {
		if (Util.isDebugRenderMode())
			r.drawBox(Align.BL, x, y, getWidth(s, scale), getHeight(scale), ColorUtil.GREEN);
		
		if (!fromBaseline) {
			// Position so that y is at the bottom, not baseline
			float proportion = ((float)-descent[0]) / (float)(ascent[0] - descent[0]);
			y += proportion * getHeight(scale);
		}
		
		FontAdvancer fa = getAdvancer(x, 0.0f, scale);
		
		for (int i = 0; i < s.length();) {
			int c = s.codePointAt(i);
			
			STBTTAlignedQuad q = fa.advance(c);
			if (q == null)
				continue;
			
			float w = (q.x1() - q.x0())*scale;
			float h = (q.y1() - q.y0())*scale;
			
			if (Util.isDebugRenderMode())
				r.drawBox(Align.BL, q.x0(), y - q.y0()*scale - h, w, h, ColorUtil.PINK);
			
			r.drawTextureUV(text, Align.BL, q.x0(), y - q.y0()*scale - h, w, h, 0.0f, q.s0(), q.t0(), q.s1(), q.t1(), color);
			
			i += Character.charCount(c);
		}
		
		fa.free();
	}
}

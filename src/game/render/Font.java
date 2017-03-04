package game.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

import java.nio.ByteBuffer;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;
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
	
	public Font(String path) {
		this(path, 64);
	}
	
	public Font(String path, int _pixelHeight) {
		this.pixelHeight = _pixelHeight;
		
		charToIndex = new HashMap<>();
		
		for (int i = 0; i < NUM_CHARS; i++) {
			charToIndex.put(i + FIRST_CHAR, i);
		}
		
		fontInfo = STBTTFontinfo.malloc();
		cdata = STBTTBakedChar.malloc(NUM_CHARS);
		
		try {
			RandomAccessFile file = new RandomAccessFile(path, "r");
			FileChannel channel = file.getChannel();
			ByteBuffer ttf = ByteBuffer.allocateDirect((int)channel.size());
			channel.read(ttf);
			file.close();
			ttf.rewind();
			
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
			
			stbtt_GetFontVMetrics(fontInfo, ascent, descent, lineGap);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Texture getTexture() {
		return this.text;
	}
	
	private boolean isCharCached(int c) {
		return charToIndex.containsKey(c);
	}
	
	private int getCharIndex(int c) {
		return charToIndex.getOrDefault(c, -1);
	}
	
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
	
	public float getHeight(float scale) {
//		return 0.0f;
		return pixelHeight * scale;
//		return (ascent.get(0) - descent.get(0)) * scale;
	}
	
	public FontAdvancer getAdvancer(float x, float y, float scale) {
		return new FontAdvancer(scale, this::isCharCached, this::getCharIndex, cdata, x, y, BITMAP_W, BITMAP_H);
	}
	
	public void render(Renderer r, String s, boolean fromBaseline, float x, float y, float scale) {
		if (!fromBaseline) {
			// Position so that y is at the bottom, not baseline
			float descentProportion = (float)-descent[0] / (float)(ascent[0] - descent[0]);
			y += descentProportion * pixelHeight * scale;
		}
		
		FontAdvancer fa = getAdvancer(x, 0.0f, scale);
		
		for (int i = 0; i < s.length();) {
			int c = s.codePointAt(i);
			
			STBTTAlignedQuad q = fa.advance(c);
			if (q == null)
				continue;
			
			float w = (q.x1() - q.x0())*scale;
			float h = (q.y1() - q.y0())*scale;
			r.drawTextureUV(text, Align.TL, q.x0(), y - q.y0(), w, h, q.s0(), q.t0(), q.s1(), q.t1());
			
			i += Character.charCount(c);
		}
		
		fa.free();
	}
}

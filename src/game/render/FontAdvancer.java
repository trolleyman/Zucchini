package game.render;

import static org.lwjgl.stb.STBTruetype.*;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;

import java.util.function.Function;
import java.util.function.Predicate;

public class FontAdvancer {
	private final float scale;
	private final Predicate<Integer> isCharCached;
	private final Function<Integer, Integer> getCharIndex;
	private final STBTTBakedChar.Buffer cdata;
	
	private final int BITMAP_W;
	private final int BITMAP_H;
	
	private final float[] xBuf = new float[] {0.0f};
	private final float[] yBuf = new float[] {0.0f};
	
	private STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
	
	protected FontAdvancer(float scale, Predicate<Integer> isCharCached, Function<Integer, Integer> getCharIndex, STBTTBakedChar.Buffer cdata, float x, float y, int BITMAP_W, int BITMAP_H) {
		this.scale = scale;
		this.isCharCached = isCharCached;
		this.getCharIndex = getCharIndex;
		this.cdata = cdata;
		
		this.xBuf[0] = x;
		this.yBuf[0] = y;
		
		this.BITMAP_W = BITMAP_W;
		this.BITMAP_H = BITMAP_H;
	}
	
	/**
	 * Advances the FontAdvancer by the specified character
	 * @param c The codepoint
	 * @return null if the character is not in the cache, otherwise returns the aligned quad to place the character at
	 */
	public STBTTAlignedQuad advance(int c) {
		float dx;
		if (isCharCached.test(c)) {
			float prevX = xBuf[0];
			stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, getCharIndex.apply(c), xBuf, yBuf, q, true);
			dx = xBuf[0] - prevX;
			xBuf[0] = prevX + dx * scale;
			return q;
		} else {
			return null;
		}
	}
	
	public float getX() {
		return xBuf[0];
	}
	
	public float getY() {
		return yBuf[0];
	}
	
	public void free() {
		q.free();
	}
}

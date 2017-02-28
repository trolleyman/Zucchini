package game.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import sun.nio.ch.IOUtil;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.BufferUtils.*;

public class Font {

	private STBTTFontinfo fontInfo;
	private STBTTBakedChar.Buffer cdata;
	private Texture text;
	
	int BITMAP_W = 512;
	int BITMAP_H = 512;
	
	IntBuffer ascent = MemoryUtil.memAllocInt(1);
	IntBuffer descent = MemoryUtil.memAllocInt(1);
	IntBuffer lineGap = MemoryUtil.memAllocInt(1);
	
	private final int pixelHeight;
	
	public Font(String path) {
		this(path, 64);
	}
	
	public Font(String path, int _pixelHeight) {
		this.pixelHeight = _pixelHeight;
		
		fontInfo = STBTTFontinfo.malloc();
		cdata = STBTTBakedChar.malloc(96);
		
		try {
			RandomAccessFile file = new RandomAccessFile(path, "r");
			FileChannel channel = file.getChannel();
			ByteBuffer ttf = ByteBuffer.allocateDirect((int)channel.size());
			channel.read(ttf);
			file.close();
			ttf.rewind();
			
			stbtt_InitFont(fontInfo, ttf);
			ByteBuffer bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);
			stbtt_BakeFontBitmap(ttf, pixelHeight, bitmap, BITMAP_W, BITMAP_H, 32, cdata);
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
	
	public float getWidth(String s, float scale) {
		STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
		
		xBuf[0] = 0.0f;
		
		int asciiPrev = -1;
		for (int i=0; i<s.length(); i++) {
			int ascii = (int) s.charAt(i);
			ascii = ascii - 32;
			
			float dx;
			if (ascii < cdata.limit()){
				float prevX = xBuf[0];
				stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, ascii, xBuf, yBuf, q, true);
				
				if (asciiPrev != -1) {
					// Advance kerning
					int kerningAdvance = stbtt_GetCodepointKernAdvance(fontInfo, asciiPrev, ascii);
					xBuf[0] += kerningAdvance;
				}
				
				dx = xBuf[0] - prevX;
				xBuf[0] = prevX + dx * scale;
			}
			
			asciiPrev = ascii;
		}
		
		q.free();
		return xBuf[0];
	}
	
	public float getHeight(float scale) {
//		return 0.0f;
		return pixelHeight * scale;
//		return (ascent.get(0) - descent.get(0)) * scale;
	}
	
	private float[] xBuf = new float[1];
	private float[] yBuf = new float[1];
	
	public void render(Renderer r, String s, boolean fromBaseline, float x, float y, float scale) {
		if (!fromBaseline) {
			// Position so that y is at the bottom, not baseline
			float descentProportion = (float)-descent.get(0) / (float)(ascent.get(0) - descent.get(0));
			y += descentProportion * pixelHeight * scale;
		}
		
		STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
		
		xBuf[0] = x;
		yBuf[0] = 0.0f;
		
		for (int i=0; i<s.length(); i++) {
			int ascii = (int) s.charAt(i);
			ascii = ascii - 32;
			
			float dx;
			if (ascii < cdata.limit()){
				float prevX = xBuf[0];
				stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, ascii, xBuf, yBuf, q, true);
				dx = xBuf[0] - prevX;
				xBuf[0] = prevX + dx * scale;
			} else {
				System.out.println("Error: Character "+s.charAt(i)+" in string not recognised");
				continue;
			}
			
			float w = (q.x1() - q.x0())*scale;
			float h = (q.y1() - q.y0())*scale;
			r.drawTextureUV(text, Align.TL, q.x0(), y - q.y0(), w, h, q.s0(), q.t0(), q.s1(), q.t1());
		}
		
		q.free();
	}
}

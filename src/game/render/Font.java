package game.render;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
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

	STBTTBakedChar.Buffer cdata;
	Texture text;
	
	public Font(String path) {
		
		cdata = STBTTBakedChar.malloc(96);
		
		try {
			int w = 512;
			int h = 512;
			
			RandomAccessFile file = new RandomAccessFile(path, "r");
			FileChannel channel = file.getChannel();
			ByteBuffer ttf = ByteBuffer.allocateDirect((int)channel.size());
			channel.read(ttf);
			file.close();
			ttf.rewind();

			ByteBuffer bitmap = MemoryUtil.memAlloc(w * h);
			stbtt_BakeFontBitmap(ttf, 32, bitmap, w, h, 32, cdata);
			bitmap.rewind();
			
			ByteBuffer rgba = MemoryUtil.memAlloc(w * h * 4);
			
			for (int i = 0; i < w*h; i++) {
				rgba.put((byte) -1);
				rgba.put((byte) -1);
				rgba.put((byte) -1);
				rgba.put(bitmap.get());
			}
			
			rgba.rewind();
			
			text = new Texture(rgba, w, h, GL_RGBA);
			
			MemoryUtil.memFree(rgba);
			MemoryUtil.memFree(bitmap);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Texture getTexture() {
		return this.text;
	}
}

package game.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * Represents an OpenGL texture. See <a target="_top" href="https://open.gl/textures">here</a> for an introduction.
 * 
 * @author Callum
 */
public class Texture {
	/** The width of the texture in pixels */
	private int w;
	/** The height of the texture in pixels */
	private int h;
	/** The OpenGL texture ID */
	private int texID;
	
	/**
	 * Constructs a new Texture by reading the file specified.
	 * @param bdata The texture data.
	 */
	public Texture(byte[] bdata, String name) {
		int[] wArr = new int[1];
		int[] hArr = new int[1];
		int[] compArr = new int[1];
		
		// Decode the image
		ByteBuffer idata = MemoryUtil.memAlloc(bdata.length);
		idata.put(bdata);
		idata.flip();
		
		ByteBuffer data = stbi_load_from_memory(idata, wArr, hArr, compArr, 4);
		if (data == null)
			throw new RuntimeException("Failed to load texture: " + name + ": " + stbi_failure_reason());
		data.rewind();
		
		idata.rewind();
		MemoryUtil.memFree(idata);
		
		w = wArr[0];
		h = hArr[0];
		
		this.loadTexture(data, w, h, GL_RGBA);
		MemoryUtil.memFree(data);
	}
	
	public Texture(ByteBuffer data, int _w, int _h, int format) {
		this.w = _w;
		this.h = _h;
		this.loadTexture(data, w, h, format);
	}
	
	private void loadTexture(ByteBuffer data, int w, int h, int format) {
		// Upload to OpenGL
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, format, GL_UNSIGNED_BYTE, data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		/*
		System.out.print("Data = [");
		byte[] pixel = new byte[4];
		data.rewind();
		for (int i = 0; i < 4; i++) {
			System.out.print("0x");
			data.get(pixel);
			for (byte b : pixel) {
				System.out.print(String.format("%02X", b));
			}
			System.out.print(", ");
		}
		data.rewind();
		System.out.println(" ... ]");//*/
	}
	
	/**
	 * Returns the OpenGL texture ID
	 */
	public int getTextureID() {
		return this.texID;
	}
	
	/**
	 * Frees OpenGL resources related to this texture.
	 */
	public void destroy() {
		glDeleteTextures(this.texID);
	}
	
	/**
	 * Returns the width in pixels of this texture.
	 */
	public int getWidth() {
		return w;
	}
	
	/**
	 * Returns the height in pixels of this texture.
	 */
	public int getHeight() {
		return h;
	}
}

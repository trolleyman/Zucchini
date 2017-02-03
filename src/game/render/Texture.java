package game.render;

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
	 * @param path The texture location.
	 */
	public Texture(String path) {
		int[] wArr = new int[1];
		int[] hArr = new int[1];
		int[] compArr = new int[1];
		
		// Decode the image
		ByteBuffer data = stbi_load(path, wArr, hArr, compArr, 4);
		if (data == null)
			throw new RuntimeException("Failed to load texture: " + path + ": " + stbi_failure_reason());
		data.rewind();
		
		w = wArr[0];
		h = hArr[0];
		
		// Upload to OpenGL
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
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

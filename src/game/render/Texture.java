package game.render;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
	private ByteBuffer data;
	private int w;
	private int h;
	private int texID;
	
	public Texture(String path) {
		int[] wArr = new int[1];
		int[] hArr = new int[1];
		int[] compArr = new int[1];
		
		// Decode the image
		data = stbi_load(path, wArr, hArr, compArr, 3);
		if (data == null)
			throw new RuntimeException("Failed to load texture: " + path + ": " + stbi_failure_reason());
		data.rewind();
		
		w = wArr[0];
		h = hArr[0];
		
		// Upload to OpenGL
		int texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		/*System.out.print("Data = [");
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
		System.out.println(" ... ]");*/
	}
	
	public int getTextureID() {
		return this.texID;
	}
	
	public void destroy() {
		glDeleteTextures(this.texID);
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
}

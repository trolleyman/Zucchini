package game.render;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Image {
	private ByteBuffer data;
	private int w;
	private int h;
	private int texID;
	
	public Image(String path) {
		int[] wArr = new int[1];
		int[] hArr = new int[1];
		int[] compArr = new int[1];
		
		// Decode the image
		data = stbi_load(path, wArr, hArr, compArr, 4);
		if (data == null)
			throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
		
		w = wArr[0];
		h = hArr[0];
		
		// Upload to OpenGL
		int texID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, texID);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		System.out.println("Loaded image: " + path);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, this.texID);
	}
	
	public void destroy() {
		glDeleteTextures(texID);
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
}

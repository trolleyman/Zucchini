package game.render;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;


import org.lwjgl.BufferUtils;

public class Image {
	public ByteBuffer data;
	public int w;
	public int h;
	public int comp;
	public int texID;
	
	public Image(String path) {
		ByteBuffer imageBuffer;
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(path));
			imageBuffer = ByteBuffer.wrap(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);

		// Use info to read image metadata without decoding the entire image.
		// We don't need this for this demo, just testing the API.
		if ( !stbi_info_from_memory(imageBuffer, w, h, comp) )
			throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());

		System.out.println("Image width: " + w.get(0));
		System.out.println("Image height: " + h.get(0));
		System.out.println("Image components: " + comp.get(0));
		System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

		// Decode the image
		data = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
		if ( data == null )
			throw new RuntimeException("Failed to load image: " + stbi_failure_reason());

		this.w = w.get(0);
		this.h = h.get(0);
		this.comp = comp.get(0);
		
		// Upload to OpenGLs
		int texID = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, texID);

		if ( comp.get(0) == 3 ) {
			if ( (w.get(0) & 3) != 0 )
				glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w.get(0) & 1));
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(0), h.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
		} else {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		glEnable(GL_TEXTURE_2D);
	}
}

package game.render;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import game.Util;

/**
 * Represents the currently loaded textures.
 * 
 * @author Callum
 */
public class TextureBank {
	/** Textures loaded */
	private HashMap<String, Texture> textures;
	
	/**
	 * Constructs a new TextureBank instance. Images are loaded from the img/ directory, relative to
	 * the base dir. (See {@link game.Util#getBasePath() Util#getBasePath()})
	 */
	public TextureBank() {
		System.out.println("Loading textures...");
		textures = new HashMap<>();
		
		// Find all .png files in directory "img/"
		String baseDir = Util.getBasePath();
		Path imgsDirPath = Paths.get(baseDir, "img");
		File imgsDir = imgsDirPath.toFile();
		File[] imgFiles = imgsDir.listFiles((dir, name) -> {
			return name.endsWith(".png");
		});
		
		if (imgFiles == null) {
			System.out.println("No texture loaded.");
			return;
		}
		
		for (File file : imgFiles) {
			Texture i = new Texture(file.toString());
			String name = file.getName();
			System.out.println("Loaded texture: " + name);
			textures.put(name, i);
		}
		System.out.println(textures.size() + " texture(s) loaded.");
	}
	
	/**
	 * Frees all resources associated with this TextureBank instance.
	 */
	public void destroy() {
		for (Texture i : textures.values()) {
			i.destroy();
		}
	}
	
	/**
	 * Returns the texture associated with the name specified
	 * @param name The name of the texture requested
	 * @return {@code null} if the texture could not be located
	 */
	public Texture getTexture(String name) {
		Texture t = textures.get(name);
		if (t == null)
			System.err.println("Warning: getTexture of unknown texture: " + name);
		return t;
	}
}

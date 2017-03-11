package game.render;

import java.util.HashMap;
import java.util.Map.Entry;

import game.Resources;
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
	 * the base directory.
	 */
	public TextureBank() {
		System.out.println("Loading textures...");
		textures = new HashMap<>();
		
		// Find all .png files in directory "resources/img/"
		HashMap<String, byte[]> imgs = Resources.getImages();
		
		if (imgs.size() == 0) {
			System.out.println("No textures loaded.\n");
			return;
		}
		
		for (Entry<String, byte[]> e : imgs.entrySet()) {
			String name = e.getKey();
			Texture t = new Texture(e.getValue(), name);
			System.out.println("Loaded texture: " + name);
			textures.put(name, t);
		}
		System.out.println(textures.size() + " texture(s) loaded.\n");
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

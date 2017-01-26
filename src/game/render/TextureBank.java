package game.render;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import game.Util;

public class TextureBank {
	// Images loaded
	private HashMap<String, Texture> textures;
	
	public TextureBank() {
		System.out.println("Loading textures...");
		textures = new HashMap<>();
		
		// Find all .png files in directory "img/"
		Path baseDir = Util.getBasePath();
		Path imgsDirPath = Paths.get(baseDir.toString(), "img");
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
	
	public void destroy() {
		for (Texture i : textures.values()) {
			i.destroy();
		}
	}
	
	public Texture getTexture(String name) {
		Texture i = textures.get(name);
		if (i == null)
			System.err.println("Warning: getTexture of unknown texture: " + name);
		return i;
	}
}

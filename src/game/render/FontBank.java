package game.render;

import game.Util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Holds all of the fonts that are used in the program
 */
public class FontBank {
	private HashMap<String, Font> fonts;
	
	/**
	 * Loads all of the fonts in the resources/fonts/ directory.
	 */
	public FontBank() {
		System.out.println("Loading fonts...");
		fonts = new HashMap<>();
		
		// Find all .png files in directory "resources/fonts/"
		Path fontsDirPath = Paths.get(Util.getResourcesDir(), "fonts");
		File fontsDir = fontsDirPath.toFile();
		File[] fontFiles = fontsDir.listFiles((dir, name) -> name.endsWith(".ttf"));
		
		if (fontFiles == null) {
			System.out.println("No fonts loaded.");
			return;
		}
		
		for (File file : fontFiles) {
			Font f = new Font(file.toString());
			String name = file.getName();
			System.out.println("Loaded font: " + name);
			fonts.put(name, f);
		}
		System.out.println(fonts.size() + " font(s) loaded.");
	}
	
	public Font getFont(String name) {
		return fonts.get(name);
	}
}

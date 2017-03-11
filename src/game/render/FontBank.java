package game.render;

import game.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
		
		HashMap<String, byte[]> fontsData = Util.getFonts();
		
		if (fontsData.size() == 0) {
			System.out.println("No fonts loaded.\n");
			return;
		}
		
		for (Map.Entry<String, byte[]> e : fontsData.entrySet()) {
			Font f = new Font(e.getValue());
			String name = e.getKey();
			System.out.println("Loaded font: " + name);
			fonts.put(name, f);
		}
		System.out.println(fonts.size() + " font(s) loaded.\n");
	}
	
	public Font getFont(String name) {
		return fonts.get(name);
	}
}

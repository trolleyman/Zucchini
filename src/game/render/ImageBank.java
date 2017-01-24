package game.render;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ImageBank {
	// Images loaded
	private HashMap<String, Image> images;
	
	public ImageBank() {
		images = new HashMap<>();
		
		// Find all .png files in directory "img/"
		Path baseDir = Paths.get(".").toAbsolutePath();
		Path imgsDirPath = Paths.get(baseDir.toString(), "img");
		File imgsDir = imgsDirPath.toFile();
		File[] imgFiles = imgsDir.listFiles((dir, name) -> {
			return name.endsWith(".png");
		});
		
		if (imgFiles == null) {
			System.out.println("No images loaded.");
			return;
		}
		
		for (File file : imgFiles) {
			Image i = new Image(file.toString());
			images.put(file.getName(), i);
		}
		System.out.println(images.size() + " image(s) loaded.");
	}
	
	public void destroy() {
		for (Image i : images.values()) {
			i.destroy();
		}
	}
	
	public Image getImage(String name) {
		return this.images.get(name);
	}
}

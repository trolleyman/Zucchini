package game;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class to load resources
 *
 * @author Callum
 */
public class Resources {
	public static HashMap<String, byte[]> getFiles(String base) {
		return getFiles(base, (s) -> true);
	}
	
	public static HashMap<String, byte[]> getFiles(String base, Predicate<String> pattern) {
		try {
			HashMap<String, byte[]> ret = new HashMap<>();
			File localDir = new File("./resources/" + base);
			if (localDir.exists() && localDir.isDirectory()) {
				// Get files from local file system
				File[] files = localDir.listFiles();
				if (files != null) {
					for (File f : files) {
						if (!pattern.test(f.getName())) // Only add files that match the pattern
							continue;
						byte[] bytes = Files.readAllBytes(f.toPath());
						ret.put(f.getName(), bytes);
					}
				}
			} else {
				// Load from JAR
				CodeSource codeSource = Util.class.getProtectionDomain().getCodeSource();
				
				if (codeSource == null)
					throw new RuntimeException("Could not locate resources directory: " + base);
				
				URL jar = codeSource.getLocation();
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				
				ZipEntry ze;
				while ((ze = zip.getNextEntry()) != null) {
					String totalBase = "resources/" + base + '/';
					String name = ze.getName();
					if (!name.startsWith(totalBase))
						continue;
					
					// Get filename of resource
					String filename = name.substring(totalBase.length());
					if (filename.length() == 0 || !pattern.test(filename))
						continue;
					
					byte[] buffer = new byte[1024];
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					int len;
					while ((len = zip.read(buffer)) > 0) {
						os.write(buffer, 0, len);
					}
					
					byte[] data = os.toByteArray();
					// System.out.println(String.format("Loaded file from zip: %s (%d bytes)", name, data.length));
					ret.put(filename, data);
				}
				
				zip.close();
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static HashMap<String, byte[]> getImages() {
		return getFiles("img", (s) -> s.endsWith(".png"));
	}
	
	public static HashMap<String, byte[]> getFonts() {
		return getFiles("fonts", (s) -> s.endsWith(".ttf"));
	}
	
	public static HashMap<String, byte[]> getAudioFiles() {
		return getFiles("audio_assets", (s) -> s.endsWith(".wav"));
	}
	
	public static HashMap<String, byte[]> getShaders() {
		return getFiles("shader");
	}
}

package test;

import game.Resources;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ResourcesTest {
	@Test
	public void getFiles() {
		HashMap<String, byte[]> res = Resources.getFiles("img");
		assertTrue(res.size() > 0);
		res = Resources.getFiles("img", (s) -> s.endsWith(".png"));
		assertTrue(res.size() > 0);
		for (Map.Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".png"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	public void getImages() {
		HashMap<String, byte[]> res = Resources.getImages();
		assertTrue(res.size() > 0);
		for (Map.Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".png"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	public void getFonts() {
		HashMap<String, byte[]> res = Resources.getFonts();
		assertTrue(res.size() > 0);
		for (Map.Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".ttf"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	public void getAudioFiles() {
		HashMap<String, byte[]> res = Resources.getAudioFiles();
		assertTrue(res.size() > 0);
		for (Map.Entry<String, byte[]> e : res.entrySet()) {
			assertTrue(e.getKey().endsWith(".wav"));
			assertTrue(e.getValue().length > 0);
		}
	}
	
	@Test
	public void getShaders() {
		HashMap<String, byte[]> res = Resources.getShaders();
		assertTrue(res.size() > 0);
		boolean vert = false;
		boolean frag = false;
		for (Map.Entry<String, byte[]> e : res.entrySet()) {
			if (e.getKey().endsWith(".vert"))
				vert = true;
			if (e.getKey().endsWith(".frag"))
				frag = true;
		}
		
		assertTrue(vert);
		assertTrue(frag);
	}
}
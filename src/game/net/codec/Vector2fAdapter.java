package game.net.codec;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joml.Vector2f;

import java.io.IOException;

public class Vector2fAdapter extends TypeAdapter<Vector2f> {
	@Override
	public Vector2f read(JsonReader jsonReader) throws IOException {
		String s = jsonReader.nextString();
		int i = s.indexOf(',');
		if (i == -1)
			throw new JsonParseException("Invalid Vector2f: " + s);
		
		String sx = s.substring(0, i);
		String sy = s.substring(i+1);
		
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);
			return new Vector2f(x, y);
		} catch (NumberFormatException e) {
			throw new JsonParseException(e);
		}
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Vector2f v) throws IOException {
		jsonWriter.value("" + v.x + ',' + v.y);
	}
}

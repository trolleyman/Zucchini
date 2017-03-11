package game.net.codec;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joml.Vector3f;

import java.io.IOException;

public class Vector3fAdapter extends TypeAdapter<Vector3f> {
	@Override
	public Vector3f read(JsonReader jsonReader) throws IOException {
		String s = jsonReader.nextString();
		int i = s.indexOf(',');
		if (i == -1)
			throw new JsonParseException("Invalid Vector3f: " + s);
		int j = s.indexOf(',', i+1);
		if (j == -1)
			throw new JsonParseException("Invalid Vector3f: " + s);
		
		String sx = s.substring(0, i);
		String sy = s.substring(i+1, j);
		String sz = s.substring(j+1);
		
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);
			float z = Float.parseFloat(sz);
			return new Vector3f(x, y, z);
		} catch (NumberFormatException e) {
			throw new JsonParseException(e);
		}
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Vector3f v) throws IOException {
		jsonWriter.value("" + v.x + ',' + v.y + ',' + v.z);
	}
}

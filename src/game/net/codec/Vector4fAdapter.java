package game.net.codec;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joml.Vector4f;

import java.io.IOException;

public class Vector4fAdapter extends TypeAdapter<Vector4f> {
	@Override
	public Vector4f read(JsonReader jsonReader) throws IOException {
		String s = jsonReader.nextString();
		int i = s.indexOf(',');
		if (i == -1)
			throw new JsonParseException("Invalid Vector4f: " + s);
		int j = s.indexOf(',', i+1);
		if (j == -1)
			throw new JsonParseException("Invalid Vector4f: " + s);
		int k = s.indexOf(',', j+1);
		if (k == -1)
			throw new JsonParseException("Invalid Vector4f: " + s);
		
		String sx = s.substring(0, i);
		String sy = s.substring(i+1, j);
		String sz = s.substring(j+1, k);
		String sw = s.substring(k+1);
		
		try {
			float x = Float.parseFloat(sx);
			float y = Float.parseFloat(sy);
			float z = Float.parseFloat(sz);
			float w = Float.parseFloat(sw);
			return new Vector4f(x, y, z, w);
		} catch (NumberFormatException e) {
			throw new JsonParseException(e);
		}
	}
	
	@Override
	public void write(JsonWriter jsonWriter, Vector4f v) throws IOException {
		jsonWriter.value("" + v.x + ',' + v.y + ',' + v.z + ',' + v.w);
	}
}

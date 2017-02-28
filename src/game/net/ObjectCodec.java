package game.net;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import game.exception.ProtocolException;
import game.world.Team;
import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.entity.weapon.HandgunBullet;
import org.joml.Vector2f;

import java.io.IOException;
import java.lang.reflect.Type;

public class ObjectCodec {
	private static Gson gson = new GsonBuilder()
			.registerTypeAdapter(Item.class, new AbstractClassAdapter<Item>(Item.class).nullSafe())
			//.registerTypeAdapter(Entity.class, new AbstractClassAdapter<Entity>(Entity.class).nullSafe())
			.create();
	
	private static class AbstractClassAdapter<T> extends TypeAdapter<T> {
		private Class clazz;
		
		public AbstractClassAdapter(Class clazz) {
			this.clazz = clazz;
		}
		
		@Override
		public T read(JsonReader jsonReader) throws IOException {
			jsonReader.beginObject();
			String name = jsonReader.nextName();
			if (!name.equals("type"))
				throw new JsonParseException("No 'type' field.");
			String type = jsonReader.nextString();
			Class c;
			try {
				c = Class.forName(type);
				c.asSubclass(clazz);
			} catch (ClassNotFoundException e) {
				throw new JsonParseException("Class does not exist: '" + type + "'", e);
			} catch (ClassCastException e) {
				throw new JsonParseException("Class '" + type + "' is not subclass of '" + clazz + "'", e);
			}
			String dataName = jsonReader.nextName();
			if (!dataName.equals("data"))
				throw new JsonParseException("No 'data' field.");
			
			@SuppressWarnings("unchecked")
			T ret = (T) gson.getAdapter(c).read(jsonReader);
			jsonReader.endObject();
			return ret;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void write(JsonWriter jsonWriter, T t) throws IOException {
			String typeName = t.getClass().getName();
			
			jsonWriter.beginObject();
			jsonWriter.name("type");
			jsonWriter.value(typeName);
			jsonWriter.name("data");
			TypeAdapter subAdapter = gson.getAdapter(t.getClass());
			subAdapter.write(jsonWriter, t);
			jsonWriter.endObject();
		}
	}
	
	public synchronized static <T> String toJson(T t) {
		return gson.toJson(t);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static <T> T fromJson(String s) {
		return (T) gson.fromJson(s, s.getClass());
	}
	
	public synchronized static <T> T genFromString(String s) throws ProtocolException {
		try {
			JsonElement o = new JsonParser().parse(s);
			Type type = typeForName(get(o, "type"));
			JsonElement data = get(o, "data");
			return gson.fromJson(data, type);
		} catch (IllegalStateException | ClassCastException | JsonParseException e) {
			throw new ProtocolException("Invalid JSON: " + s, e);
		}
	}
	
	public synchronized static <T> String genToString(T t) {
		String name = t.getClass().getName();
		JsonObject o = new JsonObject();
		o.addProperty("type", name);
		o.add("data", gson.toJsonTree(t));
		return o.toString();
	}
	
	private static Type typeForName(final JsonElement typeElem) {
		try {
			return Class.forName(typeElem.getAsString());
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Type not found", e);
		}
	}
	
	private static JsonElement get(final JsonElement e, String name) {
		if (!e.isJsonObject()) {
			throw new JsonParseException("Could not find '" + name + "' in JSON Object");
		} else {
			JsonObject o = e.getAsJsonObject();
			JsonElement member = o.get(name);
			if (member == null) {
				throw new JsonParseException("Could not find '" + name + "' in JSON Object");
			}
			return member;
		}
	}
	
	public static void main(String[] args) throws ProtocolException {
		HandgunBullet b = new HandgunBullet(new Vector2f(2.0f, 3.0f), Team.START_FREE_TEAM, 1.0f);
		String s = ObjectCodec.genToString(b);
		System.out.println("JSON: " + s);
		Entity e = ObjectCodec.genFromString(s);
		boolean val = e instanceof HandgunBullet;
		System.out.println("e instanceof HangunBullet == " + val);
		assert(val);
	}
}
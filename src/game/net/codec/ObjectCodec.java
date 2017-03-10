package game.net.codec;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import game.audio.event.AudioEvent;
import game.exception.ProtocolException;
import game.world.Team;
import game.world.entity.Entity;
import game.world.entity.Item;
import game.world.entity.weapon.HandgunBullet;
import game.world.entity.update.EntityUpdate;
import game.world.update.WorldUpdate;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;

public class ObjectCodec {
	private static final ThreadLocal<Gson> GSON = ThreadLocal.withInitial(() ->
		new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.setLenient()
				.registerTypeAdapter(Vector2f.class, new Vector2fAdapter().nullSafe())
				.registerTypeAdapter(Vector3f.class, new Vector3fAdapter().nullSafe())
				.registerTypeAdapter(Item.class, new AbstractClassAdapter<Item>(Item.class).nullSafe())
				.registerTypeAdapter(Entity.class, new AbstractClassAdapter<Entity>(Entity.class).nullSafe())
				.registerTypeAdapter(EntityUpdate.class, new AbstractClassAdapter<EntityUpdate>(EntityUpdate.class).nullSafe())
				.registerTypeAdapter(AudioEvent.class, new AbstractClassAdapter<AudioEvent>(AudioEvent.class).nullSafe())
				.registerTypeAdapter(WorldUpdate.class, new AbstractClassAdapter<WorldUpdate>(WorldUpdate.class).nullSafe())
				.create()
	);
	
	private static final ThreadLocal<JsonParser> PARSER = ThreadLocal.withInitial(JsonParser::new);
	
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
			T ret = (T) GSON.get().getAdapter(c).read(jsonReader);
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
			TypeAdapter subAdapter = GSON.get().getAdapter(t.getClass());
			subAdapter.write(jsonWriter, t);
			jsonWriter.endObject();
		}
	}
	
	/**
	 * Gets the thread-local Gson instance
	 */
	public static Gson getGson() {
		return GSON.get();
	}
	
	/**
	 * Gets the thread-local JsonParser instance
	 */
	public static JsonParser getParser() {
		return PARSER.get();
	}
	
	private static <T> String genToSting(T t, Class<T> clazz) {
		return GSON.get().toJson(t, clazz);
	}
	private static <T> T genFromString(String s, Class<T> clazz) throws ProtocolException {
		try {
			return GSON.get().fromJson(s, clazz);
		} catch (JsonSyntaxException e) {
			throw new ProtocolException(e);
		}
	}
	
	public static String entityToString(Entity e) {
		return genToSting(e, Entity.class);
	}
	public static Entity entityFromString(String s) throws ProtocolException {
		return genFromString(s, Entity.class);
	}
	
	public static String entityUpdateToString(EntityUpdate e) {
		return genToSting(e, EntityUpdate.class);
	}
	public static EntityUpdate entityUpdateFromString(String s) throws ProtocolException {
		return genFromString(s, EntityUpdate.class);
	}
	
	public static String audioEventToString(AudioEvent e) {
		return genToSting(e, AudioEvent.class);
	}
	public static AudioEvent audioEventFromString(String s) throws ProtocolException {
		return genFromString(s, AudioEvent.class);
	}
	
	public static String worldUpdateToString(WorldUpdate e) {
		return genToSting(e, WorldUpdate.class);
	}
	public static WorldUpdate worldUpdateFromString(String s) throws ProtocolException {
		return genFromString(s, WorldUpdate.class);
	}
	
	public static void main(String[] args) throws ProtocolException {
		HandgunBullet b = new HandgunBullet(new Vector2f(2.0f, 3.0f), Entity.INVALID_ID, Team.START_FREE_TEAM, 1.0f);
		String s = GSON.get().toJson(b, Entity.class);
		System.out.println("JSON: " + s);
		Entity e = GSON.get().fromJson(s, Entity.class);
		boolean val = e instanceof HandgunBullet;
		System.out.println("e instanceof HangunBullet == " + val);
		assert(val);
	}
}
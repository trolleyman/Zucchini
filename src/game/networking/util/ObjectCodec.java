package game.networking.util;

import com.google.gson.*;
import game.audio.event.AudioEvent;
import game.world.entity.Entity;
import game.world.entity.HandgunBullet;
import org.joml.Vector2f;

import java.lang.reflect.Type;

public class ObjectCodec {
	private static Gson gson = new GsonBuilder().create();
	
	public static Entity entityFromString(String s) {
		try {
			JsonElement o = new JsonParser().parse(s);
			Type type = typeForName(get(o, "type"));
			JsonElement data = get(o, "data");
			Entity e = (Entity) gson.fromJson(data, type);
			return e;
		} catch (IllegalStateException e) {
			throw new JsonParseException("JSON Object expected", e);
		} catch (ClassCastException e) {
			throw new JsonParseException("Entity expected", e);
		}
	}
	
	public static String entityToString(Entity e) {
		String name = e.getClass().getName();
		JsonObject o = new JsonObject();
		o.addProperty("type", name);
		o.add("data", gson.toJsonTree(e));
		return o.toString();
	}
	
	public static AudioEvent audioEventFromString(String s) {
		try {
			JsonElement o = new JsonParser().parse(s);
			Type type = typeForName(get(o, "type"));
			JsonElement data = get(o, "data");
			AudioEvent e = (AudioEvent) gson.fromJson(data, type);
			return e;
		} catch (IllegalStateException e) {
			throw new JsonParseException("JSON Object expected", e);
		} catch (ClassCastException e) {
			throw new JsonParseException("Entity expected", e);
		}
	}
	
	public static String audioEventToString(AudioEvent e) {
		String name = e.getClass().getName();
		JsonObject o = new JsonObject();
		o.addProperty("type", name);
		o.add("data", gson.toJsonTree(e));
		return o.toString();
	}
	
	public static void main(String[] args) {
		HandgunBullet b = new HandgunBullet(new Vector2f(2.0f, 3.0f), 1.0f);
		String s = ObjectCodec.entityToString(b);
		System.out.println("JSON: " + s);
		Entity e = ObjectCodec.entityFromString(s);
		boolean val = e instanceof HandgunBullet;
		System.out.println("e instanceof HangunBullet == " + val);
		assert(val);
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
}

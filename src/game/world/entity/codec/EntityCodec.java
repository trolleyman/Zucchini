package game.world.entity.codec;

import java.lang.reflect.Type;

import org.joml.Vector2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import game.world.entity.Entity;
import game.world.entity.HandgunBullet;

public class EntityCodec {
	private static Gson gson = new GsonBuilder().create();
	
	public static Entity fromString(String s) {
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
	
	public static String toString(Entity e) {
		String name = e.getClass().getName();
		JsonObject o = new JsonObject();
		o.addProperty("type", name);
		o.add("data", gson.toJsonTree(e));
		return o.toString();
	}
	
	public static void main(String[] args) {
		HandgunBullet b = new HandgunBullet(new Vector2f(2.0f, 3.0f), 1.0f);
		String s = EntityCodec.toString(b);
		System.out.println("JSON: " + s);
		Entity e = EntityCodec.fromString(s);
		System.out.println("e instanceof HangunBullet == " + (e instanceof HandgunBullet));
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

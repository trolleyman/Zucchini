package game.net;
import com.google.gson.*;
import game.exception.ProtocolException;
import game.world.Team;
import game.world.entity.Entity;
import game.world.entity.weapon.HandgunBullet;
import org.joml.Vector2f;
import java.lang.reflect.Type;

public class ObjectCodec {
	private static Gson gson = new GsonBuilder().create();
	
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
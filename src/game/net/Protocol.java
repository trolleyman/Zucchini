package game.net;

import com.google.gson.*;
import game.LobbyInfo;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.audio.event.AudioEvent;
import game.exception.ProtocolException;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

import java.util.ArrayList;

public class Protocol {
	private static Gson gson = new GsonBuilder().create();
	private static JsonParser parser = new JsonParser();
	
	public static final String DISCOVERY_REQUEST = "DISCOVER_SERVER_C3_CLIENT";
	public static final String DISCOVERY_RESPONSE = "DISCOVER_SERVER_C3_CLIENT_RESPONSE";
	
	public static final String TCP_CONNECT_REQUEST = "[CONNECT]";
	public static final String TCP_CONNECT_RESPONSE_ACC = "[CONNECT_ACC]";
	public static final String TCP_CONNECT_RESPONSE_REJ = "[CONNECT_REJ]";
	
	public static final String UDP_EXIT = "[EXIT]";
	public static final String TCP_EXIT = "[EXIT]";
	
	public static final String TCP_PING = "[PING]";
	public static final String TCP_PONG = "[PONG]";
	public static final String TCP_MESSAGE = "[MES]"; // TODO
	
	public static final String TCPSocketTag = "[TCP_SOCK]";
	
	public static final int UDP_PORT = 6612;
	public static final int TCP_PORT = 6613;
	
	private static final String TAG_ACTION          = "[ACT]";
	private static final String TAG_ADD_ENTITY      = "[ADD_ENT]";
	private static final String TAG_UPDATE_ENTITY   = "[UPDATE_ENT]";
	private static final String TAG_ENTITY_REMOVE   = "[REM_ENT]";
	private static final String TAG_AUDIO_EVENT     = "[AUDIO]";
	private static final String TAG_LOBBIES_REQUEST = "[LOBREQ]";
	private static final String TAG_LOBBIES_REPLY   = "[LOBREP]";
	private static final String TAG_FULL_UPDATE_REQUEST = "[FULL_UPDATE_REQ]";
	
	/**************** Action ****************/
	public static String sendAction(Action a) {
		String s = TAG_ACTION + "[" + a.getType() + "]";
		if (a instanceof AimAction)
			s += ":" + ((AimAction) a).getAngle();
		return s;
	}
	
	public static boolean isAction(String s) {
		return s.startsWith(TAG_ACTION);
	}
	
	public static Action parseAction(String s) {
		ActionType found = null;
		ActionType[] types = ActionType.values();
		for (ActionType type : types) {
			String stype = "[" + type.toString() + "]";
			if (s.startsWith(stype)) {
				found = type;
				break;
			}
		}
		if (found == null)
			throw new RuntimeException("Invalid action: " + s);
		
		if (found.equals(ActionType.AIM)) {
			int i = s.indexOf(']');
			if (i == -1)
				throw new RuntimeException("Invalid action: " + s);
			i++;
			String sangle = s.substring(i);
			float angle = Float.parseFloat(sangle);
			return new AimAction(angle);
		}
		return new Action(found);
	}
	
	/**************** Add Entity ****************/
	public static String sendAddEntity(Entity e) {
		return TAG_ADD_ENTITY + ObjectCodec.genToString(e);
	}
	
	public static boolean isAddEntity(String s) {
		return s.startsWith(TAG_ADD_ENTITY);
	}
	
	public static Entity parseAddEntity(String s) throws ProtocolException {
		String es = s.substring(TAG_ADD_ENTITY.length());
		return ObjectCodec.genFromString(es);
	}
	
	/**************** Update Entity ****************/
	public static String sendUpdateEntity(EntityUpdate eu) {
		return TAG_UPDATE_ENTITY + ObjectCodec.genToString(eu);
	}
	
	public static boolean isUpdateEntity(String s) {
		return s.startsWith(TAG_UPDATE_ENTITY);
	}
	
	public static EntityUpdate parseUpdateEntity(String s) throws ProtocolException {
		String es = s.substring(TAG_UPDATE_ENTITY.length());
		return ObjectCodec.genFromString(es);
	}
	
	/**************** Remove Entity ****************/
	public static String sendRemoveEntity(int id) {
		return TAG_ENTITY_REMOVE + id;
	}
	
	public static boolean isRemoveEntity(String s) {
		return s.startsWith(TAG_ENTITY_REMOVE);
	}
	
	public static int parseRemoveEntity(String s) {
		return Integer.parseInt(s.substring(TAG_ENTITY_REMOVE.length()));
	}
	
	/**************** Audio Event ****************/
	public static String sendAudioEvent(AudioEvent e) {
		return TAG_AUDIO_EVENT + ObjectCodec.genToString(e);
	}
	
	public static boolean isAudioEvent(String s) {
		return s.startsWith(TAG_AUDIO_EVENT);
	}
	
	public static AudioEvent parseAudioEvent(String s) throws ProtocolException {
		s = s.substring(TAG_AUDIO_EVENT.length());
		return ObjectCodec.genFromString(s);
	}
	
	/**************** Lobbies Request ****************/
	public static String sendLobbiesRequest() {
		return TAG_LOBBIES_REQUEST;
	}
	
	public static boolean isLobbiesRequest(String s) {
		return s.equals(TAG_LOBBIES_REQUEST);
	}
	
	/**************** Lobbies Reply ****************/
	public static String sendLobbiesReply(ArrayList<LobbyInfo> lobbies) {
		JsonArray json = new JsonArray();
		for (LobbyInfo lobby : lobbies)
			json.add(gson.toJson(lobby));
		
		return TAG_LOBBIES_REQUEST + json.toString();
	}
	
	public static boolean isLobbiesReply(String s) {
		return s.startsWith(TAG_LOBBIES_REPLY);
	}
	
	public static ArrayList<LobbyInfo> parseLobbiesReply(String s) throws ProtocolException {
		s = s.substring(TAG_LOBBIES_REPLY.length());
		
		try {
			ArrayList<LobbyInfo> ret = new ArrayList<>();
			JsonArray array = parser.parse(s).getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				JsonElement e = array.get(i);
				LobbyInfo info = gson.fromJson(e, LobbyInfo.class);
				ret.add(info);
			}
			return ret;
		} catch (JsonParseException | IllegalStateException e) {
			throw new ProtocolException("Invalid lobbies reply: " + s, e);
		}
	}
	
	/**************** Full Update Request ****************/
	public static String sendFullUpdateRequest() {
		return TAG_FULL_UPDATE_REQUEST;
	}
	
	public static boolean isFullUpdateRequest(String s) {
		return s.startsWith(TAG_FULL_UPDATE_REQUEST);
	}
}

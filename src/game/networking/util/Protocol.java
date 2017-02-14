package game.networking.util;

import com.google.gson.*;
import game.LobbyInfo;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.audio.event.AudioEvent;
import game.world.entity.Entity;

import java.util.ArrayList;

public class Protocol
{
	private static Gson gson = new GsonBuilder().create();
	private static JsonParser parser = new JsonParser();
	
	public static final String CtoS_Discovery = "DISCOVER_SERVER_C3_CLIENT";
	public static final String StoC_DiscoveryAccept = "DISCOVER_SERVER_C3_RESPONSE_ACC";
	public static final String StoC_DiscoveryReject = "DISCOVER_SERVER_C3_RESPONSE_REJ";
	public static final String StoC_DiscoveryWait = "DISCOVER_SERVER_C3_RESPONSE_WAIT";
	public static final String TCP_Ping = "[PING]";
	public static final String TCP_Pong = "[PONG]";
	public static final String TCP_Message = "[MES]";
	public static final String TCP_LobbyAction = "[LOBACT]";
	public static final String TCP_GameAction = "[GAMEACT]";

	public static final String UDP_playerNameTagBegin = "[UDP_PNAME]";
	public static final String UDP_playerNameTagEnd = "[/UDP_PNAME]";

	// public static final String TCP_playerNameTagBegin = "[/TCP_PNAME]";
	// public static final String TCP_playerNameTagEnd = "[/TCP_PNAME]";
	public static final String TCPSocketTag = "[TCP_SOCK]";
	
	public static final String TAG_ACTION          = "[ACT]";
	public static final String TAG_ENTITY_UPDATE   = "[UPDATE]";
	public static final String TAG_ENTITY_REMOVE   = "[REMOVE]";
	public static final String TAG_AUDIO_EVENT     = "[AUDIO]";
	public static final String TAG_LOBBIES_REQUEST = "[LOBREQ]";
	public static final String TAG_LOBBIES_REPLY   = "[LOBREP]";
	
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
	
	public static String sendEntityUpdate(Entity e) {
		return TAG_ENTITY_UPDATE + ObjectCodec.entityToString(e);
	}
	
	public static boolean isEntityUpdate(String s) {
		return s.startsWith(TAG_ENTITY_UPDATE);
	}
	
	public static Entity parseEntityUpdate(String s) {
		String es = s.substring(TAG_ENTITY_UPDATE.length());
		return ObjectCodec.entityFromString(es);
	}
	
	public static String sendRemoveEntity(int id) {
		return TAG_ENTITY_REMOVE + id;
	}
	
	public static boolean isRemoveEntity(String s) {
		return s.startsWith(TAG_ENTITY_REMOVE);
	}
	
	public static int parseRemoveEntity(String s) {
		return Integer.parseInt(s.substring(TAG_ENTITY_REMOVE.length()));
	}
	
	public static String sendAudioEvent(AudioEvent e) {
		return TAG_AUDIO_EVENT + ObjectCodec.audioEventToString(e);
	}
	
	public static boolean isAudioEvent(String s) {
		return s.startsWith(TAG_AUDIO_EVENT);
	}
	
	public static AudioEvent parseAudioEvent(String s) {
		s = s.substring(TAG_AUDIO_EVENT.length());
		return ObjectCodec.audioEventFromString(s);
	}
	
	public static String sendLobbiesRequest() {
		return TAG_LOBBIES_REQUEST;
	}
	
	public static boolean isLobbiesRequest(String s) {
		return s.equals(TAG_LOBBIES_REQUEST);
	}
	
	public static String sendLobbiesReply(ArrayList<LobbyInfo> lobbies) {
		JsonArray json = new JsonArray();
		for (LobbyInfo lobby : lobbies)
			json.add(gson.toJson(lobby));
		
		return TAG_LOBBIES_REQUEST + json.toString();
	}
	
	public static boolean isLobbiesReply(String s) {
		return s.startsWith(TAG_LOBBIES_REPLY);
	}
	
	public static ArrayList<LobbyInfo> parseLobbiesReply(String s) {
		s = s.substring(TAG_LOBBIES_REPLY.length());
		
		ArrayList<LobbyInfo> ret = new ArrayList<>();
		JsonArray array = parser.parse(s).getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			JsonElement e = array.get(i);
			LobbyInfo info = gson.fromJson(e, LobbyInfo.class);
			ret.add(info);
		}
		return ret;
	}
}

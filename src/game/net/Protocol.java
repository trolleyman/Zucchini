package game.net;

import com.google.gson.*;
import game.LobbyInfo;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.audio.event.AudioEvent;
import game.exception.InvalidMessageException;
import game.exception.ProtocolException;
import game.world.entity.Entity;
import game.world.update.EntityUpdate;

import java.util.ArrayList;

public class Protocol {
	private static Gson gson = new GsonBuilder().create();
	private static JsonParser parser = new JsonParser();
	
	private static final String TCP_CONNECT_REQUEST = "[CONNECT]";
	private static final String TCP_CONNECT_RESPONSE_ACC = "[CONNECT_ACC]";
	private static final String TCP_CONNECT_RESPONSE_REJ = "[CONNECT_REJ]";
	
	// TODO: Handle TXP_EXIT and UDP_EXIT
	public static final String UDP_EXIT = "[EXIT_UDP]";
	public static final String TCP_EXIT = "[EXIT_TCP]";
	
	public static final String TCP_PING = "[PING]";
	public static final String TCP_PONG = "[PONG]";
	public static final String TCP_MESSAGE = "[MES]"; // TODO: Handle Message
	
	public static final String TCPSocketTag = "[TCP_SOCK]";
	
	public static final int UDP_DISCOVERY_PORT = 6611;
	public static final int UDP_SERVER_PORT = 6612;
	public static final int TCP_SERVER_PORT = 6613;
	
	private static final String TAG_DISCOVERY_REQUEST  = "[DISCOVERY_REQ]";
	private static final String TAG_DISCOVERY_RESPONSE = "[DISCOVERY_RES]";
	
	private static final String TAG_ACTION              = "[ACT]";
	private static final String TAG_ADD_ENTITY          = "[ADD_ENT]";
	private static final String TAG_UPDATE_ENTITY       = "[UPDATE_ENT]";
	private static final String TAG_ENTITY_REMOVE       = "[REM_ENT]";
	private static final String TAG_AUDIO_EVENT         = "[AUDIO]";
	private static final String TAG_LOBBIES_REQUEST     = "[LOBREQ]";
	private static final String TAG_LOBBIES_RESPONSE    = "[LOBRES]";
	private static final String TAG_FULL_UPDATE_REQUEST = "[FULL_UPDATE_REQ]";
	private static final String TAG_LOBBY_JOIN_REQUEST  = "[LOBBY_JOIN_REQ]";
	private static final String TAG_LOBBY_JOIN_ACCEPT   = "[LOBBY_JOIN_ACC]";
	private static final String TAG_LOBBY_JOIN_REJECT   = "[LOBBY_JOIN_REJ]";
	private static final String TAG_LOBBY_UPDATE        = "[LOBBY_UPDATE]";
	private static final String TAG_READY_TOGGLE        = "[READY_TOGGLE]";
	
	/**************** TCP Connection Request ****************/
	public static String sendTcpConnectionRequest(String name, int port) {
		return TCP_CONNECT_REQUEST + '[' + port + ']' + name;
	}
	
	public static boolean isTcpConnectionRequest(String msg) {
		return msg.startsWith(TCP_CONNECT_REQUEST);
	}
	
	public static Tuple<String, Integer> parseTcpConnectionRequest(String msg) throws ProtocolException {
		if (!isTcpConnectionRequest(msg))
			 throw new InvalidMessageException(msg);
		
		msg = msg.substring(TCP_CONNECT_REQUEST.length());
		if (!msg.startsWith("["))
			throw new ProtocolException("Invalid format: " + msg);
		
		int i = msg.indexOf("]");
		if (i == -1)
			throw new ProtocolException("Invalid format: " + msg);
		
		String portString = msg.substring(1, i);
		int port;
		try {
			port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			throw new ProtocolException("Invalid format: " + msg, e);
		}
		
		String name = msg.substring(i+1);
		return new Tuple<>(name, port);
	}
	
	/**************** TCP Connection Reponse ****************/
	public static boolean isTcpConnectionResponse(String msg) {
		return isTcpConnectionResponseAccept(msg)
				|| isTcpConnectionResponseReject(msg);
	}
	
	/**************** TCP Connection Reponse Success ****************/
	public static String sendTcpConnectionResponseAccept() {
		return TCP_CONNECT_RESPONSE_ACC;
	}
	
	public static boolean isTcpConnectionResponseAccept(String msg) {
		return msg.startsWith(TCP_CONNECT_RESPONSE_ACC);
	}
	
	/**************** TCP Connection Reponse Reject ****************/
	public static String sendTcpConnectionResponseReject(String reason) {
		return TCP_CONNECT_RESPONSE_REJ + reason;
	}
	
	public static boolean isTcpConnectionResponseReject(String msg) {
		return msg.startsWith(TCP_CONNECT_RESPONSE_REJ);
	}
	
	public static String parseTcpConnectionResponseReject(String msg) throws ProtocolException {
		if (!msg.startsWith(TCP_CONNECT_RESPONSE_REJ))
			throw new InvalidMessageException(msg);
		
		return msg.substring(TCP_CONNECT_RESPONSE_REJ.length());
	}
	
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
	
	public static Action parseAction(String s) throws ProtocolException {
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
			throw new ProtocolException("Invalid action: " + s);
		
		if (found.equals(ActionType.AIM)) {
			int i = s.indexOf(']');
			if (i == -1)
				throw new ProtocolException("Invalid action: " + s);
			i++;
			String sangle = s.substring(i);
			float angle;
			try {
				angle = Float.parseFloat(sangle);
			} catch (NumberFormatException e) {
				throw new ProtocolException("Invalid float: " + sangle, e);
			}
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
		return s.startsWith(TAG_LOBBIES_RESPONSE);
	}
	
	public static ArrayList<LobbyInfo> parseLobbiesReply(String s) throws ProtocolException {
		s = s.substring(TAG_LOBBIES_RESPONSE.length());
		
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
	
	/**************** Discovery Request ****************/
	public static String sendDiscoveryRequest() {
		return TAG_DISCOVERY_REQUEST;
	}
	
	public static boolean isDiscoveryRequest(String msg) {
		return msg.startsWith(TAG_DISCOVERY_REQUEST);
	}
	
	/**************** Discovery Reponse ****************/
	public static String sendDiscoveryResponse() {
		return TAG_DISCOVERY_RESPONSE;
	}
	
	public static boolean isDiscoveryResponse(String msg) {
		return msg.startsWith(TAG_DISCOVERY_RESPONSE);
	}
	
	/**************** Lobby Join Request ****************/
	public static String sendLobbyJoinRequest(String lobbyName) {
		return TAG_LOBBY_JOIN_REQUEST + lobbyName;
	}
	
	public static boolean isLobbyJoinRequest(String msg) {
		return msg.startsWith(TAG_LOBBY_JOIN_REQUEST);
	}
	
	public static String parseLobbyJoinRequest(String msg) throws ProtocolException {
		if (msg.length() < TAG_LOBBY_JOIN_REQUEST.length())
			throw new ProtocolException("Invalid lobby join request: Too short: " + msg);
		
		return msg.substring(TAG_LOBBY_JOIN_REQUEST.length());
	}
	
	/**************** Lobby Join Accept ****************/
	public static String sendLobbyJoinAccept() {
		return TAG_LOBBY_JOIN_ACCEPT;
	}
	
	public static boolean isLobbyJoinAccept(String msg) {
		return msg.startsWith(TAG_LOBBY_JOIN_ACCEPT);
	}
	
	/**************** Lobby Join Reject ****************/
	public static String sendLobbyJoinReject(String reason) {
		return TAG_LOBBY_JOIN_REJECT + reason;
	}
	
	public static boolean isLobbyJoinReject(String msg) {
		return msg.startsWith(TAG_LOBBY_JOIN_REJECT);
	}
	
	public static String parseLobbyJoinReject(String msg) {
		return msg.substring(TAG_LOBBY_JOIN_REJECT.length());
	}
	
	/**************** Lobby Update ****************/
	public static String sendLobbyUpdate(LobbyInfo info) {
		return TAG_LOBBY_UPDATE + gson.toJson(info);
	}
	
	public static boolean isLobbyUpdate(String msg) {
		return msg.startsWith(TAG_LOBBY_UPDATE);
	}
	
	public static LobbyInfo parseLobbyUpdate(String msg) throws ProtocolException {
		if (msg.length() < TAG_LOBBY_UPDATE.length())
			throw new ProtocolException("Invalid lobby update: Too short: " + msg);
		
		String nmsg = msg.substring(TAG_LOBBY_UPDATE.length());
		try {
			return gson.fromJson(nmsg, LobbyInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new ProtocolException("Invalid lobby update: " + msg, e);
		}
	}
	
	/**************** Ready Toggle ****************/
	public static String sendReadyToggle() {
		return TAG_READY_TOGGLE;
	}
	
	public static boolean isReadyToggle(String msg) {
		return msg.startsWith(TAG_READY_TOGGLE);
	}
}

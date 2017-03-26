package game.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import game.LobbyInfo;
import game.Util;
import game.action.Action;
import game.action.ActionType;
import game.action.AimAction;
import game.audio.event.AudioEvent;
import game.exception.InvalidMessageException;
import game.exception.ProtocolException;
import game.net.codec.ObjectCodec;
import game.world.entity.Entity;
import game.world.entity.update.EntityUpdate;
import game.world.map.Map;
import game.world.update.WorldUpdate;

import java.util.ArrayList;

/**
 * Contains the implementation of the encoding and decoding of packets
 */
public class Protocol {
	/**
	 * TCP Connect Request
	 * <p>
	 * Client -> Server. This must be the first message sent
	 * <p>
	 * Format: TCP_CONNECT_REQUEST "[" port "]" username <br>
	 * port = The UDP port to connect to <br>
	 * username = The requested username of the client
	 */
	private static final String TCP_CONNECT_REQUEST = "[CONNECT]";
	/**
	 * TCP Connect Response (Accept)
	 * <p>
	 * Server -> Client. This indicates that the server has accepted the client, and is ready for more packets
	 * <p>
	 * Format: TCP_CONNECT_RESPONSE_ACC
	 */
	private static final String TCP_CONNECT_RESPONSE_ACC = "[CONNECT_ACC]";
	/**
	 * TCP Connect Response (Reject)
	 * <p>
	 * Server -> Client. This indicates that the server has rejected the client's connection request.
	 * <p>
	 * Format: TCP_CONNECT_RESPONSE_REJ reason <br>
	 * reason = The reason why the client was rejected
	 */
	private static final String TCP_CONNECT_RESPONSE_REJ = "[CONNECT_REJ]";
	
	/**
	 * UDP Exit
	 * <p>
	 * * -> *. Sent when the connection is closed
	 * <p>
	 * Format: UDP_EXIT
	 */
	public static final String UDP_EXIT = "[EXIT_UDP]";
	/**
	 * TCP Exit
	 * <p>
	 * * -> *. Sent when the connection is closed
	 * <p>
	 * Format: TCP_EXIT
	 */
	public static final String TCP_EXIT = "[EXIT_TCP]";
	
	/**
	 * The port that the UDP discovery server is located on
	 */
	public static final int UDP_DISCOVERY_PORT = 6611;
	/**
	 * The port that the UDP server is located on
	 */
	public static final int UDP_SERVER_PORT = 6612;
	/**
	 * The port that the TCP server is located on
	 */
	public static final int TCP_SERVER_PORT = 6613;
	
	/**
	 * UDP Discovery Request
	 * <p>
	 * Requests that the server responds to allow for automatic discovery
	 * <p>
	 * Format: TAG_DISCOVERY_REQUEST
	 */
	private static final String TAG_DISCOVERY_REQUEST = "[C3_DISCOVERY_REQ]";
	/**
	 * UDP Discovery Response
	 * <p>
	 * Responds to a discovery request
	 * <p>
	 * Format: TAG_DISCOVERY_RESPONSE
	 */
	private static final String TAG_DISCOVERY_RESPONSE = "[C3_DISCOVERY_RES]";
	
	/**
	 * Action
	 * <p>
	 * Client -> Server. Notifies the server that the client has performed an action.
	 * <p>
	 * Format: TAG_ACTION "[" actionType "]" [ ":" aimAngle ] <br>
	 * actionType = The type of the action. <br>
	 * aimAngle = Optional. Included when the action is an {@link AimAction}.
	 * @see Action
	 * @see ActionType
	 */
	private static final String TAG_ACTION = "[ACT]";
	/**
	 * Add Entity
	 * <p>
	 * Server -> Client. Sent to add an entity to the world
	 * <p>
	 * Format: TAG_ADD_ENTITY entity <br>
	 * entity = The entity represented as JSON using {@link ObjectCodec}
	 */
	private static final String TAG_ADD_ENTITY = "[ADD_ENT]";
	/**
	 * Update Entity
	 * <p>
	 * Server -> Client. Sent to update a currently existing entity
	 * <p>
	 * Format: TAG_UPDATE_ENTITY entityUpdate <br>
	 * entityUpdate = The {@link EntityUpdate} encoded using {@link ObjectCodec}
	 */
	private static final String TAG_UPDATE_ENTITY = "[UPDATE_ENT]";
	/**
	 * Remove Entity
	 * <p>
	 * Server -> Client. Removes the entity with the ID specified from the world
	 * <p>
	 * Format: TAG_ENTITY_REMOVE removeId <br>
	 * removeId = Integer represented as a string
	 */
	private static final String TAG_ENTITY_REMOVE = "[REM_ENT]";
	/**
	 * Audio Event
	 * <p>
	 * Server -> Client. Notifies the client about an audio event to process
	 * <p>
	 * Format: TAG_AUDIO_EVENT event <br>
	 * event = {@link AudioEvent} encoded using {@link ObjectCodec}
	 */
	private static final String TAG_AUDIO_EVENT = "[AUDIO]";
	/**
	 * Lobbies Request
	 * <p>
	 * Client -> Server. Requests a list of the lobbies on the server to be sent to the client
	 * <p>
	 * Format: TAG_LOBBIES_REQUEST
	 */
	private static final String TAG_LOBBIES_REQUEST = "[LOBREQ]";
	/**
	 * Lobbies Response
	 * <p>
	 * Server -> Client. Responds to a lobbies request
	 * <p>
	 * Format: TAG_LOBBIES_RESPONSE lobbies <br>
	 * lobbies = JSON array of {@link LobbyInfo}
	 */
	private static final String TAG_LOBBIES_RESPONSE = "[LOBRES]";
	/**
	 * Full Update Request
	 * <p>
	 * Client -> Server. Requests that a full update for the world should be sent to the client
	 * <p>
	 * Format: TAG_FULL_UPDATE_REQUEST
	 */
	private static final String TAG_FULL_UPDATE_REQUEST = "[FULL_UPDATE_REQ]";
	/**
	 * Lobby Join Request
	 * <p>
	 * Client -> Server. Requests that the client join a specified lobby
	 * <p>
	 * Format: TAG_LOBBY_JOIN_REQUEST lobbyName <br>
	 * lobbyName = The name of the lobby to join.
	 */
	private static final String TAG_LOBBY_JOIN_REQUEST = "[LOBBY_JOIN_REQ]";
	/**
	 * Lobby Join Response (Accept)
	 * <p>
	 * Server -> Client. Sent if the lobby join request has been accepted
	 * <p>
	 * Format: TAG_LOBBY_JOIN_ACCEPT
	 */
	private static final String TAG_LOBBY_JOIN_ACCEPT = "[LOBBY_JOIN_ACC]";
	/**
	 * Lobby Join Response (Reject)
	 * <p>
	 * Server -> Client. Sent if the lobby join request has been rejected
	 * <p>
	 * Format: TAG_LOBBY_JOIN_REJECT reason <br>
	 * reason = Human-readable string representing why the request was denied
	 */
	private static final String TAG_LOBBY_JOIN_REJECT = "[LOBBY_JOIN_REJ]";
	/**
	 * Lobby Update
	 * <p>
	 * Server -> Client. Updates the client to changes that have occured with the lobby they are in.
	 * <p>
	 * Format: TAG_LOBBY_UPDATE info <br>
	 * info = {@link LobbyInfo} encoded in JSON using {@link ObjectCodec}
	 */
	private static final String TAG_LOBBY_UPDATE = "[LOBBY_UPDATE]";
	/**
	 * Lobby Leave Request
	 * <p>
	 * Client -> Server. Requests that the client leave the current lobby.
	 * <p>
	 * Format: TAG_LOBBY_LEAVE_REQUEST
	 */
	private static final String TAG_LOBBY_LEAVE_REQUEST = "[LOBBY_LEAVE_REQ]";
	/**
	 * Lobby Leave Notify
	 * <p>
	 * Server -> Client. Responds to the lobby leave request.
	 */
	private static final String TAG_LOBBY_LEAVE_NOTIFY = "[LOBBY_LEAVE_NOTIFY]";
	/**
	 * Lobby Create Request
	 * <p>
	 * Client -> Server. Requests that a new lobby be created.
	 * <p>
	 * Format: TAG_LOBBY_CREATE_REQUEST info <br>
	 * info = {@link LobbyInfo} on the lobby to be created
	 */
	private static final String TAG_LOBBY_CREATE_REQUEST = "[LOBBY_CREATE]";
	/**
	 * Lobby Create Response (Accept)
	 * <p>
	 * Server -> Client. Accepts the lobby create request, and puts the client into the lobby created
	 * <p>
	 * Format: TAG_LOBBY_CREATE_ACCEPT
	 */
	private static final String TAG_LOBBY_CREATE_ACCEPT = "[LOBBY_CREATE_ACCEPT]";
	/**
	 * Lobby Create Response (Reject)
	 * <p>
	 * Server -> Client. Rejects the lobby create request.
	 * <p>
	 * Format: TAG_LOBBY_CREATE_REJECT reason <br>
	 * reason = Human-readable string that describes why the client's request was rejected
	 */
	private static final String TAG_LOBBY_CREATE_REJECT = "[LOBBY_CREATE_REJECT]";
	/**
	 * Ready Toggle
	 * <p>
	 * Client -> Server. The client requests that the server toggle their ready status
	 * <p>
	 * Format: TAG_READY_TOGGLE
	 */
	private static final String TAG_READY_TOGGLE = "[READY_TOGGLE]";
	/**
	 * World Start
	 * <p>
	 * Server -> Client. Sent when the world is to be started
	 * <p>
	 * Format: TAG_WORLD_START "[" playerId "]" map <br>
	 * playerId = The entity ID of the client's avatar <br>
	 * map = The {@link Map} that was chosen, encoded in JSON using {@link ObjectCodec}
	 */
	private static final String TAG_WORLD_START = "[WORLD_START]";
	/**
	 * World Update
	 * <p>
	 * Server -> Client. Sent when the world is updated
	 * <p>
	 * Format: TAG_WORLD_UPDATE update <br>
	 * update = A {@link WorldUpdate} object, encoded using {@link ObjectCodec}
	 */
	private static final String TAG_WORLD_UPDATE = "[WORLD_UPDATE]";
	/**
	 * Message To Server
	 * <p>
	 * Client -> Server. Sent when a client wants all other clients to see a message
	 * <p>
	 * Format: TAG_MESSAGE_TO_SERVER message <br>
	 * message = The message
	 * @see Message
	 */
	private static final String TAG_MESSAGE_TO_SERVER = "[MESS_S]";
	/**
	 * Message To Client
	 * <p>
	 * Server -> Client. Sent to notify clients of messages received
	 * <p>
	 * Format: TAG_MESSAGE_TO_CLIENT name {@link Protocol#NAME_SEPERATOR} message <br>
	 * name = The username of the client that is is from. Can be "" to represent a message without a sender <br>
	 * message = The message
	 * @see Message
	 */
	private static final String TAG_MESSAGE_TO_CLIENT = "[MESS_C]";
	
	
	/** @see Protocol#TCP_CONNECT_REQUEST */
	public static String sendTcpConnectionRequest(String name, int port) {
		return TCP_CONNECT_REQUEST + '[' + port + ']' + name;
	}
	
	/** @see Protocol#TCP_CONNECT_REQUEST */
	public static boolean isTcpConnectionRequest(String msg) {
		return msg.startsWith(TCP_CONNECT_REQUEST);
	}
	
	/** @see Protocol#TCP_CONNECT_REQUEST */
	public static Tuple<String, Integer> parseTcpConnectionRequest(String msg) throws ProtocolException {
		if (!isTcpConnectionRequest(msg))
			throw new ProtocolException("Invalid format: " + msg);
		
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
		
		String name = msg.substring(i + 1);
		return new Tuple<>(name, port);
	}
	
	
	/**
	 * @see Protocol#TCP_CONNECT_RESPONSE_ACC
	 * @see Protocol#TCP_CONNECT_RESPONSE_REJ
	 */
	public static boolean isTcpConnectionResponse(String msg) {
		return isTcpConnectionResponseAccept(msg)
				|| isTcpConnectionResponseReject(msg);
	}
	
	
	/** @see Protocol#TCP_CONNECT_RESPONSE_ACC */
	public static String sendTcpConnectionResponseAccept() {
		return TCP_CONNECT_RESPONSE_ACC;
	}
	
	/** @see Protocol#TCP_CONNECT_RESPONSE_ACC */
	public static boolean isTcpConnectionResponseAccept(String msg) {
		return msg.startsWith(TCP_CONNECT_RESPONSE_ACC);
	}
	
	
	/** @see Protocol#TCP_CONNECT_RESPONSE_REJ */
	public static String sendTcpConnectionResponseReject(String reason) {
		return TCP_CONNECT_RESPONSE_REJ + reason;
	}
	
	/** @see Protocol#TCP_CONNECT_RESPONSE_REJ */
	public static boolean isTcpConnectionResponseReject(String msg) {
		return msg.startsWith(TCP_CONNECT_RESPONSE_REJ);
	}
	
	/** @see Protocol#TCP_CONNECT_RESPONSE_REJ */
	public static String parseTcpConnectionResponseReject(String msg) throws ProtocolException {
		return msg.substring(TCP_CONNECT_RESPONSE_REJ.length());
	}
	
	
	/** @see Protocol#TAG_ACTION */
	public static String sendAction(Action a) {
		String s = TAG_ACTION + "[" + a.getType() + "]";
		if (a instanceof AimAction)
			s += ":" + ((AimAction) a).getAngle();
		return s;
	}
	
	/** @see Protocol#TAG_ACTION */
	public static boolean isAction(String s) {
		return s.startsWith(TAG_ACTION);
	}
	
	/** @see Protocol#TAG_ACTION */
	public static Action parseAction(String msg) throws ProtocolException {
		String s = msg.substring(TAG_ACTION.length());
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
			String sangle = s.substring(i + 2);
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
	
	/** @see Protocol#TAG_ADD_ENTITY */
	public static String sendAddEntity(Entity e) {
		return TAG_ADD_ENTITY + ObjectCodec.entityToString(e);
	}
	
	/** @see Protocol#TAG_ADD_ENTITY */
	public static boolean isAddEntity(String s) {
		return s.startsWith(TAG_ADD_ENTITY);
	}
	
	/** @see Protocol#TAG_ADD_ENTITY */
	public static Entity parseAddEntity(String s) throws ProtocolException {
		String es = s.substring(TAG_ADD_ENTITY.length());
		return ObjectCodec.entityFromString(es);
	}
	
	
	/** @see Protocol#TAG_UPDATE_ENTITY */
	public static String sendUpdateEntity(EntityUpdate eu) {
		return TAG_UPDATE_ENTITY + ObjectCodec.entityUpdateToString(eu);
	}
	
	/** @see Protocol#TAG_UPDATE_ENTITY */
	public static boolean isUpdateEntity(String s) {
		return s.startsWith(TAG_UPDATE_ENTITY);
	}
	
	/** @see Protocol#TAG_UPDATE_ENTITY */
	public static EntityUpdate parseUpdateEntity(String s) throws ProtocolException {
		String es = s.substring(TAG_UPDATE_ENTITY.length());
		return ObjectCodec.entityUpdateFromString(es);
	}
	
	
	/** @see Protocol#TAG_ENTITY_REMOVE */
	public static String sendRemoveEntity(int id) {
		return TAG_ENTITY_REMOVE + id;
	}
	
	/** @see Protocol#TAG_ENTITY_REMOVE */
	public static boolean isRemoveEntity(String s) {
		return s.startsWith(TAG_ENTITY_REMOVE);
	}
	
	/** @see Protocol#TAG_ENTITY_REMOVE */
	public static int parseRemoveEntity(String s) {
		return Integer.parseInt(s.substring(TAG_ENTITY_REMOVE.length()));
	}
	
	
	/** @see Protocol#TAG_AUDIO_EVENT */
	public static String sendAudioEvent(AudioEvent e) {
		return TAG_AUDIO_EVENT + ObjectCodec.audioEventToString(e);
	}
	
	/** @see Protocol#TAG_AUDIO_EVENT */
	public static boolean isAudioEvent(String s) {
		return s.startsWith(TAG_AUDIO_EVENT);
	}
	
	/** @see Protocol#TAG_AUDIO_EVENT */
	public static AudioEvent parseAudioEvent(String s) throws ProtocolException {
		s = s.substring(TAG_AUDIO_EVENT.length());
		return ObjectCodec.audioEventFromString(s);
	}
	
	
	/** @see Protocol#TAG_LOBBIES_REQUEST */
	public static String sendLobbiesRequest() {
		return TAG_LOBBIES_REQUEST;
	}
	
	/** @see Protocol#TAG_LOBBIES_REQUEST */
	public static boolean isLobbiesRequest(String s) {
		return s.equals(TAG_LOBBIES_REQUEST);
	}
	
	
	/** @see Protocol#TAG_LOBBIES_RESPONSE */
	public static String sendLobbiesResponse(ArrayList<LobbyInfo> lobbies) {
		JsonArray json = new JsonArray();
		for (LobbyInfo lobby : lobbies)
			json.add(ObjectCodec.getGson().toJsonTree(lobby));
		
		return TAG_LOBBIES_RESPONSE + json.toString();
	}
	
	/** @see Protocol#TAG_LOBBIES_RESPONSE */
	public static boolean isLobbiesResponse(String s) {
		return s.startsWith(TAG_LOBBIES_RESPONSE);
	}
	
	/** @see Protocol#TAG_LOBBIES_RESPONSE */
	public static ArrayList<LobbyInfo> parseLobbiesResponse(String s) throws ProtocolException {
		s = s.substring(TAG_LOBBIES_RESPONSE.length());
		
		try {
			ArrayList<LobbyInfo> ret = new ArrayList<>();
			JsonArray array = ObjectCodec.getParser().parse(s).getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				JsonElement e = array.get(i);
				LobbyInfo info = ObjectCodec.getGson().fromJson(e, LobbyInfo.class);
				ret.add(info);
			}
			return ret;
		} catch (JsonParseException | IllegalStateException e) {
			throw new ProtocolException("Invalid lobbies reply: " + s, e);
		}
	}
	
	
	/** @see Protocol#TAG_FULL_UPDATE_REQUEST */
	public static String sendFullUpdateRequest() {
		return TAG_FULL_UPDATE_REQUEST;
	}
	
	/** @see Protocol#TAG_FULL_UPDATE_REQUEST */
	public static boolean isFullUpdateRequest(String s) {
		return s.startsWith(TAG_FULL_UPDATE_REQUEST);
	}
	
	
	/** @see Protocol#TAG_DISCOVERY_REQUEST */
	public static String sendDiscoveryRequest() {
		return TAG_DISCOVERY_REQUEST;
	}
	
	/** @see Protocol#TAG_DISCOVERY_REQUEST */
	public static boolean isDiscoveryRequest(String msg) {
		return msg.startsWith(TAG_DISCOVERY_REQUEST);
	}
	
	
	/** @see Protocol#TAG_DISCOVERY_RESPONSE */
	public static String sendDiscoveryResponse() {
		return TAG_DISCOVERY_RESPONSE;
	}
	
	/** @see Protocol#TAG_DISCOVERY_RESPONSE */
	public static boolean isDiscoveryResponse(String msg) {
		return msg.startsWith(TAG_DISCOVERY_RESPONSE);
	}
	
	
	/** @see Protocol#TAG_LOBBY_JOIN_REQUEST */
	public static String sendLobbyJoinRequest(String lobbyName) {
		return TAG_LOBBY_JOIN_REQUEST + lobbyName;
	}
	
	/** @see Protocol#TAG_LOBBY_JOIN_REQUEST */
	public static boolean isLobbyJoinRequest(String msg) {
		return msg.startsWith(TAG_LOBBY_JOIN_REQUEST);
	}
	
	/** @see Protocol#TAG_LOBBY_JOIN_REQUEST */
	public static String parseLobbyJoinRequest(String msg) throws ProtocolException {
		if (msg.length() < TAG_LOBBY_JOIN_REQUEST.length())
			throw new ProtocolException("Invalid lobby join request: Too short: " + msg);
		
		return msg.substring(TAG_LOBBY_JOIN_REQUEST.length());
	}
	
	
	/** @see Protocol#TAG_LOBBY_JOIN_ACCEPT */
	public static String sendLobbyJoinAccept() {
		return TAG_LOBBY_JOIN_ACCEPT;
	}
	
	/** @see Protocol#TAG_LOBBY_JOIN_ACCEPT */
	public static boolean isLobbyJoinAccept(String msg) {
		return msg.startsWith(TAG_LOBBY_JOIN_ACCEPT);
	}
	
	
	/** @see Protocol#TAG_LOBBY_JOIN_REJECT */
	public static String sendLobbyJoinReject(String reason) {
		return TAG_LOBBY_JOIN_REJECT + reason;
	}
	
	/** @see Protocol#TAG_LOBBY_JOIN_REJECT */
	public static boolean isLobbyJoinReject(String msg) {
		return msg.startsWith(TAG_LOBBY_JOIN_REJECT);
	}
	
	/** @see Protocol#TAG_LOBBY_JOIN_REJECT */
	public static String parseLobbyJoinReject(String msg) {
		return msg.substring(TAG_LOBBY_JOIN_REJECT.length());
	}
	
	
	/** @see Protocol#TAG_LOBBY_UPDATE */
	public static String sendLobbyUpdate(LobbyInfo info) {
		return TAG_LOBBY_UPDATE + ObjectCodec.getGson().toJson(info);
	}
	
	/** @see Protocol#TAG_LOBBY_UPDATE */
	public static boolean isLobbyUpdate(String msg) {
		return msg.startsWith(TAG_LOBBY_UPDATE);
	}
	
	/** @see Protocol#TAG_LOBBY_UPDATE */
	public static LobbyInfo parseLobbyUpdate(String msg) throws ProtocolException {
		if (msg.length() < TAG_LOBBY_UPDATE.length())
			throw new ProtocolException("Invalid lobby update: Too short: " + msg);
		
		String nmsg = msg.substring(TAG_LOBBY_UPDATE.length());
		try {
			return ObjectCodec.getGson().fromJson(nmsg, LobbyInfo.class);
		} catch (JsonParseException | IllegalStateException e) {
			throw new ProtocolException("Invalid lobby update: " + msg, e);
		}
	}
	
	
	/** @see Protocol#TAG_LOBBY_LEAVE_REQUEST */
	public static String sendLobbyLeaveRequest() {
		return TAG_LOBBY_LEAVE_REQUEST;
	}
	
	/** @see Protocol#TAG_LOBBY_LEAVE_REQUEST */
	public static boolean isLobbyLeaveRequest(String msg) {
		return msg.startsWith(TAG_LOBBY_LEAVE_REQUEST);
	}
	
	
	/** @see Protocol#TAG_LOBBY_LEAVE_NOTIFY */
	public static String sendLobbyLeaveNotify() {
		return TAG_LOBBY_LEAVE_NOTIFY;
	}
	
	/** @see Protocol#TAG_LOBBY_LEAVE_NOTIFY */
	public static boolean isLobbyLeaveNotify(String msg) {
		return msg.startsWith(TAG_LOBBY_LEAVE_NOTIFY);
	}
	
	
	/** @see Protocol#TAG_LOBBY_CREATE_REQUEST */
	public static String sendLobbyCreateRequest(LobbyInfo lobby) {
		return TAG_LOBBY_CREATE_REQUEST + ObjectCodec.getGson().toJson(lobby);
	}
	
	/** @see Protocol#TAG_LOBBY_CREATE_REQUEST */
	public static boolean isLobbyCreateRequest(String msg) {
		return msg.startsWith(TAG_LOBBY_CREATE_REQUEST);
	}
	
	/** @see Protocol#TAG_LOBBY_CREATE_REQUEST */
	public static LobbyInfo parseLobbyCreateRequest(String msg) throws ProtocolException {
		try {
			String s = msg.substring(TAG_LOBBY_CREATE_REQUEST.length());
			return ObjectCodec.getGson().fromJson(s, LobbyInfo.class);
		} catch (JsonSyntaxException e) {
			throw new ProtocolException("Invalid LobbyInfo: " + msg, e);
		}
	}
	
	
	/** @see Protocol#TAG_LOBBY_CREATE_ACCEPT */
	public static String sendLobbyCreateAccept() {
		return TAG_LOBBY_CREATE_ACCEPT;
	}
	
	/** @see Protocol#TAG_LOBBY_CREATE_ACCEPT */
	public static boolean isLobbyCreateAccept(String msg) {
		return msg.startsWith(TAG_LOBBY_CREATE_ACCEPT);
	}
	
	
	/** @see Protocol#TAG_LOBBY_CREATE_REJECT */
	public static String sendLobbyCreateReject(String reason) {
		return TAG_LOBBY_CREATE_REJECT + reason;
	}
	
	/** @see Protocol#TAG_LOBBY_CREATE_REJECT */
	public static boolean isLobbyCreateReject(String msg) {
		return msg.startsWith(TAG_LOBBY_CREATE_REJECT);
	}
	
	/** @see Protocol#TAG_LOBBY_CREATE_REJECT */
	public static String parseLobbyCreateReject(String msg) {
		return msg.substring(TAG_LOBBY_CREATE_REJECT.length());
	}
	
	
	/** @see Protocol#TAG_READY_TOGGLE */
	public static String sendReadyToggle() {
		return TAG_READY_TOGGLE;
	}
	
	/** @see Protocol#TAG_READY_TOGGLE */
	public static boolean isReadyToggle(String msg) {
		return msg.startsWith(TAG_READY_TOGGLE);
	}
	
	
	/** @see Protocol#TAG_WORLD_START */
	public static String sendWorldStart(WorldStart start) {
		return TAG_WORLD_START + '[' + start.playerId + ']' + ObjectCodec.getGson().toJson(start.map);
	}
	
	/** @see Protocol#TAG_WORLD_START */
	public static boolean isWorldStart(String msg) {
		return msg.startsWith(TAG_WORLD_START);
	}
	
	/** @see Protocol#TAG_WORLD_START */
	public static WorldStart parseWorldStart(String msg) throws ProtocolException {
		String s = msg.substring(TAG_WORLD_START.length());
		if (!s.startsWith("["))
			throw new ProtocolException("Invalid world start message: " + msg);
		
		int i = s.indexOf("]");
		if (i == -1)
			throw new ProtocolException("Invalid world start message: " + msg);
		
		String sPlayerId = s.substring(1, i);
		int playerId;
		try {
			playerId = Integer.parseInt(sPlayerId);
		} catch (NumberFormatException e) {
			throw new ProtocolException("Invalid world start message: " + msg, e);
		}
		
		String sMap = s.substring(i + 1);
		Map map = ObjectCodec.getGson().fromJson(sMap, Map.class);
		return new WorldStart(map, playerId);
	}
	
	
	/** @see Protocol#TAG_WORLD_UPDATE */
	public static String sendWorldUpdate(WorldUpdate update) {
		return TAG_WORLD_UPDATE + ObjectCodec.worldUpdateToString(update);
	}
	
	/** @see Protocol#TAG_WORLD_UPDATE */
	public static boolean isWorldUpdate(String msg) {
		return msg.startsWith(TAG_WORLD_UPDATE);
	}
	
	/** @see Protocol#TAG_WORLD_UPDATE */
	public static WorldUpdate parseWorldUpdate(String msg) throws ProtocolException {
		return ObjectCodec.worldUpdateFromString(msg.substring(TAG_WORLD_UPDATE.length()));
	}
	
	
	/** @see Protocol#TAG_MESSAGE_TO_SERVER */
	public static String sendMessageToServer(String msg) {
		return TAG_MESSAGE_TO_SERVER + msg;
	}
	
	/** @see Protocol#TAG_MESSAGE_TO_SERVER */
	public static boolean isMessageToServer(String msg) {
		return msg.startsWith(TAG_MESSAGE_TO_SERVER);
	}
	
	/** @see Protocol#TAG_MESSAGE_TO_SERVER */
	public static String parseMessageToServer(String msg) {
		return msg.substring(TAG_MESSAGE_TO_SERVER.length());
	}
	
	
	/**
	 * @see Protocol#TAG_MESSAGE_TO_SERVER
	 * @see Protocol#TAG_MESSAGE_TO_CLIENT
	 */
	private static final char NAME_SEPERATOR = ' ';
	
	static {
		// Ensure that the name seperator will not occur in a username
		assert (!Util.isValidNameChar(NAME_SEPERATOR));
	}
	
	/** @see Protocol#TAG_MESSAGE_TO_CLIENT */
	public static String sendMessageToClient(String name, String msg) {
		return TAG_MESSAGE_TO_CLIENT + name + NAME_SEPERATOR + msg;
	}
	
	/** @see Protocol#TAG_MESSAGE_TO_CLIENT */
	public static boolean isMessageToClient(String msg) {
		return msg.startsWith(TAG_MESSAGE_TO_CLIENT);
	}
	
	/** @see Protocol#TAG_MESSAGE_TO_CLIENT */
	public static Tuple<String, String> parseMessageToClient(String msg) throws ProtocolException {
		msg = msg.substring(TAG_MESSAGE_TO_CLIENT.length());
		int i = msg.indexOf(NAME_SEPERATOR);
		if (i == -1)
			throw new ProtocolException("Invalid Message Packet received: " + msg);
		
		String name = msg.substring(0, i);
		String cmsg = msg.substring(i + 1);
		return new Tuple<>(name, cmsg);
	}
}

package game.networking.util;

public class Protocol
{
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

	// public static final String = "";

}

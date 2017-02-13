package game.networking.util.interfaces;

public interface IConnectionHandler
{
	public void TCPListenerUserDisconnect(String name);

	public void TCPSenderUserDisconnect(String name);
}

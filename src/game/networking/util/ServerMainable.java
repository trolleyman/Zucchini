package networking.util;

import java.util.Queue;

public interface ServerMainable
{
	public void acceptClientConnection(String ClientName);

	public Queue<String> getAccQueue();
}

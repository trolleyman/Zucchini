package game.net;

import game.net.client.IClientConnectionHandlerTest;
import game.net.codec.ObjectCodecTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import sun.rmi.transport.ObjectTable;

/**
 * Test suite for all networking tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		ClientServerConnectionTest.class,
		IClientConnectionHandlerTest.class,
		MessageTest.class,
		ObjectCodecTest.class,
		PairTest.class,
		ProtocolTest.class,
		TCPConnectionTest.class,
		UDPConnectionTest.class,
})
public class NetTests {
}

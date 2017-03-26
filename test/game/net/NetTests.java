package game.net;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all networking tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		ClientServerConnectionTest.class,
		ProtocolTest.class,
		TCPConnectionTest.class,
		UDPConnectionTest.class,
})
public class NetTests {
}

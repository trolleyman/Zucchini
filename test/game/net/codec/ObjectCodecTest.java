package game.net.codec;

import game.audio.event.AudioEvent;
import game.audio.event.AudioStopEvent;
import game.exception.ProtocolException;
import game.world.entity.Entity;
import game.world.entity.HumanPlayer;
import game.world.entity.Player;
import game.world.entity.update.EntityUpdate;
import game.world.entity.update.HealthUpdate;
import game.world.entity.weapon.Knife;
import game.world.update.StartTimeWorldUpdate;
import game.world.update.WorldUpdate;
import org.joml.Vector2f;
import org.junit.Test;

import static org.junit.Assert.*;
import static game.TestUtil.assertThrows;

public class ObjectCodecTest {
	@Test
	public void entityCodec() throws ProtocolException {
		Player p1 = new HumanPlayer(1, new Vector2f(2.0f, 3.0f), "4", new Knife(new Vector2f(5.0f, 6.0f)));
		Entity e1 = p1;
		String s = ObjectCodec.entityToString(e1);
		Entity e2 = ObjectCodec.entityFromString(s);
		assertTrue(e2 instanceof HumanPlayer);
		Player p2 = (Player) e2;
		assertEquals(p1.getId(), p2.getId());
		assertEquals(p1.getTeam(), p2.getTeam());
		assertEquals(p1.position, p2.position);
		assertEquals(p1.getName(), p2.getName());
		assertTrue(p2.getHeldItem() instanceof Knife);
		assertEquals(p1.getHeldItem().position, p2.getHeldItem().position);
	}
	
	@Test
	public void entityUpdateCodec() throws ProtocolException {
		HealthUpdate hu1 = new HealthUpdate(4, 10.0f);
		EntityUpdate u1 = hu1;
		String s = ObjectCodec.entityUpdateToString(u1);
		EntityUpdate u2 = ObjectCodec.entityUpdateFromString(s);
		assertTrue(u2 instanceof HealthUpdate);
		HealthUpdate hu2 = (HealthUpdate) u2;
		assertEquals(hu1.getId(), hu2.getId());
	}
	
	@Test
	public void audioEventCodec() throws ProtocolException {
		AudioStopEvent s1 = new AudioStopEvent(2);
		AudioEvent e1 = s1;
		String s = ObjectCodec.audioEventToString(e1);
		AudioEvent e2 = ObjectCodec.audioEventFromString(s);
		assertTrue(e2 instanceof AudioStopEvent);
		AudioStopEvent s2 =(AudioStopEvent) e2;
		assertEquals(s1.id, s2.id);
	}
	
	@Test
	public void worldUpdateCodec() throws ProtocolException {
		StartTimeWorldUpdate s1 = new StartTimeWorldUpdate(3.0f);
		WorldUpdate u1 = s1;
		String s = ObjectCodec.worldUpdateToString(u1);
		WorldUpdate u2 = ObjectCodec.worldUpdateFromString(s);
		assertTrue(u2 instanceof StartTimeWorldUpdate);
		StartTimeWorldUpdate s2 =(StartTimeWorldUpdate) u2;
		assertEquals(s1.getStartTime(), s2.getStartTime(), 0.00001);
	}
	
	@Test
	public void testExceptions() throws ProtocolException {
		// Invalid JSON
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("--"));
		// Empty object
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("{}"));
		// No 'type' field
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("{\"typ\":4}"));
		// Unknown class
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("{\"type\":\"game.UnknownClass\",\"data\":{}}"));
		// Class not subclass of Entity
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("{\"type\":\"game.Client\",\"data\":{}}"));
		// No 'data' field
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("{\"type\":\"game.world.entity.Player\",\"dat\":2}"));
		// Invalid 'data' field
		Entity e = ObjectCodec.entityFromString("{\"type\":\"game.world.entity.HumanPlayer\",data:{}}");
		assertTrue(e instanceof HumanPlayer);
		// This should not work as it is instantiating the abstract class
		assertThrows(ProtocolException.class, () -> ObjectCodec.entityFromString("{\"type\":\"game.world.entity.Entity\",data:{}}"));
		// This should not work as it is instantiating an abstract class, and this should never happen
		assertThrows(RuntimeException.class, () -> ObjectCodec.entityFromString("{\"type\":\"game.world.entity.weapon.Weapon\",data:{}}"));
	}
}
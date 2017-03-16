package test.world;

import game.world.Team;
import org.junit.Test;

import static org.junit.Assert.*;

public class TeamTest {
	@Test
	public void isHostileTeam() {
		assertEquals(false, Team.isHostileTeam(Team.PASSIVE_TEAM, Team.PASSIVE_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.PASSIVE_TEAM, Team.INVALID_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.INVALID_TEAM, Team.PASSIVE_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.INVALID_TEAM, Team.INVALID_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.INVALID_TEAM, Team.MONSTER_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.PASSIVE_TEAM, Team.MONSTER_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.MONSTER_TEAM, Team.MONSTER_TEAM));
		assertEquals(true, Team.isHostileTeam(Team.MONSTER_TEAM, Team.FIRST_PLAYER_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.FIRST_PLAYER_TEAM, Team.FIRST_PLAYER_TEAM));
		assertEquals(true , Team.isHostileTeam(Team.FIRST_PLAYER_TEAM +1, Team.FIRST_PLAYER_TEAM));
		assertEquals(true , Team.isHostileTeam(Team.FIRST_PLAYER_TEAM, Team.FIRST_PLAYER_TEAM +1));
	}
}
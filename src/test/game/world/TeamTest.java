package test.game.world;

import game.world.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {
	@Test
	void isHostileTeam() {
		assertEquals(false, Team.isHostileTeam(Team.PASSIVE_TEAM, Team.PASSIVE_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.PASSIVE_TEAM, Team.INVALID_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.INVALID_TEAM, Team.PASSIVE_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.INVALID_TEAM, Team.INVALID_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.INVALID_TEAM, Team.MONSTER_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.PASSIVE_TEAM, Team.MONSTER_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.MONSTER_TEAM, Team.MONSTER_TEAM));
		assertEquals(true, Team.isHostileTeam(Team.MONSTER_TEAM, Team.START_FREE_TEAM));
		assertEquals(false, Team.isHostileTeam(Team.START_FREE_TEAM, Team.START_FREE_TEAM));
		assertEquals(true , Team.isHostileTeam(Team.START_FREE_TEAM+1, Team.START_FREE_TEAM));
		assertEquals(true , Team.isHostileTeam(Team.START_FREE_TEAM, Team.START_FREE_TEAM+1));
	}
}
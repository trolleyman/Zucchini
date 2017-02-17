package game.world;

import java.util.HashMap;

/**
 * Utility functions for teams
 */
public class Team {
	public static final int MONSTER_TEAM = -1;
	public static final int PASSIVE_TEAM = 0;
	public static final int START_FREE_TEAM = 1;
	
	public static boolean isHostileTeam(int myTeam, int otherTeam) {
		if (otherTeam == PASSIVE_TEAM || myTeam == PASSIVE_TEAM)
			return false;
		
		return otherTeam != myTeam;
	}
}

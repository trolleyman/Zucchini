package game.world.map;

import java.util.ArrayList;

/**
 * A simple test map
 * 
 * @author Callum
 */
public class TestMap extends Map {
	
	// Rows
	private static final float RA = 0.0f;
	private static final float RB = 2.0f;
	private static final float RC = 2.0f;
	private static final float RD = 3.0f;
	private static final float RE = 4.0f;
	private static final float RF = 4.0f;
	private static final float RG = 9.0f;
	private static final float RH = 4.5f;
	private static final float RI = 5.0f;
	private static final float RJ = RF + (float) Math.sin(Math.toRadians(60));
	private static final float RK = ???;
	private static final float RL = ???;
	private static final float RM = 6.0f;
	private static final float RN = 7.0f;
	private static final float RO = ???;
	private static final float RP = 6.5f;
	private static final float RQ = 7.5f;
	private static final float RR = 7.5f;
	private static final float RS = 7.0f;
	
	// Columns
	private static final float CA = 0.0f;
	private static final float CB = 2.0f;
	private static final float CC = 5.0f;
	private static final float CD = 6.0f;
	private static final float CE = 7.0f;
	private static final float CF = 8.0f;
	private static final float CG = 10.0f;
	private static final float CH = 1.5f;
	private static final float CI = ???;
	private static final float CJ = ;
	private static final float CK = ;
	private static final float CL = ;
	private static final float CM = ;
	private static final float CN = ;
	private static final float CO = ;
	private static final float CP = ;
	private static final float CQ = ;
	private static final float CR = ;
	private static final float CS = ;
	private static final float CT = ;
	private static final float CU = ;
	private static final float CV = ;
	
	/** Constructs the test map */
	public TestMap() {
		super(new ArrayList<>());
		walls.add(new Wall());
	}
}

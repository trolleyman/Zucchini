package game.world.map;

import game.world.entity.weapon.MachineGun;
import game.world.entity.Pickup;
import game.world.entity.monster.Zombie;
import game.world.entity.weapon.RocketLauncher;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * A simple test map
 * 
 * @author Callum
 */
public class TestMap extends Map {
	
	// Columns
	private static final float CA = 0.0f;
	private static final float CB = 2.0f;
	private static final float CC = 5.0f;
	private static final float CD = 6.0f;
	private static final float CE = 7.0f;
	private static final float CF = 8.0f;
	private static final float CG = 10.0f;
	private static final float CH = 1.5f;
	private static final float CI = CH + (float) Math.cos(Math.toRadians(60));
	private static final float CJ = 2.5f;
	private static final float CK = 4.5f;
	private static final float CL = 7.0f;
	private static final float CM = CL + (float) Math.sin(Math.toRadians(45));
	private static final float CN = CM + (float) Math.sin(Math.toRadians(45));
	private static final float CO = CN;
	private static final float CP = 6.5f;
	private static final float CQ = 6.5f;
	private static final float CR = 1.5f;
	private static final float CS = 2.0f;
	private static final float CT = 2.5f;
	private static final float CU = 3.0f;
	private static final float CV = 3.5f;
	
	// Rows
	private static final float RA = 0.0f;
	private static final float RB = 2.0f;
	private static final float RC = 2.0f;
	private static final float RD = 3.0f;
	private static final float RE = 4.0f;
	private static final float RF = 4.0f;
	private static final float RG = 9.0f;
	private static final float RH = 4.5f;
	private static final float RI = RH + (float) Math.sin(Math.toRadians(60));
	private static final float RK = RI + (float) Math.sin(Math.toRadians(45));
	private static final float RL = RK + (float) Math.sin(Math.toRadians(45));
	private static final float RJ = RL - 1.0f;
	private static final float RM = 6.0f;
	private static final float RN = 7.0f;
	private static final float RO = RL + Math.abs(CO - CG) * (float) Math.sin(Math.toRadians(45));
	private static final float RP = 6.5f;
	private static final float RQ = 7.8f;
	private static final float RR = 7.5f;
	private static final float RS = 7.0f;
	
	/** Constructs the test map */
	public TestMap() {
		super(new ArrayList<>(), 5.0f);
		walls.add(new Wall(CA, RA, CG, RA)); // -- Outside walls --
		walls.add(new Wall(CG, RA, CG, RG));
		walls.add(new Wall(CA, RA, CA, RG));
		walls.add(new Wall(CA, RG, CG, RG));
		walls.add(new Wall(CA, RF, CH, RF)); // -- Main walls --
		walls.add(new Wall(CH, RF, CI, RH)); // door
		walls.add(new Wall(CJ, RF, CK, RF));
		walls.add(new Wall(CK, RF, CK, RM));
		walls.add(new Wall(CK, RN, CK, RG));
		walls.add(new Wall(CK, RI, CL, RI));
		walls.add(new Wall(CL, RI, CM, RK));
		walls.add(new Wall(CN, RJ, CO, RL)); // door
		walls.add(new Wall(CO, RL, CG, RO));
		walls.add(new Wall(CC, RC, CP, RE)); // -- Triangles --
		walls.add(new Wall(CP, RE, CF, RC));
		walls.add(new Wall(CD, RC, CP, RD));
		walls.add(new Wall(CP, RD, CE, RC));
		walls.add(new Wall(CD, RC, CE, RC));
		walls.add(new Wall(CR, RR, CS, RP)); // -- Weird thing in top left --
		walls.add(new Wall(CS, RP, CU, RP));
		walls.add(new Wall(CU, RP, CV, RR));
		
		// -- Circle --
		float circleX = CQ;
		float circleY = RQ;
		int num = 8;
		float size = 1.0f;
		boolean prev = false;
		float prevX = 0.0f;
		float prevY = 0.0f;
		for (int i = 0; i <= num; i++) {
			float ang = (i / (float)num) * (float)Math.PI * 2;
			float x = circleX + size * (float) Math.sin(ang);
			float y = circleY + size * (float) Math.cos(ang);
			if (prev)
				walls.add(new Wall(prevX, prevY, x, y));
			
			prev = true;
			prevX = x;
			prevY = y;
		}
		
		initialEntities.add(new Pickup(new Vector2f(CT, RS), new MachineGun(new Vector2f(0.0f, 0.0f))));
		initialEntities.add(new Pickup(new Vector2f(CP, 1.0f), new RocketLauncher(new Vector2f(0.0f, 0.0f))));
		
		initialEntities.add(new Zombie(new Vector2f(3.0f, 2.0f)));
		//initialEntities.add(new Zombie(new Vector2f(3.5f, 2.0f)));
		//initialEntities.add(new Zombie(new Vector2f(3.25f, 2.45f)));
	}
}

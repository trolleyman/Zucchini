package game.world.map;
import game.ai.AIPlayer;
import game.world.Team;
import game.world.entity.Pickup;
import game.world.entity.light.Torch;
import game.world.entity.monster.Zombie;
import game.world.entity.weapon.*;
import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.Random;
/**
 * Created by jackm.
 */
public class FinalMap extends Map {
	// Arraylists of Outer and Inner Pickup Spawn Locations
	ArrayList<Vector2f> outerPickups = new ArrayList<>();
	ArrayList<Vector2f> innerPickups = new ArrayList<>();
	ArrayList<Vector2f> zombieSpawns = new ArrayList<>();
	
	public FinalMap() {
		super(new ArrayList<>(), 6.0f, 4);
		// Outer Walls
		wall2D(0, 0, 30, 30);
		// Horizontal Walls
		wall2D(10, 2.05f, 12, 1.95f);
		wall2D(16, 2.05f, 18, 1.95f);
		wall2D(0, 4.05f, 4, 3.95f);
		wall2D(8, 4.05f, 12, 3.95f);
		wall2D(16, 4.05f, 18, 3.95f);
		wall2D(26, 4.05f, 30, 3.95f);
		wall2D(2, 6.05f, 4, 5.95f);
		wall2D(18, 6.05f, 22, 5.95f);
		wall2D(24, 6.05f, 30, 5.95f);
		wall2D(0, 8.05f, 2, 7.95f);
		wall2D(4, 8.05f, 8, 7.95f);
		wall2D(12, 8.05f, 16, 7.95f);
		wall2D(18, 8.05f, 24, 7.95f);
		wall2D(26, 8.05f, 30, 7.95f);
		wall2D(0, 10.05f, 4, 9.95f);
		wall2D(6, 10.05f, 14, 9.95f);
		wall2D(16, 10.05f, 20, 9.95f);
		wall2D(26, 10.05f, 28, 9.95f);
		wall2D(0, 12.05f, 4, 11.95f);
		wall2D(4, 14.05f, 8, 13.95f);
		wall2D(26, 14.05f, 28, 13.95f);
		wall2D(24, 16.05f, 28, 15.95f);
		wall2D(4, 20.05f, 14, 19.95f);
		wall2D(16, 20.05f, 22, 19.95f);
		wall2D(28, 20.05f, 30, 19.95f);
		wall2D(2, 22.05f, 6, 21.95f);
		wall2D(12, 22.05f, 20, 21.95f);
		wall2D(24, 22.05f, 28, 21.95f);
		wall2D(2, 24.05f, 6, 23.95f);
		wall2D(14, 24.05f, 24, 23.95f);
		wall2D(0, 26.05f, 4, 25.95f);
		wall2D(8, 26.05f, 12, 25.95f);
		wall2D(16, 26.05f, 20, 25.95f);
		wall2D(2, 28.05f, 6, 27.95f);
		wall2D(26, 28.05f, 30, 27.95f);
		// Vertical Walls
		//wall2D(2.05f, 0, 1.95f, 2);
		wall2D(2.05f, 5.95f, 1.95f, 8.05f);
		wall2D(2.05f, 12, 1.95f, 16);
		wall2D(2.05f, 18, 1.95f, 22.05f);
		wall2D(2.05f, 27.95f, 1.95f, 30);
		wall2D(4.05f, 0, 3.95f, 2);
		wall2D(4.05f, 7.95f, 3.95f, 10.05f);
		wall2D(4.05f, 16, 3.95f, 20.05f);
		wall2D(6.05f, 0, 5.95f, 6);
		wall2D(6.05f, 9.95f, 5.95f, 12);
		wall2D(6.05f, 14, 5.95f, 18);
		wall2D(6.05f, 23.95f, 5.95f, 26);
		wall2D(8.05f, 2, 7.95f, 6);
		wall2D(8.05f, 12, 7.95f, 18);
		wall2D(8.05f, 22, 7.95f, 26.05f);
		wall2D(8.05f, 28, 7.95f, 30);
		wall2D(10.05f, 0, 9.95f, 2.05f);
		wall2D(10.05f, 6, 9.95f, 14);
		wall2D(10.05f, 16, 9.95f, 24);
		wall2D(10.05f, 26, 9.95f, 28);
		wall2D(12.05f, 3.95f, 11.95f, 8.05f);
		wall2D(12.05f, 21.95f, 11.95f, 24);
		wall2D(12.05f, 28, 11.95f, 30);
		wall2D(14.05f, 2, 13.95f, 6);
		wall2D(14.05f, 23.95f, 13.95f, 28);
		wall2D(16.05f, 1.95f, 15.95f, 4.05f);
		wall2D(16.05f, 6, 15.95f, 8.05f);
		wall2D(16.05f, 28, 15.95f, 30);
		wall2D(18.05f, 5.95f, 17.95f, 8.05f);
		wall2D(18.05f, 26, 17.95f, 28);
		wall2D(20.05f, 0, 19.95f, 4);
		wall2D(20.05f, 8, 19.95f, 14);
		wall2D(20.05f, 16, 19.95f, 20);
		wall2D(20.05f, 25.95f, 19.95f, 30);
		wall2D(22.05f, 2, 21.95f, 4);
		wall2D(22.05f, 10, 21.95f, 16);
		wall2D(22.05f, 18, 21.95f, 22);
		wall2D(22.05f, 24, 21.95f, 28);
		wall2D(24.05f, 0, 23.95f, 4);
		wall2D(24.05f, 12, 23.95f, 22.05f);
		wall2D(24.05f, 26, 23.95f, 30);
		wall2D(26.05f, 2, 25.95f, 4.05f);
		wall2D(26.05f, 12, 25.95f, 14.05f);
		wall2D(26.05f, 18, 25.95f, 24);
		wall2D(28.05f, 0, 27.95f, 2);
		wall2D(28.05f, 9.95f, 27.95f, 12);
		wall2D(28.05f, 18, 27.95f, 20.05f);
		wall2D(28.05f, 24, 27.95f, 28);
		// Outer Pickup Locations
		outerPickups.add(new Vector2f(1,3));
		outerPickups.add(new Vector2f(11,1));
		outerPickups.add(new Vector2f(17,3));
		outerPickups.add(new Vector2f(27,3));
		outerPickups.add(new Vector2f(3,9));
		outerPickups.add(new Vector2f(29,7));
		outerPickups.add(new Vector2f(1,15));
		outerPickups.add(new Vector2f(7,15));
		outerPickups.add(new Vector2f(27,13));
		outerPickups.add(new Vector2f(21,19));
		outerPickups.add(new Vector2f(16,23));
		outerPickups.add(new Vector2f(25,21));
		outerPickups.add(new Vector2f(7,23));
		outerPickups.add(new Vector2f(19,27));
		outerPickups.add(new Vector2f(3,29));
		outerPickups.add(new Vector2f(29,27));
		// Inner Pickup Locations
		innerPickups.add(new Vector2f(12,12));
		innerPickups.add(new Vector2f(18,12));
		innerPickups.add(new Vector2f(12,18));
		innerPickups.add(new Vector2f(18,18));
		innerPickups.add(new Vector2f(14,16));
		innerPickups.add(new Vector2f(16,14));
		// Zombie Spawns
		zombieSpawns.add(new Vector2f(21, 1));
		zombieSpawns.add(new Vector2f(13, 5));
		zombieSpawns.add(new Vector2f(1, 7));
		zombieSpawns.add(new Vector2f(21, 7));
		zombieSpawns.add(new Vector2f(9, 9));
		zombieSpawns.add(new Vector2f(15, 11));
		zombieSpawns.add(new Vector2f(27, 11));
		zombieSpawns.add(new Vector2f(14, 14));
		zombieSpawns.add(new Vector2f(16, 14));
		zombieSpawns.add(new Vector2f(11, 15));
		zombieSpawns.add(new Vector2f(19,15));
		zombieSpawns.add(new Vector2f(14, 16));
		zombieSpawns.add(new Vector2f(16, 16));
		zombieSpawns.add(new Vector2f(3, 19));
		zombieSpawns.add(new Vector2f(15, 19));
		zombieSpawns.add(new Vector2f(23, 19));
		zombieSpawns.add(new Vector2f(29, 19));
		zombieSpawns.add(new Vector2f(1, 25));
		zombieSpawns.add(new Vector2f(19, 25));
		zombieSpawns.add(new Vector2f(11,27));

		// Random Generator
		Random r = new Random();
		int high = 4;
		// Simple Random weapon spawning (no laser guns)
		// TODO: Potentially add more spawns with chance of nothing spawning
		// TODO: Add weighted randomness
		for (int i = 0; i < outerPickups.size(); i++) {
			int weapon = r.nextInt(high);
			switch (weapon) {
				case 0:
					initialEntities.add(new Pickup(outerPickups.get(i), new MachineGun(new Vector2f(0.0f, 0.0f), 60)));
					break;
				case 1:
					initialEntities.add(new Pickup(outerPickups.get(i), new Handgun(new Vector2f(0.0f, 0.0f), 32)));
					break;
				case 2:
					initialEntities.add(new Pickup(outerPickups.get(i), new PumpActionShotgun(new Vector2f(0.0f, 0.0f), 16)));
					break;
				case 3:
					initialEntities.add(new Pickup(outerPickups.get(i), new RocketLauncher(new Vector2f(0.0f, 0.0f), 5)));
					break;
				case 4:
					initialEntities.add(new Pickup(outerPickups.get(i), new SilencedPistol(new Vector2f(0.0f, 0.0f), 32)));
					break;
				default:
					// Spawn Nothing
					break;
			}
		}

		// Center has 1 laser gun
		initialEntities.add(new Pickup(new Vector2f(15.0f, 15.0f), new LaserGun(new Vector2f(0.0f, 0.0f), 64)));

		// Add Zombies
		for (int i = 0; i < zombieSpawns.size(); i++) {
			initialEntities.add(new Zombie(zombieSpawns.get(i)));
		}

		/*
		// Add all types of guns for testing only
		initialEntities.add(new Pickup(new Vector2f(1, 1), new MachineGun(new Vector2f(0.0f, 0.0f), 256)));
		initialEntities.add(new Pickup(new Vector2f(2, 1.0f), new RocketLauncher(new Vector2f(0.0f, 0.0f), 16)));
		initialEntities.add(new Pickup(new Vector2f(3.0f,2.0f), new LaserGun(new Vector2f(0.0f, 0.0f), 256)));
		initialEntities.add(new Pickup(new Vector2f(2.0f,2.0f), new SilencedPistol(new Vector2f(0.0f, 0.0f),14)));
		initialEntities.add(new Pickup(new Vector2f(1.5f,2.5f), new PumpActionShotgun(new Vector2f(0.0f, 0.0f),16)));




		initialEntities.add(new Zombie(new Vector2f(2.5f, 6.0f)));
		initialEntities.add(new Zombie(new Vector2f(6.25f, 5.45f)));
		initialEntities.add(new Zombie(new Vector2f(1.0f, 2.0f)));
		initialEntities.add(new Zombie(new Vector2f(1.5f, 6.0f)));
		initialEntities.add(new Zombie(new Vector2f(3.25f, 5.45f)));


		// Add torches
		// West Entrance
		initialEntities.add(new Torch(new Vector2f(10.21f, 16.22f)));
		initialEntities.add(new Torch(new Vector2f(10.21f, 13.82f)));
		// South Entrances
		initialEntities.add(new Torch(new Vector2f(13.81f, 10.21f)));
		initialEntities.add(new Torch(new Vector2f(16.22f, 10.21f)));
		// East Entrace
		initialEntities.add(new Torch(new Vector2f(19.79f, 13.84f)));
		initialEntities.add(new Torch(new Vector2f(19.79f, 16.26f)));
		// North Entrace
		initialEntities.add(new Torch(new Vector2f(16.24f, 19.79f)));
		initialEntities.add(new Torch(new Vector2f(13.78f, 19.79f)));
		*/

	}
	
	/**
	 * Draw 4 walls in a rectangle based on 2 opposite corners
	 * @param x0 First x pos
	 * @param y0 First y pos
	 * @param x1 Second x pos
	 * @param y1 Second y pos
	 */
	public void wall2D(float x0, float y0, float x1, float y1) {
		walls.add(new Wall(x0, y0, x1, y0));
		walls.add(new Wall(x0, y0, x0, y1));
		walls.add(new Wall(x1, y0, x1, y1));
		walls.add(new Wall(x0, y1, x1, y1));
	}
	
	@Override
	public Vector2f getSpawnLocation(int team) {
		switch (team) {
			case 1:
				return new Vector2f(1.0f, 1.0f);
			case 2:
				return new Vector2f(1.0f, 29.0f);
			case 3:
				return new Vector2f(29.0f, 29.0f);
			case 4:
				return new Vector2f(29.0f, 1.0f);
			default:
				return new Vector2f(2.0f, 2.0f);
		}
	}
}

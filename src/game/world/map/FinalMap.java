package game.world.map;


import game.world.entity.Pickup;
import game.world.entity.monster.Zombie;
import game.ai.AIPlayer;
import game.world.entity.weapon.LaserGun;
import game.world.entity.weapon.MachineGun;
import game.world.entity.weapon.PumpActionShotgun;
import game.world.entity.weapon.RocketLauncher;
import game.world.entity.weapon.SilencedPistol;

import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * Created by jackm.
 */
public class FinalMap extends Map {

    public FinalMap() {
        super(new ArrayList<>(), 10.0f);
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


        initialEntities.add(new Pickup(new Vector2f(1, 1), new MachineGun(new Vector2f(0.0f, 0.0f), 256)));
        initialEntities.add(new Pickup(new Vector2f(2, 1.0f), new RocketLauncher(new Vector2f(0.0f, 0.0f), 16)));
        initialEntities.add(new Pickup(new Vector2f(3.0f,2.0f), new LaserGun(new Vector2f(0.0f, 0.0f), 64)));
        initialEntities.add(new Pickup(new Vector2f(2.0f,2.0f), new SilencedPistol(new Vector2f(0.0f, 0.0f),14)));
        initialEntities.add(new Pickup(new Vector2f(1.5f,2.5f), new PumpActionShotgun(new Vector2f(0.0f, 0.0f),16)));
        initialEntities.add(new AIPlayer(new Vector2f(3.0f, 2.0f),new SilencedPistol(new Vector2f(0.0f, 0.0f),14)));
        
        
//        initialEntities.add(new Zombie(new Vector2f(3.0f, 2.0f)));
//        initialEntities.add(new Zombie(new Vector2f(2.5f, 6.0f)));
//        initialEntities.add(new Zombie(new Vector2f(6.25f, 5.45f)));
//        initialEntities.add(new Zombie(new Vector2f(1.0f, 2.0f)));
//        initialEntities.add(new Zombie(new Vector2f(1.5f, 6.0f)));
//        initialEntities.add(new Zombie(new Vector2f(3.25f, 5.45f)));
       // initialEntities.add(new AIPlayer(0, new Vector2f(1.25f, 1.45f), new MachineGun(new Vector2f(0.0f, 0.0f),256)));

    }
    
    public void wall2D(float x0, float y0, float x1, float y1) {
        walls.add(new Wall(x0, y0, x1, y0));
        walls.add(new Wall(x0, y0, x0, y1));
        walls.add(new Wall(x1, y0, x1, y1));
        walls.add(new Wall(x0, y1, x1, y1));
    }
}
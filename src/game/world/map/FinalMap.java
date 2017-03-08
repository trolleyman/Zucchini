package game.world.map;

import game.world.entity.Pickup;
import game.world.entity.monster.Zombie;
import game.world.entity.weapon.LaserGun;
import game.world.entity.weapon.MachineGun;
import game.world.entity.weapon.RocketLauncher;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * Created by jackm.
 */
public class FinalMap extends Map {

    public FinalMap() {
        super(new ArrayList<>(), 5.0f);
        // Outer Walls
        wall2D(0, 0, 30, 30);

        // Horizontal Walls
        wall2D(0, 4.05f, 4, 3.95f);
        wall2D(0, 8.05f, 2, 7.95f);
        wall2D(2, 6.05f, 4, 5.95f);
        wall2D(0, 10.05f, 4, 9.95f);
        wall2D(4, 8.05f, 8, 7.95f);
        wall2D(0, 12.05f, 4, 11.95f);
        wall2D(6, 10.05f, 14, 9.95f);
        wall2D(8, 4.05f, 12, 3.95f);
        wall2D(4, 14.05f, 8, 13.95f);
        wall2D(0, 4.05f, 4, 3.95f);
        wall2D(4, 20.05f, 14, 19.95f);
        wall2D(2, 22.05f, 6, 21.95f);
        wall2D(2, 24.05f, 6, 23.95f);


        initialEntities.add(new Pickup(new Vector2f(1, 1), new MachineGun(new Vector2f(0.0f, 0.0f))));
        initialEntities.add(new Pickup(new Vector2f(2, 1.0f), new RocketLauncher(new Vector2f(0.0f, 0.0f))));
        initialEntities.add(new Pickup(new Vector2f(3.0f,2.0f), new LaserGun(new Vector2f(0.0f, 0.0f))));

        //initialEntities.add(new Zombie(new Vector2f(3.0f, 2.0f)));
        //initialEntities.add(new Zombie(new Vector2f(2.5f, 6.0f)));
        initialEntities.add(new Zombie(new Vector2f(6.25f, 5.45f)));
    }

    public void wall2D(float x0, float y0, float x1, float y1) {
        walls.add(new Wall(x0, y0, x1, y0));
        walls.add(new Wall(x0, y0, x0, y1));
        walls.add(new Wall(x1, y0, x1, y1));
        walls.add(new Wall(x0, y1, x1, y1));
    }
}


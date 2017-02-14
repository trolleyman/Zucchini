package game.world.map;

/**
 * Class that generates a Maze-like map
 * 
 * @author Callum
 */
public class MazeMap extends Map {
	protected MazeMap(Maze maze, float scale) {
		super(maze.toWalls(scale));
	}
}

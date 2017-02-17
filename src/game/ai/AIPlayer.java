package game.ai;

import java.util.ArrayList;

import game.world.UpdateArgs;
import game.world.map.PathFindingMap;
import game.world.entity.Player;
import game.world.map.TestMap;
import game.world.map.Wall;

public class AIPlayer extends AI {
	/**
	 * Clones the specified AI
	 * @param ai The AI
	 */
	public AIPlayer(AIPlayer ai) {
		super(ai);
	}
	
	public AIPlayer(Player _player/* TODO: , Connection whatever*/) {
		super(_player.getId());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(UpdateArgs ua) {
		// TODO Auto-generated method stub
		ArrayList<Wall> walls = ua.map.walls;
		walls = improvePrecision(5, walls);
		float[] widthAndHeight = findWidthHeight(walls);
		
		int width = (int)widthAndHeight[0];
		int height = (int)widthAndHeight[1];	
		
		boolean [][] map = PathFindingMap.convertWallsToGrid(walls, width, height);
		
		ArrayList<Node> node = new ArrayList<Node>();
		node = new AStar(new Node(1,1), new Node(width - 5,height -5),map).findRoute();
		
	
		

	}

	@Override
	public AIPlayer clone() {
		return new AIPlayer(this);
	}
	
	
	private static ArrayList<Wall> improvePrecision (int multiplier, ArrayList<Wall> walls){
		ArrayList<Wall> newWalls = new ArrayList<Wall>();
		
		for (Wall wall : walls){
			Wall newWall = new Wall(wall.p0.x * multiplier, wall.p0.y * multiplier,
					wall.p1.x * multiplier, wall.p1.y * multiplier);
			
			newWalls.add(newWall);
			
		}
		return newWalls;
	}
	
	
	private static float[] findWidthHeight(ArrayList<Wall> walls){
		float[] biggestValue = new float[2]; //0 = width 1 = height
		for (Wall z : walls){
			if (z.p0.x > biggestValue[0]){
				biggestValue[0] = z.p0.x + 1;
			}
			if (z.p1.x > biggestValue[0]){
				biggestValue[0] = z.p1.x + 1;
			}
			if (z.p0.y > biggestValue[1]){
				biggestValue[1] = z.p0.y + 1;
			}
			if (z.p1.y > biggestValue[1]){
				biggestValue[1] = z.p1.y+ 1;
			}
			
		}
		
		return biggestValue;
		
	}
	
}

package game.ai;

import java.util.HashMap;
import java.util.ArrayList;

public class AStar {
	private int[][] heuristicMap;
	private int[][] movementCostMap;
	private int[][] totalCostMap;
	private boolean[][] walkable;
	private int width;
	private int height;
	private Node start;
	private Node goal;
	

	private ArrayList<Node> openSet;
	private ArrayList<Node> closeSet;

	private HashMap<Node, Node> previousNodeMap;
	
	
	private ArrayList<Node> finalPath;


	public AStar(Node start, Node goal, Boolean[][] walkable) {
		this.start = start;
		this.goal = goal;
		width = walkable.length;
		height = walkable[0].length;
		heuristicMap = new int[width][height];
		movementCostMap = new int[width][height]; // g score
		previousNodeMap = new HashMap<Node, Node>();
		totalCostMap = new int[width][height]; // f score
		

		openSet = new ArrayList<Node>();
		closeSet = new ArrayList<Node>();
		// setting all default values
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				heuristicMap[x][y] = (int) (Math.abs(goal.getX() -x)
						+ Math.abs(goal.getY() - y));

				movementCostMap[x][y] = Integer.MAX_VALUE;

				totalCostMap[x][y] = Integer.MAX_VALUE;

				

			}
		}

		// creating obstacles in the map
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y++) {
				if (!walkable[x][y]){
					closeSet.add(new Node(x,y));
				}
		
			}
		}

		//this.finalPath = findRoute(start, goal);
		
	}

	public ArrayList<Node> findRoute() {
		// adding start node to the openSet
		openSet.add(start);
		Node currentNode;
	
		movementCostMap[start.getX()][start.getY()] = 0;
		while (!openSet.isEmpty()) {
		
			currentNode = getSmallestValue();
			
		
			openSet.remove(currentNode);
			closeSet.add(currentNode);
		
			
			if (currentNode.equals(goal)) {
			
				// return completed path
				return constructPath(currentNode);
			}

			for (int x = currentNode.getX() - 1; x <= currentNode.getX() + 1; x++) {
				for (int y = currentNode.getY() - 1; y <= currentNode.getY() + 1; y++) {
					if (x < width && x >= 0 && y >= 0 && y < height && (x == currentNode.getX() || y == currentNode.getY())) {
							
					
						
						Node neighbour = new Node(x, y);
					
						if (closeSet.contains(neighbour)) {
							// go to next loop
							continue;
						}
			
						int tentativeGScore = movementCostMap[currentNode.getX()][currentNode.getY()] + 1;
						if (!openSet.contains(neighbour)) {
							openSet.add(neighbour);
						} else if (tentativeGScore >= movementCostMap[neighbour.getX()][neighbour.getY()]) {
							// go to next loop
							continue;
						}
						previousNodeMap.put(neighbour, currentNode);
						movementCostMap[neighbour.getX()][neighbour.getY()] = tentativeGScore;
						totalCostMap[neighbour.getX()][neighbour.getY()] = tentativeGScore
								+ heuristicMap[neighbour.getX()][neighbour.getY()];
						
					}
				}
			}

		}
		// no path found
		new Exception("no path found");
		return null;

	}

	private ArrayList<Node> constructPath(Node currentNode) {
		ArrayList<Node> path = new ArrayList<>();
		path.add(currentNode);
		while (previousNodeMap.keySet().contains(currentNode)) {
			currentNode = previousNodeMap.get(currentNode);
			path.add(0, currentNode);
		}

		return path;
	}

	public ArrayList<Node> getPath() {
		return this.finalPath;
	}

	private Node getSmallestValue() {
		Node min;
		min = openSet.get(0);
		for (Node n : openSet) {
			if (totalCostMap[n.getX()][ n.getY()] < totalCostMap[min.getX()][min.getY()]) {
				
				min = n;
			}
		}
		return min;
	}
}
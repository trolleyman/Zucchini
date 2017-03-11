package game.ai;

import java.util.HashMap;

import game.world.map.PathFindingMap;

import java.util.ArrayList;


/**
 * Simple A* route from (x,y) to (a,b)
 * 
 * run by: AStar route = new Astar(new Node(startX, startY), new Node(goalx, goaly), map);
 * 		   route.findRoute();
 * If preferred could be converted so that it can be executed in one commanded
 * @author George
 */


public class AStar {
	private int[][] heuristicMap;
	private int[][] movementCostMap;
	private int[][] totalCostMap;
	
	private int width;
	private int height;
	private Node start;
	private Node goal;
	

	private ArrayList<Node> openSet;
	private ArrayList<Node> closeSet;

	private HashMap<Node, Node> previousNodeMap;
	
	
	private ArrayList<Node> finalPath;

/*
 * Constructor of A*Star
 * @param start the start coordinates 
 * @param goal the goal coordinates
 * @param walkable the map
 */
	public AStar(Node start, Node goal, boolean[][] walkable) {
		
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
				if (walkable[x][y]){
					closeSet.add(new Node(x,y));
				}
		
			}
		}

		//this.finalPath = findRoute(start, goal);
		
	}
	/*
	 * finds the path to the goal
	 * @returns an ArrayList of nodes which is the route from start to goal
	 */
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
					if (x < width && x >= 0 && y >= 0 ) {
							
					
						
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
		System.out.println("[Game]: [AStar]: no path found from " + start + " to " + goal);
		return new ArrayList<>();
	}
	/*
	 * adds the path to an array list
	 * @param currentNode adds the current node to the path
	 * @returns path   
	 */
	private ArrayList<Node> constructPath(Node currentNode) {
		ArrayList<Node> path = new ArrayList<>();
		path.add(currentNode);
		while (previousNodeMap.keySet().contains(currentNode)) {
			currentNode = previousNodeMap.get(currentNode);
			path.add(0, currentNode);
		}

		return path;
	}
	/*
	 * finds the final path
	 * @returns the path from start to goal
	 */
	public ArrayList<Node> getPath() {
		return this.finalPath;
	}
	/*
	 * When a* chooses the next node to follow it will look at the 
	 * heuristic mapping, this function finds the smallest heuristic
	 * value in an adjacent nodes (all the nodes in the open set)
	 * @retuns node the lowest heuristical value of the adjacent nodes (all the nodes in the open set)
	 */
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
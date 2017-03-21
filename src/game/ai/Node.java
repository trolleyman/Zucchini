package game.ai;

public class Node{
    private int x, y;

    public Node(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX(){
    	return this.x;
    	
    }
    public int getY(){
    	return this.y;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(this.x) * 139 + Integer.hashCode(this.y);
    }
    
    @Override
    public boolean equals(Object o){
        Node n = (Node) o;
        return x == n.x && y == n.y;
    }

    @Override
    public String toString(){
        return "Node: (" + this.x + ", " + this.y +")";
    }
}
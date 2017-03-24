package game.ai;
/**
 * the node which consists of an X and Y coordinate
 * @author George Alexander
 *
 */
public class Node{
    private int x, y;
    /**
     * constructor of the node
     * @param x x coordinate
     * @param y y coordinate
     */
    public Node(int x, int y){
        this.x = x;
        this.y = y;
    }
    /**
     * get the x coordinate
     * @return x coordinate
     */
    public int getX(){
    	return this.x;
    	
    }
    /**
     * get the y coordinate
     * @return y coordinate
     */
    public int getY(){
    	return this.y;
    }
    /**
     * the hash code of the node object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(this.x) * 139 + Integer.hashCode(this.y);
    }
    /**
     * for comparison
     * @return the comparison
     */
    @Override
    public boolean equals(Object o){
        Node n = (Node) o;
        return x == n.x && y == n.y;
    }
    /**
     * converts object to string
     * @return the x and y coordinate to string
     */
    @Override
    public String toString(){
        return "Node: (" + this.x + ", " + this.y +")";
    }
}
package game.net;

public class Pair<F, S> {
	private F first;
	private S second;
	
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public F getFirst() {
		return first;
	}
	
	public S getSecond() {
		return second;
	}
	
	public boolean equals(Pair<F, S> p) {
		if (p.getFirst().equals(first) && p.getSecond().equals(second))
			return true;
		else
			return false;
	}
}

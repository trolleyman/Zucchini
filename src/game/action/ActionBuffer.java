package game.action;

import java.util.ArrayList;

public class ActionBuffer implements IActionSource {
	
	private ArrayList<Action> buffer;

	public ActionBuffer() {
		this.buffer = new ArrayList<>();
	}
	
	@Override
	public synchronized ArrayList<Action> getActions() {
		ArrayList<Action> prevBuf = this.buffer;
		this.buffer = new ArrayList<>();
		return prevBuf;
	}
	
	/**
	 * Adds an action to the internal buffer.
	 * @param a The action
	 */
	public synchronized void addAction(Action a) {
		this.buffer.add(a);
	}
}

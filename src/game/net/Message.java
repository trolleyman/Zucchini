package game.net;

public class Message {
	public String name;
	public String msg;
	public long timeReceived;
	
	public Message(String name, String msg) {
		this.name = name;
		this.msg = msg;
		this.timeReceived = System.nanoTime();
	}
	
	@Override
	public String toString() {
		if (name == null || name.equals("")) {
			return msg;
		} else {
			return name + ": " + msg;
		}
	}
}

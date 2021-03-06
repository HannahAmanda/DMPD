package message;

import java.util.Arrays;

public class Message {
	
	private final double[] message;
	private final String name;
	
	public Message(String name, double[] message) {
		this.name = name;
		this.message = message;
	}
	
	public double[] getMessage() {
		return message;
	}
	
	
	public String getSenderName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ":       " + Arrays.toString(message);
	}

}

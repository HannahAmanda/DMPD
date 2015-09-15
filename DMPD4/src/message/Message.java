package message;

import java.util.Arrays;

import node.Vertex;


public class Message {
	
	private final double[] message;
	private final Vertex sender;
	
	public Message(Vertex sender, double[] message) {
		this.message = message;
		this.sender = sender;
	}

	public double[] getMessage() {
		return message;
	}
	
	public Vertex getSender() {
		return sender;
	}

	@Override
	public String toString() {
		return sender.getNodeName() + ": " + Arrays.toString(message);
	}

}

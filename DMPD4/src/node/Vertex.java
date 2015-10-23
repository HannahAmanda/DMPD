package node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import message.Message;

public abstract class Vertex implements Comparable<Vertex> {

	protected int nodeId;
	protected String nodeName;
	protected Vertex buddy;

	protected List<Vertex> neighborList = new ArrayList<Vertex>();
	protected List<Message> messageList = new ArrayList<Message>();
	protected List<Vertex> pendingNeighborList = new ArrayList<Vertex>();

	public abstract double[] calculateTransmission(Vertex except);

	public abstract void reset();

	public int getNodeId() {
		return nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public Vertex getBuddy() {
		return buddy;
	}

	public void setBuddy(Vertex buddy) {
		if (this.buddy != null) {
			throw new IllegalStateException("Buddy should be final, is already set to " + this.buddy.toString());
		}
		this.buddy = buddy;
		neighborList.add(buddy);
	}

	public List<Vertex> getNeighborList() {
		return neighborList;
	}

	public void addNeighbor(Vertex n) {
		neighborList.add(n);
	}

	public boolean canSendTo(Vertex n) {
		for (Vertex v : neighborList) {
			if (!v.equals(n) && !hasMessageFrom(v)) {
				return false;
			
			}
		}
		return true;
	}

	public boolean hasMessageFrom(Vertex v) {
		for (Message m : messageList) {
			if (m.getSender() == v)
				return true;
		}
		return false;
	}

	public void passMessageTo(Vertex to) {
		if (canSendTo(to)) {
			Message m = new Message(this, calculateTransmission(to));
		 	to.receiveMessage(m);
		 	System.out.println(toString() + " -> " + to.toString() + ": " + Arrays.toString(m.getMessage()));
		} else {
			new IllegalArgumentException("Cannot send message to " + to.toString());
		}
	}

	public void receiveMessage(Message incoming) {
		int index = -1;
		Message m = normalize(incoming);
		for (Message v : messageList) {
			if (v.getSender().equals(m.getSender())) {
				index = messageList.indexOf(v);
			}
		}

		if (index == -1) {
			// System.out.println(-1 + " " + m.toString() + " to " +
			// toString());
			messageList.add(m);
		} else {
			
			messageList.remove(index);
			messageList.add(m);
		}
	}

	private Message normalize(Message incoming) {
		double[] incomingNormalized = incoming.getMessage();
		
		double sum= 0;
		for (int i = 0; i < incomingNormalized.length; i++) {
			sum += incomingNormalized[i];
		}
		
		for (int i = 0; i < incomingNormalized.length; i++) {
			incomingNormalized[i] /= sum;
		}
		
		return new Message(incoming.getSender(), incomingNormalized);
	}
	
	/**
	 * Calculates and passes messages to all neighbors
	 */
	public void passAllMessages() {
		for (Vertex n: neighborList) {
			passMessageTo(n);
		}
	}

	@Override
	public boolean equals(Object o) {
		// check null and not vertex
		if (((Vertex) o).nodeName.equals(this.nodeName)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return nodeName;
	}

	@Override
	public int compareTo(Vertex o) {
		if (nodeId < o.nodeId) {
			return -1;
		} else if (nodeId > o.nodeId) {
			return 1;
		} else {
			return 0;
		}
	}

}

package decoder.embedded.factor_graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import message.Message;
import message.recieveMessage;

public abstract class FactorGraphNode implements Comparable<FactorGraphNode>, recieveMessage{

	protected int nodeId;
	protected String nodeName;
	protected FactorGraphNode buddy;

	protected List<FactorGraphNode> neighborList = new ArrayList<FactorGraphNode>();
	protected List<Message> messageList = new ArrayList<Message>();
	protected List<FactorGraphNode> pendingNeighborList = new ArrayList<FactorGraphNode>();

	public abstract double[] calculateTransmission(FactorGraphNode except);

	public abstract void reset();

	public int getNodeId() {
		return nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public FactorGraphNode getBuddy() {
		return buddy;
	}

	public void setBuddy(FactorGraphNode buddy) {
		if (this.buddy != null) {
			throw new IllegalStateException("Buddy should be final, is already set to " + this.buddy.toString());
		}
		this.buddy = buddy;
		neighborList.add(buddy);
	}

	public List<FactorGraphNode> getNeighborList() {
		return neighborList;
	}

	public void addNeighbor(FactorGraphNode n) {
		neighborList.add(n);
	}

	public boolean canSendTo(FactorGraphNode n) {
		for (FactorGraphNode v : neighborList) {
			if (!v.equals(n) && !hasMessageFrom(v)) {
				return false;
			
			}
		}
		return true;
	}

	public boolean hasMessageFrom(FactorGraphNode v) {
		for (Message m : messageList) {
			if (m.getSenderName().equals(v.nodeName))
				return true;
		}
		return false;
	}

	public void passMessageTo(FactorGraphNode to) {
		if (canSendTo(to)) {
			Message m = new Message(nodeName, calculateTransmission(to));
		 	to.receiveMessage(m);
		 	System.out.println(toString() + " -> " + to.toString() + ": " + Arrays.toString(m.getMessage()));
		} else {
			new IllegalArgumentException("Cannot send message to " + to.toString());
		}
	}

	@Override
	public void receiveMessage(Message incoming) {
		int index = -1;
		Message m = normalize(incoming);
		for (Message v : messageList) {
			if (v.getSenderName().equals(m.getSenderName())) {
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
		
		return new Message(incoming.getSenderName(), incomingNormalized);
	}
	
	/**
	 * Calculates and passes messages to all neighbors
	 */
	public void passAllMessages() {
		for (FactorGraphNode n: neighborList) {
			passMessageTo(n);
		}
	}

	@Override
	public boolean equals(Object o) {
		// check null and not vertex
		if (((FactorGraphNode) o).nodeName.equals(this.nodeName)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return nodeName;
	}

	@Override
	public int compareTo(FactorGraphNode o) {
		if (nodeId < o.nodeId) {
			return -1;
		} else if (nodeId > o.nodeId) {
			return 1;
		} else {
			return 0;
		}
	}

}

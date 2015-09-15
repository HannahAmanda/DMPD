package node;

import java.util.ArrayList;
import java.util.List;

import message.Message;

public abstract class Vertex {
	
	protected int nodeId;
	protected String nodeName;
	protected Vertex buddy;
	
	protected List<Vertex> neighborList = new ArrayList<Vertex>();
	protected List<Message> messageList = new ArrayList<Message>();
	protected List<Vertex> pendingNeighborList = new ArrayList<Vertex>();
	
	public abstract double[] calculateTransmission(Vertex except);
	
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
		for (Vertex v: neighborList) {
			if (!v.equals(n) && hasMessageFrom(v)) {
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
		Message m = new Message(this, calculateTransmission(to));
		to.receiveMessage(m);
	
		// TODO: Need to keep track of the neighbors one has sent to?
		// A - Yes, update pendingNeighborList
		/*for (Vertex n: pendingNeighborList) {
			if (to.equals(n)) {
				pendingNeighborList.remove(n);
			}
		}*/
	}
	
	public void receiveMessage(Message m) {
		boolean added = false;
		int index = -1;
		for (Message v: messageList) {
			if (v.getSender().equals(m.getSender())) {
				index = messageList.indexOf(v);
			}
		}
		
		if (index == -1) {
			System.out.println(-1 + " " + this.toString() + " to "+ m.toString());
			messageList.add(m);
		} else {
			System.out.println( " " + index + " " + this.toString() + " to " + m.toString());
			messageList.remove(index);
			messageList.add(m);
		}
		
		// update pendingNeighborsList
		/*for(Vertex n: neighborList){
			if(!n.equals(m.getSender()) && !pendingNeighborList.contains(n)){
				pendingNeighborList.add(n);
			}
		}*/
	}
	
	@Override
	public boolean equals(Object o) {
		if(((Vertex) o).nodeName.equals(this.nodeName)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return nodeName;
	}

}

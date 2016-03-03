package node;

import java.util.ArrayList;
import java.util.List;

import f4.Element;
import message.Message;

public abstract class Node {
	
	protected int nodeId;
	protected String nodeName;
	protected List<Node> neighbors = new ArrayList<Node>();
	
	public abstract void receiveSoftInfo(double[] softInfo);
	public abstract void passMessageTo(Node theOther);
	public abstract void passInitialMessages();
	public abstract boolean hasMessageFrom(Node to);
	public abstract void receiveMessage(Message m);
	
	
	public abstract void finalSetup();
	public abstract void reset();
	public abstract void addNeighbor(Node n);
	public abstract Element getState();
	
	public List<Node> getNeighborList(){
		return neighbors;
	}
	
	public int getNodeId(){
		return nodeId;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	@Override
	public String toString() {
		return "M" + nodeId;
	}
	
	public boolean isLeaf() {
		return neighbors.size() == 1;
	}
	

}

package node;

import java.util.ArrayList;
import java.util.List;

import message.Message;

public class SimpleNode extends Node {

	private double[] softInfo;
	private boolean isLeaf;
	
	private List<Message> messagesA = new ArrayList<Message>();
	private List<Message> messagesB = new ArrayList<Message>();
		
	
	public SimpleNode(int id) {
		this.nodeId = id;
		this.nodeName = "N" +id;
	}
	
	@Override
	public void receiveSoftInfo(double[] softInfo) {
		this.softInfo = softInfo;
	}

	@Override
	public void passMessageTo(Node theOther) {
		if (this.isLeaf) {
			theOther.receiveMessage(new Message( nodeName, softInfo));
			
		} 
		// If |this| only has messages from leaves and is sending to an internal node
		else if (!((SimpleNode) theOther).isLeaf() && messagesB.isEmpty()){
			theOther.receiveMessage(new Message(nodeName, greekVector()));
			
		} 
		else {
			if (messagesB.isEmpty()) {
				// (s,t,s,t)
				
			} else {
				// (s,s,t,t)
				
			}
			
			
		}
		
	}

	private double[] greekVector() {
		double[] result = new double[] {1,1,1,1};
		
		for (Message m: messagesA) {
			result = dividedSXSX(result, m.getMessage());
		}
		
		result = dividedSXSX(result, softInfo);
		
		return result;
	}

	@Override
	public void passInitialMessages() {
		double[] identityMessage = {1.0,1.0,1.0,1.0};
		for (Node n: neighbors) {
			n.receiveMessage(new Message(toString(), identityMessage));
		}
	}

	@Override
	public void finalSetup() {
		if (neighbors.size() == 1) {
			isLeaf = true;
		} else {
			isLeaf = false;
		}
	}

	@Override
	public void reset() {
		messagesA.clear();
		messagesB.clear();
	}

	@Override
	public void addNeighbor(Node n) {
		neighbors.add(n);
	}

	public boolean isLeaf() {
		return isLeaf;
	}
	
	@Override
	public void receiveMessage(Message m) {
		String name = m.getSenderName();
		boolean leaf = ((SimpleNode) neighbors.get(findNeighbor(name))).isLeaf();
		int index = -1;

		if (leaf) {
			
			for (Message a: messagesA) {
				if (a.getSenderName().equals(name)) {
					index = messagesA.indexOf(a);
				}
			}
			
			if (index != -1) {
				messagesA.remove(index);
			}
			messagesA.add(m);
			
		} else {
			
			for (Message b: messagesB) {
				if(b.getSenderName().equals(name)) {
					index = messagesB.indexOf(b);
				}
			}
			
			if (index != -1) {
				messagesB.remove(index);
			}
			messagesB.add(m);
		}
	}
	
	private int findNeighbor(String name) {
		int index = -1;
		for (Node v: neighbors) {
			if (v.nodeName.equals(name)) {
				index = neighbors.indexOf(v);
			}
		}
		return index;
	}

	private double[] dividedSXSX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[1];
		double b = m[0]*n[1] + m[1]*n[0];
		double c = m[2]*n[2] + m[3]*n[3];
		double d = m[2]*n[3] + m[3]*n[2];
		
		result = new double[] {a,b,c,d};
		
		return result;
	}
	private double[] dividedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[1];
		double b = m[0]*n[1] + m[1]*n[0];
		double c = m[2]*n[2] + m[3]*n[3];
		double d = m[2]*n[3] + m[3]*n[2];
		
		result = new double[] {a,c,b,d};
		
		return result;
	}
	
	private double[] twistedSXSX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[0]*n[2] + m[2]*n[0];
		double c = m[1]*n[1] + m[3]*n[3];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[] {a,b,c,d};
		
		return result;
	}
	

	private double[] twistedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[0]*n[2] + m[2]*n[0];
		double c = m[1]*n[1] + m[3]*n[3];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[] {a,c,b,d};
		
		return result;
	}
}

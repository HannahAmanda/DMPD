package node;

import java.util.ArrayList;
import java.util.List;

import f4.Element;
import message.Calculator;
import message.Message;

public class SimpleNode extends Node {

	protected double[] softInfo;
	protected boolean isLeaf;
	protected Calculator calc = new Calculator();
	
	protected List<Message> messagesA = new ArrayList<Message>();
	protected List<Message> messagesB = new ArrayList<Message>();
		
	
	public SimpleNode(int id) {
		this.nodeId = id;
		this.nodeName = "M" +id;
	}
	
	@Override
	public void receiveSoftInfo(double[] softInfo) {
		this.softInfo = softInfo;
	}

	@Override
	public void passMessageTo(Node theOther) {
		boolean sent = false;
		if (this.isLeaf) {
			theOther.receiveMessage(new Message(nodeName, softInfo));
			sent = true;
		} 
		// Internal node
		else if (!((SimpleNode) theOther).isLeaf()){
			
			 // has both leaves and internal nodes for neighbors
			if (!(messagesB.isEmpty() || messagesA.isEmpty())) {
				double[] greek = greekVector();
				
				double[] trans = greek;
				for (Message m: messagesB) {
					if (!m.getSenderName().equals(theOther.nodeName)) {
						trans = twistedSSXX(m.getMessage(),trans);
					}
				}
				theOther.receiveMessage(new Message(nodeName, trans));
				sent = true;
				
			} // has only internal nodes for neighbors
			else if (messagesA.isEmpty() && !messagesB.isEmpty()) {
				double[] trans = softInfo;
				for (Message m: messagesB) {
					trans = dividedSSXX(trans, m.getMessage());
				}				
				theOther.receiveMessage(new Message(nodeName, trans));
				sent = true;
			}

		} 
		else if (((SimpleNode) theOther).isLeaf()) {
			double[] greek = greekVector(theOther);
			
			double[] transmission = {1.0,1.0,1.0,1.0};
			int bs = messagesB.size()-1;
			
			while (bs > 0) {
				double[] n = messagesB.get(bs).getMessage();
				transmission = twistedSSXX(n,transmission);
				
				bs--;
			}
			transmission = twistedSSXX(greek, transmission);
			theOther.receiveMessage(new Message(nodeName, transmission));
			sent = true;
		}
		if(!sent) System.out.println("Did not send any messages");
		
	}

	/**
	 * A method for combining all the leaves of an internal node, 
	 * except theOther. 
	 * 
	 * @param theOther
	 * @return
	 */
	private double[] greekVector(Node theOther) {
		double[] result = new double[] {1,1,1,1};
		
		// Messages from the leaves.
		for (Message m: messagesA) {
			if (!m.getSenderName().equals(theOther.nodeName)) {
				result = dividedSXSX(result, m.getMessage());	
			}
		}		
		// The final Greek vector. 
		result = dividedSSXX(result, softInfo);
		
		return result;
	}

	/**
	 * A method for combining all the leaves of an internal node.
	 * 
	 * @return
	 */
	private double[] greekVector() {
		double[] result = new double[] {1,1,1,1};
		
		// Messages from the leaves.
		for (Message m: messagesA) {
			result = dividedSXSX(result, m.getMessage());
		}
		
		// The final Greek vector. 
		result = dividedSSXX(result, softInfo);
		
		return result;
	}

	
	private double[] marginalize() {
		double[] marginal;
		
		if (isLeaf()) {
			marginal = messagesB.get(0).getMessage();
			marginal = dividedSXSX(marginal, softInfo);
		
		} else {
			marginal = new double[] {1.0,1.0,1.0,1.0};
			
			if (messagesA.isEmpty() && !messagesB.isEmpty()) {
				for (Message m: messagesB) {
					marginal = dividedSSXX(marginal, m.getMessage());
				}
				marginal = dividedSSXX(softInfo, marginal);
				
			} else if (!(messagesB.isEmpty() || messagesA.isEmpty())) {
				for (Message m: messagesA) {
					marginal = dividedSXSX(marginal, m.getMessage());
				}
				for (Message m: messagesB) {
					marginal = twistedSSXX(marginal, m.getMessage());
				}
				marginal = twistedSSXX(marginal, softInfo);
			}
			
		}
		
		return marginal;
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

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}
	
	@Override
	public void receiveMessage(Message m) {
		System.out.println(nodeName + " <--- " + m.toString());
		
		String name = m.getSenderName();
		boolean leaf = ((SimpleNode) neighbors.get(findNeighborIndex(name))).isLeaf();
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
	
	private int findNeighborIndex(String name) {
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
		
		return normalize(result);
	}
	private double[] dividedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[1];
		double b = m[2]*n[2] + m[3]*n[3];
		double c = m[0]*n[1] + m[1]*n[0];
		double d = m[2]*n[3] + m[3]*n[2];
		
		result = new double[] {a,b,c,d};
		
		return normalize(result);
	}
	
	private double[] twistedSXSX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[2];
		double b = m[0]*n[2] + m[1]*n[0];
		double c = m[2]*n[1] + m[3]*n[3];
		double d = m[2]*n[3] + m[3]*n[1];
		
		result = new double[] {a,b,c,d};
		
		return normalize(result);
	}
	

	private double[] twistedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[2];
		double b = m[2]*n[1] + m[3]*n[3];
		double c = m[0]*n[2] + m[1]*n[0];
		double d = m[2]*n[3] + m[3]*n[1];
		
		result = new double[] {a,b,c,d};
		
		return normalize(result);
	}

	@Override
	public Element getState() {
		double[] marginal = marginalize();
		
		System.out.println(nodeName + " marginal: " + marginal[0] + ", " + marginal[1]+ ", " + marginal[2] + ", " + marginal[3]);
		
		int index = 0;
		for (int i = 0; i < marginal.length; i++) {
			if ( marginal[index] < marginal[i]) {
				index = i;
			}
		}
		
		if (index == 0) {
			return Element.ZERO;
		} else if (index == 1) {
			return Element.ONE;
		} else if (index == 2) {
			return Element.OMEGA;
		} else if (index == 3) {
			return Element.OMEGASQ;
		}
		
		return null;
	}
	
	private double[] normalize(double[] t) {
		double sum = 0.0;
		
		for (int i = 0; i < t.length; i++) {
			sum+= t[i];
		}
		
		for (int i = 0; i < t.length; i++) {
			t[i] /= sum;
		}
		return t;
	}


	@Override
	public boolean hasMessageFrom(Node to) {
		boolean has = false;
		if (to.isLeaf()) {
			for (Message m: messagesA) {
				if (m.getSenderName().equals(to.getNodeName())) {
					has = true;
				}
			}
		} else {
			for (Message m: messagesB) {
				if (m.getSenderName().equals(to.getNodeName())) {
					has = true;
				}
			}
		}
		return has;
	}

	@Override
	public boolean hasLeaves() {
		return !isLeaf() && messagesA.size() > 0;
	}
}

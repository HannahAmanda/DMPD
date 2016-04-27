package simulator.decoding.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import simulator.decoding.message.Message;
import code.graph.Node;
import f4.Element;


public class Point extends Node {
	
	protected double[] softInfo;
	protected boolean isLeaf;
	private Calculator calc = new Calculator();
	
	protected List<Message> messagesA = new ArrayList<Message>();
	protected List<Message> messagesB = new ArrayList<Message>();
	
	public Point(int id) {
			nodeId = id;
			nodeName = "M" + id;
	}
	

	@Override 
	public void passMessageTo(Node theOther) {
		
		if (isLeaf()) {
			passLeafInfo(theOther);
			
		} else if (!theOther.isLeaf()) {
			passToInternal(theOther);
			
		} else {
			passToLeaf(theOther);
			
		}
		
	}

	private void passToLeaf(Node theOther) {
		double[] leaves = combineAllLeaves(theOther);
		double[] internals = combineAllInternal();
		
		if (internals != null){
			double[] m = calc.dSX(leaves, softInfo);
			m = calc.dSS(m, internals);
			theOther.receiveMessage(new Message(nodeName, normalize(m)));
			
		} else {
			// correct for stars
			double[] m = calc.dSS(leaves, softInfo);
			theOther.receiveMessage(new Message(nodeName, normalize(m)));
		}
	}

	private void passLeafInfo(Node theOther) {
		theOther.receiveMessage(new Message(nodeName, normalize(softInfo)));
	}

	private void passToInternal(Node theOther) {
		double[] leaves = combineAllLeaves();
		double[] internals = combineAllInternal(theOther);
		
		double[] m = new double[4];
		if (internals != null) {
			m = calc.dSX(leaves,  internals);
			m = calc.dSS(m,  softInfo);
			
		} else {
			m = calc.dSS(leaves,  softInfo);
		}
		
		theOther.receiveMessage(new Message(nodeName, normalize(m)));
	}
	
	private double[] combineAllInternal(Node theOther) {
		ArrayList<Message> mB = removeInternalMessage(theOther);
		
		if (mB.isEmpty()) {
			System.out.println("don't know what to do");
			return null;
		} else if (mB.size() == 1) {
			return mB.get(0).getMessage();
		} else {
			double[] result = mB.get(0).getMessage();
			
			for (int i = 1; i < mB.size(); i++) {
				result = calc.dSX(result, mB.get(i).getMessage());
			}
			
			return result;
		}
	}

	private double[] combineAllInternal() {
		
		if (messagesB.isEmpty()) {
			return null;
		} else {
			double[] result = messagesB.get(0).getMessage();
			
			for (int i = 1; i < messagesB.size(); i++) {
				result = calc.dSX(result, messagesB.get(i).getMessage());
			}
			
			return result;
		}
	}
	
	private double[] combineAllLeaves() {

		if (messagesA.size() == 0) {
			// System.out.println("No leaves!");
			return new double[] {1,0,1,0};
		} else if (messagesA.size() == 1) {
			double[] leaf = new double[4];
			
			leaf[0] = messagesA.get(0).getMessage()[0];
			leaf[1] = messagesA.get(0).getMessage()[2];
			leaf[2] = messagesA.get(0).getMessage()[1];
			leaf[3] = messagesA.get(0).getMessage()[3];
			return leaf;
			
		} else {
			double[] leaves = messagesA.get(0).getMessage();
			
			for (int i = 1; i < messagesA.size(); i++) {
				if (i == 1) {
					leaves = calc.dTSX(leaves, messagesA.get(1).getMessage());
				} else {
					leaves = calc.tSX(messagesA.get(i).getMessage(), leaves);
				}
			}
			
			return leaves;
		}
	}
	
	private double[] combineAllLeaves(Node except) {
		ArrayList<Message> mA = removeLeafMessage(except);
		
		if (mA.isEmpty()) {
			return new double[]{1,0,1,0};
			
		} else if (mA.size() == 1){
			double[] leaf = new double[4];
			
			leaf[0] = mA.get(0).getMessage()[0];
			leaf[1] = mA.get(0).getMessage()[2];
			leaf[2] = mA.get(0).getMessage()[1];
			leaf[3] = mA.get(0).getMessage()[3];
			
			return leaf;
			
		} else {
			double[] leaves = mA.get(0).getMessage();	
			
			for (int i = 1; i < mA.size(); i++) {
				// leaves = calc.dTSS(leaves, mA.get(i).getMessage());
				if (i == 1) {
					leaves = calc.dTSX(leaves, mA.get(1).getMessage());
				} else {
					leaves = calc.tSX(mA.get(i).getMessage(), leaves);
				}
			}
			
			return leaves;
		}
	}

	private double[] marginalize() {
		double[] result = new double[4]; 
		
		if (isLeaf) {
			result = calc.dot(softInfo, messagesB.get(0).getMessage());
		} else {

			double[] leaves = combineAllLeaves();
			double[] internals = combineAllInternal();
			if (internals != null && messagesA.size() != 0) {
				
				// leaves and internals exist
				double[] messages = calc.dSX(internals, leaves);
				result = calc.dot(messages, softInfo);
				
			} else if (internals == null && messagesA.size() > 0) {
				// only leaves exist
				result = calc.dot(leaves, softInfo);
				
			} else if (internals != null && messagesA.size() == 0) {
				// only internals
				result = calc.dot(softInfo, internals);
				
			}
		}
		
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
	
	private ArrayList<Message> removeLeafMessage(Node except) {
		ArrayList<Message> mA = new ArrayList<Message>();
		
		mA.addAll(messagesA);
		int index = -1;
		for (Message m: mA) {
			if (m.getSenderName().equals(except.getNodeName())) {
				index = mA.indexOf(m);
			}
		}
		mA.remove(index);
		return mA;
	}

	private ArrayList<Message> removeInternalMessage(Node except) {
		ArrayList<Message> mB = new ArrayList<Message>();
		
		mB.addAll(messagesB);
		int index = -1;
		for (Message m: mB) {
			if (m.getSenderName().equals(except.getNodeName())) {
				index = mB.indexOf(m);
			}
		}
		mB.remove(index);
		return mB;
		
	}

	@Override
	public void receiveSoftInfo(double[] softInfo) {
		this.softInfo = softInfo;
	}


	@Override
	public void passInitialMessages() {
		double[] identityMessage = {1.0,1.0,1.0,1.0};
		for (Node n: neighbors) {
			n.receiveMessage(new Message(toString(), identityMessage));
		}
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
	public void receiveMessage(Message m) {
		System.out.println(nodeName + " <--- " + m.toString());
		
		String name = m.getSenderName();
		boolean leaf = ((Point) neighbors.get(findNeighborIndex(name))).isLeaf();
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
			if (v.getNodeName().equals(name)) {
				index = neighbors.indexOf(v);
			}
		}

		return index;
	}

	@Override
	public boolean hasLeaves() {
		return !isLeaf() && messagesA.size() > 0;
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
}

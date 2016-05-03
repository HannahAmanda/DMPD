package decoder.simple;

import java.util.ArrayList;
import java.util.List;

import message.Message;
import message.recieveMessage;
import code.graph.Node;
import f4.GF4Element;


public class Point extends Node implements recieveMessage {
	
	private double[] softInfo;
	private boolean isLeaf;
	private Calculator calc = new Calculator();
	
	private List<Message> leafMssg = new ArrayList<Message>();
	private List<Message> internalMssg = new ArrayList<Message>();
	
	public Point(int id) {
			nodeId = id;
			nodeName = "M" + id;
	}
	

	@Override 
	public void passMessageTo(Node theOther) {
		
		if (isLeaf()) {
			passSoftInfo(theOther);
			
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
			((Point) theOther).receiveMessage(new Message(nodeName, normalize(m)));
			
		} else {
			// correct for stars
			double[] m = calc.dSS(leaves, softInfo);
			((Point) theOther).receiveMessage(new Message(nodeName, normalize(m)));
		}
	}

	private void passSoftInfo(Node theOther) {
		((Point) theOther).receiveMessage(new Message(nodeName, normalize(softInfo)));
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
		
		((Point) theOther).receiveMessage(new Message(nodeName, normalize(m)));
	}
	
	private double[] combineAllInternal(Node theOther) {
		ArrayList<Message> mB = removeInternalMessage(theOther);
		
		if (mB.isEmpty()) {
			System.out.println(nodeName + " don't know what to do");
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
		
		if (internalMssg.isEmpty()) {
			return null;
		} else {
			double[] result = internalMssg.get(0).getMessage();
			
			for (int i = 1; i < internalMssg.size(); i++) {
				result = calc.dSX(result, internalMssg.get(i).getMessage());
			}
			
			return result;
		}
	}
	
	private double[] combineAllLeaves() {

		if (leafMssg.size() == 0) {
			// System.out.println("No leaves!");
			return new double[] {1,0,1,0};
		} else if (leafMssg.size() == 1) {
			double[] leaf = new double[4];
			
			leaf[0] = leafMssg.get(0).getMessage()[0];
			leaf[1] = leafMssg.get(0).getMessage()[2];
			leaf[2] = leafMssg.get(0).getMessage()[1];
			leaf[3] = leafMssg.get(0).getMessage()[3];
			return leaf;
			
		} else {
			double[] leaves = leafMssg.get(0).getMessage();
			
			for (int i = 1; i < leafMssg.size(); i++) {
				if (i == 1) {
					leaves = calc.dTSX(leaves, leafMssg.get(1).getMessage());
				} else {
					leaves = calc.tSX(leafMssg.get(i).getMessage(), leaves);
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
			result = calc.dot(softInfo, internalMssg.get(0).getMessage());
		} else {

			double[] leaves = combineAllLeaves();
			double[] internals = combineAllInternal();
			if (internals != null && leafMssg.size() != 0) {
				
				// leaves and internals exist
				double[] messages = calc.dSX(internals, leaves);
				result = calc.dot(messages, softInfo);
				
			} else if (internals == null && leafMssg.size() > 0) {
				// only leaves exist
				result = calc.dot(leaves, softInfo);
				
			} else if (internals != null && leafMssg.size() == 0) {
				// only internals
				result = calc.dot(softInfo, internals);
				
			}
		}
		
		return normalize(result);
	}
	
	@Override 
	public GF4Element getState() {
		double[] marginal = marginalize();
		
		// System.out.println(nodeName + " : " + marginal[0] + ", " + marginal[1]+ ", " + marginal[2] + ", " + marginal[3]);
		
		int index = 0;
		for (int i = 0; i < marginal.length; i++) {
			if ( marginal[index] < marginal[i]) {
				index = i;
			}
		}
		
		if (index == 0) {
			return GF4Element.ZERO;
		} else if (index == 1) {
			return GF4Element.ONE;
		} else if (index == 2) {
			return GF4Element.OMEGA;
		} else if (index == 3) {
			return GF4Element.OMEGASQ;
		}
		
		return null;
	}
	
	private ArrayList<Message> removeLeafMessage(Node except) {
		ArrayList<Message> mA = new ArrayList<Message>();
		
		mA.addAll(leafMssg);
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
		
		mB.addAll(internalMssg);
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
	public void passIdentityMessages() {
		double[] identityMessage = {1.0,1.0,1.0,1.0};
		for (Node n: neighbors) {
			((Point) n).receiveMessage(new Message(toString(), identityMessage));
		}
	}


	public boolean hasMessageFrom(Node to) {
		boolean has = false;
		if (to.isLeaf()) {
			for (Message m: leafMssg) {
				if (m.getSenderName().equals(to.getNodeName())) {
					has = true;
				}
			}
		} else {
			for (Message m: internalMssg) {
				if (m.getSenderName().equals(to.getNodeName())) {
					has = true;
				}
			}
		}
		return has;
	}

	@Override
	public void receiveMessage(Message m) {
		// System.out.println(nodeName + " <--- " + m.toString());
		
		String name = m.getSenderName();
		boolean leaf = ((Point) neighbors.get(findNeighborIndex(name))).isLeaf();
		int index = -1;

		if (leaf) {
			for (Message a: leafMssg) {
				if (a.getSenderName().equals(name)) {
					index = leafMssg.indexOf(a);
				}
			}
			
			if (index != -1) {
				leafMssg.remove(index);
			}
			leafMssg.add(m);
			
		} else {
			
			for (Message b: internalMssg) {
				if(b.getSenderName().equals(name)) {
					index = internalMssg.indexOf(b);
				}
			}
			
			if (index != -1) {
				internalMssg.remove(index);
			}
			internalMssg.add(m);
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
		return !isLeaf() && leafMssg.size() > 0;
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
		leafMssg.clear();
		internalMssg.clear();
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

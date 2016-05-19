package decoder.discr;

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
			
		} else{
			double[] leaves = new double[4];
			double[] internals = new double[4];
			if (theOther.isLeaf()) {
				leaves = combineAllLeaves(theOther);
				internals = combineAllInternal();
			
			} else {
				leaves = combineAllLeaves();
				internals = combineAllInternals(theOther);
				
			}
			
			passMessage(theOther, leaves, internals);
		}
	}


	private void passMessage(Node theOther, double[] leaves, double[] internals) {
		if (internals != null){
			double[] m = calc.dSX(leaves, softInfo);
			m = calc.dSS(m, internals);
			((Point) theOther).receiveMessage(new Message(nodeName, normalize(m)));
			
		} else {
			double[] m = calc.dSS(leaves, softInfo);
			((Point) theOther).receiveMessage(new Message(nodeName, normalize(m)));
		}
	}

	private void passSoftInfo(Node theOther) {
		((Point) theOther).receiveMessage(new Message(nodeName, normalize(softInfo)));
	}
	
	private double[] combineAllInternals(Node theOther) {
		ArrayList<Message> mB = removeInternalMessage(theOther);
		
		if (mB.isEmpty()) {
			return null;
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
	
	private double[] combineAllLeaves(Node theOther) {
		double[] leaves = new double[]{1,0,1,0};	
			
		for (int i = 0; i < leafMssg.size(); i++) {
			if(!leafMssg.get(i).getSenderName().equals(theOther.getNodeName())) {
				leaves = calc.tSX(leafMssg.get(i).getMessage(), leaves);
			}
		}
			
		return leaves;	
	}
	
	private double[] combineAllLeaves() {
		
		double[] leaves = new double[] {1,0,1,0};
		
		for (int i = 0; i < leafMssg.size(); i++) {
			leaves = calc.tSX(leafMssg.get(i).getMessage(), leaves);
		}
		
		return leaves;
	
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
				
			} else if (internals == null) {
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


	@Override
	public double[] getMarginal() {
		return marginalize();
	}
}

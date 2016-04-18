package node;

import java.util.ArrayList;
import java.util.Arrays;

import message.Calculator;
import message.Message;
import f4.Element;


public class Spot extends SimpleNode {
	
	private Calculator calc = new Calculator();
	
	
	public Spot(int id) {
		super(id);
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
			theOther.receiveMessage(new Message(nodeName, m));
			
		} else {
			// correct for stars
			double[] m = calc.dSS(leaves, softInfo);
			theOther.receiveMessage(new Message(nodeName, m));
		}
	}


	private void passLeafInfo(Node theOther) {
		theOther.receiveMessage(new Message(nodeName, softInfo));
	}


	private void passToInternal(Node theOther) {
		double[] leaves = combineAllLeaves();
		double[] internals = combineAllInternal(theOther);
		
		double[] message = new double[4];
		if (internals != null) {
			message = calc.dSX(leaves,  internals);
			message = calc.dSS(message,  softInfo);
			
		} else {
			message = calc.dSS(leaves,  softInfo);
		}
		
		theOther.receiveMessage(new Message(nodeName, message));
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
			System.out.println("No leaves!");
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
		
		return result;
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
			if (m.getSenderName().equals(except.nodeName)) {
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
			if (m.getSenderName().equals(except.nodeName)) {
				index = mB.indexOf(m);
			}
		}
		mB.remove(index);
		return mB;
		
	}
	
}

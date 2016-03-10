package node;

import java.util.ArrayList;
import java.util.Arrays;

import message.Calculator;
import message.Message;
import f4.Element;

public class Dot extends SimpleNode {
	
	private Calculator calc = new Calculator();

	public Dot(int id) {
		super(id);
	}

	@Override
	public void passMessageTo(Node theOther) {
		if (isLeaf) {
			theOther.receiveMessage(new Message(nodeName, softInfo));
			
		} else if (theOther.isLeaf()) {
			double[] leaves = combineAllLeaves(theOther);
			
			if (messagesB.isEmpty()) {
				double[] m = calc.tSS(leaves, softInfo);
				theOther.receiveMessage(new Message(nodeName, m));
			
			} else {
				double[] comb = calc.tSX(leaves, softInfo);
				double[] m = calc.tSS(messagesB.get(0).getMessage(), comb);
				theOther.receiveMessage(new Message(nodeName, m));
				
			}
			
		} else if (!theOther.isLeaf()) {
			double[] leaves = combineAllLeaves();
			
			if (isTheOnlyOne(theOther)) {
				double[] m = calc.dSX(leaves, softInfo);
				
				theOther.receiveMessage(new Message(nodeName, m));
			} else {
				System.out.println("not the only other one." );
				double[] internals = combineAllInternal(theOther);
				
				if (messagesA.size() == 0) {
					System.out.println("No Leaves");
					double[] m = calc.tSS(internals, softInfo);
					theOther.receiveMessage(new Message(nodeName, m));
				}
			
			}
			
		}
		
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
	
	private double[] marginalize() {
		
		if (isLeaf) {
			return calc.dot(softInfo, messagesB.get(0).getMessage());
			
		} else if (onlyLeaves()) {
			double[] leaves = combineAllLeaves();
			
			return calc.dot(leaves, softInfo);
			
			
		} else if (onlyInternal()) {
			
			double[] internal = combineAllInternal();
			
			return calc.dot(softInfo, internal);
			
		} else {
			double[] comb = calc.tSX(combineAllInternal(), combineAllLeaves());
			
			return calc.dot(comb, softInfo);
			
		}
	}

	private double[] combineAllInternal() {
		double[] result = new double[]{1,1,1,1};
		if (messagesB.size() > 1) {
			for (int i = 1; i < messagesB.size(); i++) {
				if (i == 1) {
					result = calc.dTSX(messagesB.get(0).getMessage(), messagesB.get(1).getMessage());
				} else {
					System.out.println("guess work!");
					result = calc.tSX(messagesB.get(i).getMessage(), result);
				}
			}
		} else if (messagesB.size() == 1) {
			return messagesB.get(0).getMessage();
			
		}
		return result;
	}
	
	private double[] combineAllInternal(Node theOther) {
		double[] result = new double[]{1,1,1,1};
		
		ArrayList<Message> mB = removeMessage(theOther);
		
		if (mB.size() == 0) {
			System.out.println("There are no more internal nodes.");
			return new double[]{1,1,1,1};
		
		} else if (mB.size() == 1) {
			return mB.get(0).getMessage();
			
		}
		
		return null;
	}

	private double[] combineAllLeaves(){
		
		if (onlyLeaves()) {
			double[] leaves = new double[]{1,1,1,1};
			
			for (int i = 1; i < messagesA.size(); i++) {
				if (i == 1) {
					leaves = calc.dTSX(messagesA.get(0).getMessage(), messagesA.get(1).getMessage());
				} else {
					leaves = calc.tSX(messagesA.get(i).getMessage(), leaves);
				}
			}
			
			return leaves;
			
		} else {
		
			if (messagesA.isEmpty()) {
				return null;
				
			} else if (messagesA.size() == 1) {
				return messagesA.get(0).getMessage();
			
			} else if (messagesA.size() > 1) {
				double[] base = calc.dTSX(messagesA.get(0).getMessage(), messagesA.get(1).getMessage());
				
				for (int i = 2; i < messagesA.size(); i++) {
					base = calc.tSX(messagesA.get(i).getMessage(), base);
				}
				
				return base;
			} 
		}
		
		return null;
	}
	
	private double[] combineAllLeaves(Node except) {
		ArrayList<Message> mA = removeMessage(except);
		
		if (onlyLeaves()) {
			if (mA.isEmpty()) {
				return null;
				
			} else if (mA.size() == 1) {
				System.out.println("mA == 1");
				return mA.get(0).getMessage();
			
			} else if (mA.size() > 1) {
				double[] base = calc.dTSS(mA.get(0).getMessage(), mA.get(1).getMessage());
				
				for (int i = 2; i < mA.size(); i++) {
					base = calc.dTSS(base, mA.get(i).getMessage());
				}
				
				return base;
			} 
		} else {
			
			if (mA.isEmpty()) {
				return null;
			} else if (mA.size() == 1) {
				return mA.get(0).getMessage();
				
			} else if (mA.size() > 1) {
				double[] base = calc.dTSS(mA.get(0).getMessage(), mA.get(1).getMessage());
				
				for (int i = 2; i < mA.size(); i++) {
					base = calc.dTSS(base, mA.get(i).getMessage());
				}
				
				return base;
				
			}
		}
		return null;
	}
	
	private ArrayList<Message> removeMessage(Node except) {
		if (except.isLeaf()){
			ArrayList<Message> mA = new ArrayList<Message>();
			mA.addAll(messagesA);
			
			int[] index = new int[messagesA.size()];
			int count = 0;
			
			for (Message m: mA) {
				if (m.getSenderName().equals(except.nodeName)) {
					index[count] = mA.indexOf(m);
					count++;
				}
			}

			for (int i = 0; i < count; i++) {
				mA.remove(index[i]);
			}
			
			return mA;
		} else {
			ArrayList<Message> mB = new ArrayList<Message>();
			mB.addAll(messagesB);
			
			int[] index = new int[messagesB.size()];
			int count = 0;
			
			for (Message m: mB) {
				if (m.getSenderName().equals(except.nodeName)) {
					index[count] = mB.indexOf(m);
					count++;
				}
			}

			for (int i = 0; i < count; i++) {
				mB.remove(index[i]);
			}
			
			return mB;
			
		}
	}

	private boolean isTheOnlyOne(Node theOther) {
		return (messagesB.size() == 1 && messagesB.get(0).getSenderName().equals(theOther.nodeName));
	}
	
	private boolean onlyInternal() {
		return (!messagesB.isEmpty() && messagesA.isEmpty());
	}

	private boolean onlyLeaves() {
		return (messagesB.isEmpty() && !messagesA.isEmpty());
	}

}

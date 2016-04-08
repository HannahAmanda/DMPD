package node;

import java.util.ArrayList;

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
		
	}
	
	private double[] marginalize() {
		double[] result = new double[]{1,1,1,1}; 
		
		if (isLeaf) {
			result = calc.dot(softInfo, messagesB.get(0).getMessage());
		} else {

			// 1.) Combine leaves
			// 2.) Combine internals
			// 3.) Combine 1. and 2. with softInfo
			
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
	

	private ArrayList<Message> removeMessage(Node except) {
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


	
}

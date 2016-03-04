package node;

import java.util.ArrayList;

import message.Message;
import f4.Element;


public class Dot extends SimpleNode {

	public Dot(int id) {
		super(id);
	}
	


	@Override 
	public void passMessageTo(Node theOther) {
		
		if (isLeaf) {
			theOther.receiveMessage(new Message(nodeName, softInfo));
			
		} else {
			
			if (theOther.isLeaf()) {
				
				// star
				if(messagesB.isEmpty()) {
					theOther.receiveMessage(new Message(nodeName, greekVector(theOther)));
				
				// both leaves and internal nodes as neighbors
				} else if (!messagesB.isEmpty()) {
					
					System.out.println("ONE. you don't know how to calculate this.");
				}
			} else {
				if (messagesB.size() == 1) {
					theOther.receiveMessage(new Message(nodeName, greekVector()));
				
				} else if (messagesA.isEmpty()) {
					
					System.out.println("TWO. you don't know how to calculate this." );
					
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

	
	private double[] doubleTwistedSXSX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[0]*n[2] + m[2]*n[0];
		double c = m[1]*n[1] + m[3]*n[3];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}
	
	private double[] twistedSSXX(double[] n, double[] m) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[2];
		double b = m[2]*n[1] + m[3]*n[3];
		double c = m[0]*n[2] + m[1]*n[0];
		double d = m[2]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}
	
	private double[] twistedSXSX(double[] t, double[] p) {
		
		double a = t[0]*p[0] + t[2]*p[1];
		double b = t[0]*p[1] + t[2]*p[0];
		double c = t[1]*p[2] + t[3]*p[3];
		double d = t[1]*p[3] + t[3]*p[2];
		
		return new double[]{a,b,c,d};
		
	}
	
	private double[] marginalize() {
		double[] result; 
		
		if (isLeaf) {
			result = pointwiseProduct(softInfo, messagesB.get(0).getMessage());
		} else {
			
			if (messagesB.isEmpty()) {
				result = greekVector();
			} else {
				result = dividedSXSX(messagesB.get(0).getMessage(), greekVector());
			}
		}
		
		
		return result;
	}
	
	
	private double[] greekVector(){
		double[] result = new double[]{1,1,1,1};
		
		for (int i = 1; i < messagesA.size(); i++) {
			if (i == 1) {
				result = doubleTwistedSXSX(messagesA.get(0).getMessage(), messagesA.get(1).getMessage());
			} else {
				result = twistedSXSX(messagesA.get(i).getMessage(), result);
			}
		}
		
		return pointwiseProduct(result, softInfo);	
	}
	
	private double[] greekVector(Node except) {
		double[] result = new double[]{1,1,1,1};
		boolean first = false;
		
		ArrayList<Message> mA = removeMessagesFromRecipient(except);
		
		if (except.isLeaf()) { 
			int i = 0;
			while (i < mA.size()) {
				if (!first) {
					result = doubleTwistedSSXX(mA.get(i).getMessage(), mA.get(++i).getMessage());
					first = true;
					i++;
				} else if (first){
					result = doubleTwistedSSXX(result, mA.get(i).getMessage());		
					i++;
				}
				
			}
			
			result = twistedSSXX(result, softInfo);
		}
		
		return result;
	}

	
	
	private ArrayList<Message> removeMessagesFromRecipient(Node except) {
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



	private double[] doubleTwistedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[1]*n[1] + m[3]*n[3];
		double c = m[0]*n[2] + m[2]*n[0];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}

	private double[] dividedSXSX(double[] m, double[] n) {
		
		double a = m[0]*n[0] + m[1]*n[1];
		double b = m[0]*n[1] + m[1]*n[0];
		double c = m[2]*n[2] + m[3]*n[3];
		double d = m[2]*n[3] + m[3]*n[2];		
		
		return new double[]{a,b,c,d};
	}


	private double[] pointwiseProduct(double[] result, double[] softInfo) {
		double a = result[0]*softInfo[0];
		double b = result[1]*softInfo[1];
		double c = result[2]*softInfo[2];
		double d = result[3]*softInfo[3];
		
		return new double[]{a,b,c,d};
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

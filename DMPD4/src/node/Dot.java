package node;

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
			} /*else {
				
				if (messagesB.size() == 1) {
					theOther.receiveMessage(new Message(nodeName, greekVector()));
				
				} else if (messagesA.isEmpty()) {
					
					System.out.println("TWO. you don't know how to calculate this." );
					
				}
				
			}*/
			
			
			
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
	
	private double[] twistedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[2];
		double b = m[2]*n[1] + m[3]*n[3];
		double c = m[0]*n[2] + m[1]*n[0];
		double d = m[2]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}
	
	private double[] marginalize() {
		double[] result; 
		
		if (isLeaf) {
			result = pointwiseProduct(softInfo, messagesB.get(0).getMessage());
		} else {
			result = greekVector();
		}
		
		
		return result;
	}
	
	
	private double[] greekVector(){
		double[] result = new double[]{1,1,1,1};
		/*for (Message m: messagesA) {
			result = doubleTwistedSXSX(result, m.getMessage());
		}*/
		
		result = doubleTwistedSXSX(messagesA.get(0).getMessage(), messagesA.get(1).getMessage());
		
		return pointwiseProduct(result, softInfo);	
	}
	
	private double[] greekVector(Node except) {
		double[] result = new double[]{1,1,1,1};
		
		if (except.isLeaf()) {
			/*for (Message m: messagesA) {
				if (!m.getSenderName().equals(except.nodeName)) {
					result = twistedSSXX(result, m.getMessage());
				}
			}*/
			
			result = twistedSSXX(softInfo, messagesA.get(1).getMessage());
			
		}
		
		return result;
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

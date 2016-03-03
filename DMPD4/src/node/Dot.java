package node;

import f4.Element;


public class Dot extends SimpleNode {

	public Dot(int id) {
		super(id);
	}
	


	@Override 
	public void passMessageTo(Node theOther) {
		
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
		return null;
	}
	
	
	private double[] greekVector(){
		return null;	
	}
	
	private double[] greekVector(Node except) {
		return null;
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
		double b = m[0]*n[1] + m[1]*n[0];
		double c = m[2]*n[2] + m[3]*n[3];
		double d = m[2]*n[3] + m[3]*n[2];
		
		result = new double[] {a,c,b,d};
		
		return normalize(result);
	}
	
	private double[] twistedSXSX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[0]*n[2] + m[2]*n[0];
		double c = m[1]*n[1] + m[3]*n[3];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[] {a,b,c,d};
		
		return normalize(result);
	}
	
	private double[] twistedSSXX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[0]*n[2] + m[2]*n[0];
		double c = m[1]*n[1] + m[3]*n[3];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[] {a,c,b,d};
		
		return normalize(result);
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

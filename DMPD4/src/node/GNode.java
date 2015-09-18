package node;

import f4.Element;

public class GNode extends Vertex {
	
	private Variable n;
	
	private double p; 
	private double q;
	private Element bit;
	private int bitNumber;
	
	public GNode(Variable n, double p) {
		this.p = p;
		q = (1-p)/3;
		this.n = n;
	}

	public void setRecievedBit(Element bit) {
		this.bit = bit;
		if (bit == Element.ZERO) {
			bitNumber = 0;
		} else if (bit == Element.ONE) {
			bitNumber = 1;
		} else if ( bit == Element.OMEGA) {
			bitNumber = 2;
		} else {
			bitNumber = 3;
		}
	}
	
	public Variable getNeighbor() {
		return n;
	}
	public Element getBit() {
		return bit;
	}
	
	@Override
	public double[] calculateTransmission(Vertex node) {
		double[] softInformation = new double[4];
		for (int i = 0; i < softInformation.length; i++) {
			if ( i == bitNumber) {
				softInformation[i] = p;
			} else {
				softInformation[i] = q;
			}
		}
		
		return softInformation;
	}
	
	@Override
	public void passMessageTo(Vertex to) {
		n.receiveSoftInfo(calculateTransmission(n));
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

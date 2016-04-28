package code.graph;

import java.util.Arrays;
import java.util.Random;

import code.graph.Node;
import f4.GF4Element;

public class GNode {
	
	private Node n;
	
	private String nodeName;
	private double p; 
	private double q;
	private GF4Element bit;
	private int bitNumber;
	private static final Random rand = new Random(67);
	private double[] softInfo;
	
	
	public GNode(Node n, double p) {
		this.p = p;
		q = (1-p)/3;
		this.n = n;
		nodeName = "g" + n.getNodeId();
		softInfo = new double[4];
	}

	public void setRecievedBit(GF4Element bit) {
		this.bit = bit;
		if (bit == GF4Element.ZERO) {
			bitNumber = 0;
		} else if (bit == GF4Element.ONE) {
			bitNumber = 1;
		} else if ( bit == GF4Element.OMEGA) {
			bitNumber = 2;
		} else {
			bitNumber = 3;
		}
		
		for( int i = 0; i < 4; i++) {
			if ( i == bitNumber) {
				softInfo[bitNumber] = p;
			} else {
				softInfo[i] = q;
			}
		}
		
	}

	public Node getNeighbor() {
		return n;
	}
	
	public GF4Element getBit() {
		return bit;
	}
	
	public double[] getSoftInfo() {
		return softInfo;
	}

	public int getId() {
		return n.getNodeId();
	}
	
	
	private double[] calculateTransmission() {
		double[] softInformation = new double[4];
		for (int i = 0; i < softInformation.length; i++) {
			if ( i == bitNumber) {
				softInformation[i] = p*10;
			} else {
				softInformation[i] = q*10;
			}
		}
		
		return softInformation;
	}
	

	public void passChannelInfo() {
		// double[] softInfo = {randomNumber(), randomNumber(), randomNumber(), randomNumber()};
		// double[] softInfo = {0.8, 0.1, 0.1, 0.1};
		/*if (this.getId() == 0 || this.getId() == 1) {
			double[] s = {0.1, 0.8, 0.1, 0.1};
			softInfo = s;
		}*/
		// this.softInfo = softInfo;
		
		// System.out.println(this.toString() + " -> " + n.toString() + ": " + Arrays.toString(softInfo)); 
		n.receiveSoftInfo(softInfo);
		
	}

	
	@Override
	public String toString() {
		return nodeName;
	}
	
	public static double randomNumber() {
		return (rand.nextDouble());
	}
	
	
}

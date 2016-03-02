package channel;

import java.util.Arrays;
import java.util.Random;

import node.Node;
import f4.Element;

public class GNode {
	
	private Node n;
	
	private String nodeName;
	private double p; 
	private double q;
	private Element bit;
	private int bitNumber;
	private static final Random rand = new Random(67);
	private double[] softInfo;
	
	
	public GNode(Node n, double p) {
		this.p = p;
		q = (1-p)/3;
		this.n = n;
		nodeName = "g" + n.getNodeId();
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
	
	public Node getNeighbor() {
		return n;
	}
	
	public Element getBit() {
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
		double[] softInfo = {randomNumber(), randomNumber(), randomNumber(), randomNumber()};
		n.receiveSoftInfo(softInfo);
		System.out.println(this.toString() + " -> " + n.toString() + ": " + Arrays.toString(softInfo));
		this.softInfo = softInfo; 
	}

	
	@Override
	public String toString() {
		return nodeName;
	}
	
	public static double randomNumber() {
		return (rand.nextDouble());
	}
	
	
}

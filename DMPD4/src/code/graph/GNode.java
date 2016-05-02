package code.graph;
import code.graph.Node;
import f4.GF4Element;

public class GNode {
	
	private Node node;
	
	private String name;
	private double p; 
	private double q;
	private GF4Element bit;
	private int bitNumber;
	private double[] softInfo;
	
	
	
	public GNode(Node n, double p) {
		this.p = p;
		q = (1-p)/3;
		this.node = n;
		name = "g" + n.getNodeId();
		softInfo = new double[4];
	}

	public void setReceivedBit(GF4Element bit) {
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
		return node;
	}
	
	public GF4Element getBit() {
		return bit;
	}
	
	public double[] getSoftInfo() {
		return softInfo;
	}

	public int getId() {
		return node.getNodeId();
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
		node.receiveSoftInfo(softInfo);
		
	}

	
	@Override
	public String toString() {
		return name;
	}
	
}

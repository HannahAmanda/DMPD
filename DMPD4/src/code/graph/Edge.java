package code.graph;

public class Edge {
	public Node theOne;
	public Node theOther;
	
	public Edge(Node theOne, Node theOther) {
		this.theOne = theOne;
		this.theOther = theOther;
	}
	
	@Override
	public String toString() {
		return "(" + theOne.toString() + ", " + theOther.toString() + ")";
	}
}


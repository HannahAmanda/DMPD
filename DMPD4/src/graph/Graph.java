package graph;

import java.util.List;

import f4.Element;

import java.util.ArrayList;

import node.MetaNode;
import node.Node;
import channel.GNode;

public class Graph {
	
	private int iterations = 20;
	private double p;
	private boolean hasUnderlyingFG;
	
	private List<Node> nodes = new ArrayList<Node>();
	private List<GNode> gNodes = new ArrayList<GNode>();
	private List<Edge> edges = new ArrayList<Edge>();

	
	public Graph(double p){
		this.p = p;
	}
	
	public double getP() {
		return p;
	}
	
	public void setP(double p) {
		this.p = p;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public int getNrOfNodes() {
		return nodes.size();
	}
	
	public Node getNodeFromId(int i) {
		for(Node n: nodes) {
			if (n.getNodeId() == i) {
				return n;
			}
		}
		return null;
	}
	
	public void addNode(Node n){
		nodes.add(n);
		gNodes.add(new GNode(n,p));
	}
	
	public void addGNode(GNode gNode) {
		gNodes.add(gNode);
	}
	
	public void addEdge(int i, int j) {
		Node nI = getNodeFromId(i);
		Node nJ = getNodeFromId(j);
		
		nI.addNeighbor(nJ);
		nJ.addNeighbor(nI);
		
		edges.add(new Edge(nI, nJ));
	}
	
	public Element[] decode(Element[] transmission) {
		passSoftInformation(transmission);
		init();
		propagateBeliefs();
		
		return getDecodeState();
	}
	
	public void reset() {
		for (Node n: nodes) {
			n.reset();
		}
	}

	private void propagateBeliefs() {
		int it = 0;
		while (it < iterations) {
			
			for (Edge e: edges) {
				e.theOne.passMessageTo(e.theOther);
				e.theOther.passMessageTo(e.theOne);
			}			
			
			it++;
		}
		
	}

	private void init() {
		for (Node n: nodes) {
			n.passInitialMessages();
		}
		System.out.println("Initial messages passed.");	
	}

	
	private Element[] getDecodeState() {
		Element[] decodedWord = new Element[nodes.size()];
		
		for (Node x: nodes) {
			
			if( x instanceof MetaNode){
				MetaNode n = (MetaNode) x;
				
				Element decodedBit = n.getVariable().getState();
				decodedWord[n.getNodeId()] = decodedBit;
			}
		}
		
		return decodedWord;
	}
	
	
	private void passSoftInformation(Element[] transmission) {
		gNodeBitNotification(transmission);
		gNodeTransmit();
		System.out.println("Soft information passed.");
	}

	private void gNodeBitNotification(Element[] transmission) {
		for (int i = 0; i < transmission.length; i++) {
			gNodes.get(i).setRecievedBit(transmission[i]);
		}
	}

	private void gNodeTransmit() {
		for (GNode g: gNodes) {
			g.passChannelInfo();
		}
	}

	@Override
	public String toString() {
		String graph = "";
		for (Edge e: edges) {
			graph += e.toString() + "\n";
		}
		
		return graph;
	}

	public void finalSetup() {
		for (Node n: nodes) {
			n.finalSetup();
		}
	}
	
	private class Edge {
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
	
}

package graph;

import java.util.List;

import f4.Element;
import node.GNode;

import java.util.ArrayList;

public class Graph {
	
	private int iterations = 3;
	private double p;
	
	private List<MetaNode> nodes = new ArrayList<MetaNode>();
	private List<GNode> gNodes = new ArrayList<GNode>();
	private List<MetaEdge> edges = new ArrayList<MetaEdge>();

	
	public Graph(double p){
		this.p = p;
	}
	
	public double getP() {
		return p;
	}
	
	public void setP(double p) {
		this.p = p;
	}
	
	public List<MetaNode> getNodes() {
		return nodes;
	}
	
	public int getNrOfNodes() {
		return nodes.size();
	}
	
	public MetaNode getNodeFromId(int i) {
		for(MetaNode n: nodes) {
			if (n.getNodeId() == i) {
				return n;
			}
		}
		return null;
	}
	
	public void addNode(MetaNode n){
		nodes.add(n);
		gNodes.add(new GNode(n.getVariable(),p));
	}
	
	public void addGNode(GNode gNode) {
		gNodes.add(gNode);
	}
	
	public void addEdge(int i, int j) {
		MetaNode nI = getNodeFromId(i);
		MetaNode nJ = getNodeFromId(j);
		nI.addNeighbor(nJ);
		nJ.addNeighbor(nI);
		
		edges.add(new MetaEdge(nI, nJ));
		
	}
	
	public Element[] decode(Element[] transmission) {
		passSoftInformation(transmission);
		init();
		propagateBeliefs();
		
		return getDecodeState();
	}
	
	public void reset() {
		for (MetaNode n: nodes) {
			n.reset();
		}
	}
	
	private Element[] getDecodeState() {
		Element[] decodedWord = new Element[4];
		
		for (MetaNode n: nodes) {
			Element decodedBit = n.getVariable().getState();
			decodedWord[n.getNodeId()] = decodedBit;
		}
		
		return decodedWord;
	}

	private void propagateBeliefs() {
		// System.out.println("BELIEF PROPOGATION");
		// System.out.println();
		int it = 0;
		while (it < iterations) {
			// System.out.println("ITERATION: " + (it+1));
			for (MetaEdge e: edges) {
				e.theOne.passMessageTo(e.theOther);
				e.theOther.passMessageTo(e.theOne);
			}
			
			it++;
		}
		
	}

	private void init() {
		// initial message-passing
		for (MetaNode n: nodes) {
			n.passInitialMessages();
		}
		// System.out.println("Initial messages passed.");
		
	}
	
	private void passSoftInformation(Element[] transmission) {
		gNodeBitNotification(transmission);
		gNodeTransmit();
		// System.out.println("Soft information passed.");
	}

	private void gNodeBitNotification(Element[] transmission) {
		for (int i = 0; i < transmission.length; i++) {
			gNodes.get(i).setRecievedBit(transmission[i]);
		}
	}

	private void gNodeTransmit() {
		for (GNode g: gNodes) {
			g.passMessageTo(g.getBuddy());
		}
		
	}
	
	public void sortNeighbors() {
		for (MetaNode m: nodes) {
			m.sortNeighbors();
		}
		
	}

	@Override
	public String toString() {
		String graph = "";
		for (MetaEdge e: edges) {
			graph += e.toString() + "\n";
		}
		
		return graph;
	}
	
	private class MetaEdge {
		public MetaNode theOne;
		public MetaNode theOther;
		
		public MetaEdge(MetaNode theOne, MetaNode theOther) {
			this.theOne = theOne;
			this.theOther = theOther;
		}
		
		@Override
		public String toString() {
			return "(" + theOne.toString() + ", " + theOther.toString() + ")";
		}
	}

	public void generateIndicatorVectors() {
		sortNeighbors();
		for (MetaNode n: nodes) {
			n.generateIndicatorVector();
		}
				
	}
	
}

package graph;

import java.util.List;

import f4.Element;
import f4.F4;

import java.util.ArrayList;

import node.MetaNode;
import node.Node;
import node.SimpleNode;
import channel.GNode;

public class Graph {
	
	private int iterations = 1;
	private double p;
	private boolean fg;
	private boolean isTree; 
	
	private List<Node> nodes = new ArrayList<Node>();
	private List<GNode> gNodes = new ArrayList<GNode>();
	private List<Edge> edges = new ArrayList<Edge>();
	
	private List<ArrayList<Element>> adjMatrix = new ArrayList<ArrayList<Element>>();
	
	public Graph(double p, boolean tree, int factorgraph){
		this.p = p;
		this.isTree = tree;
		if (factorgraph == 0) {
			fg = false;
		} else if (factorgraph == 1) {
			fg = true;
		} else {
			fg = false;
		}
		
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
		
		System.out.println();
		
		return getDecodeState();
	}
	
	public void reset() {
		for (Node n: nodes) {
			n.reset();
		}
	}

	private void propagateBeliefs() {
		
		if (isTree) {
			traverseTree();
		} else {
			iterateEdges();
		}
	}

	private void traverseTree() {
		passMessagesFromLeaves(nodes.size()/2);
		System.out.println("passed messages from leaves.");
		System.out.println();
		
		passMessagesToLeaves(nodes.size()/2);
		System.out.println("passed messages to leaves.");
		System.out.println();
	}

	private void passMessagesToLeaves(int i) {
		Node from = getNodeFromId(i);
		if (from.isLeaf()) {
			from = from.getNeighborList().get(0);
		}
		
		for (Node to: from.getNeighborList()) {
				from.passMessageTo(to);
				passMessagesToLeaves2(to, from);
		}
	}

	private void passMessagesToLeaves2(Node to, Node from) {
		if (to.getNeighborList().size() == 1) {
			return;
		} else {
			for (Node n: to.getNeighborList()) {
				if (!n.equals(from)) {
					to.passMessageTo(n);
					passMessagesToLeaves2(n, to);
				}
			}
		}
	}

	private void passMessagesFromLeaves(int i) {

		Node root = getNodeFromId(i);
		System.out.println("root " + root.getNodeName());
		System.out.println();
		
		if (root.isLeaf()) {
			Node parent = root.getNeighborList().get(0);
			for (Node n:parent.getNeighborList()) {
				passMessagesFromLeaves2(n,parent);
			}
		} else {
			for (Node n:root.getNeighborList()) {
				passMessagesFromLeaves2(n,root);
			}
		}
		return;
	}
	
	private void passMessagesFromLeaves2(Node from, Node to) {
		if (from.getNeighborList().size() ==1) {
			from.passMessageTo(to);
			return;
		} else {
			for (Node n:from.getNeighborList()) {
				if (!n.equals(to)) {
					passMessagesFromLeaves2(n, from);
				}
			}
			from.passMessageTo(to);
		}
	}

	private void iterateEdges() {
		int it = 0;
		while (it < iterations) {
			
			for (Edge e: edges) {
				System.out.println(e.toString());
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
		System.out.println();
	}

	private Element[] getDecodeState() {
		Element[] decodedWord = new Element[nodes.size()];
		
		for (Node n: nodes) {		
			Element decodedBit = n.getState();
			decodedWord[n.getNodeId()] = decodedBit;
		}
		
		return decodedWord;
	}
	
	private void passSoftInformation(Element[] transmission) {
		System.out.println();
		gNodeBitNotification(transmission);
		gNodeTransmit();
		System.out.println("Soft information passed.");
		System.out.println();
	}

	private void gNodeBitNotification(Element[] transmission) {
		System.out.println(transmission.length);
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


	
	
	public GNode getGNode(int i) {
		for (GNode g: gNodes) {
			if (g.getId() == i) {
				return g;
			}
		}
		
		return null;
	}

	public List<ArrayList<Element>> getAdjMatrix() {
		return adjMatrix;
	}

	public void addRow(String r) {
		ArrayList<Element> row = new ArrayList<Element>();
		
		for (int i = 0; i < r.length(); i++) {
			row.add(getElement(r.charAt(i)));
		}
		
		adjMatrix.add(row);
	}

	private Element getElement(char c) {
		if (c == '0') {
			return Element.ZERO;
		} else if (c == '1') {
			return Element.ONE;
		} else if (c == 'w') {
			return Element.OMEGA;
		} else if (c == 'v') {
			return Element.OMEGASQ;
		}
		
		return null;
	}
	
}

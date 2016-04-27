package code.graph;

import java.util.List;
import f4.Element;
import java.util.ArrayList;


public class Graph {
	
	private int iterations = 10;
	private double p;
	private boolean isTree; 
	
	private List<Node> nodes = new ArrayList<Node>();
	private List<GNode> gNodes = new ArrayList<GNode>();
	private List<Edge> edges = new ArrayList<Edge>();
	
	private List<ArrayList<Element>> adjMatrix = new ArrayList<ArrayList<Element>>();
	
	public Graph(double p, boolean tree){
		this.p = p;
		this.isTree = tree;		
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
	
	public void reset() {
		for (Node n: nodes) {
			n.reset();
		}
	}
	
	public Element[] decode(Element[] transmission) {
		passSoftInformation(transmission);
		init();
		propagateBeliefs();
		
		System.out.println();
		
		return getDecodeState();
	}

	private void passSoftInformation(Element[] transmission) {
		System.out.println();
		gNodeBitNotification(transmission);
		gNodeTransmit();
	}
	
	private void propagateBeliefs() {
		
		if (isTree) {
			traverseTree();
		} else {
			iterateEdges();
		}
	}

	private void init() {
		for (Node n: nodes) {
			n.passInitialMessages();
		}
	}
	
	private void iterateEdges() {
		int it = 0;
		while (it < iterations) {
			
			for (Edge e: edges) {
				//System.out.println(e.toString());
				e.theOne.passMessageTo(e.theOther);
				e.theOther.passMessageTo(e.theOne);
			}			
			
			it++;
		}
	}
	
	private void traverseTree() {
		passMessagesFromLeaves(nodes.size()/2);
		System.out.println("Passed messages from leaves.");
		
		passMessagesToLeaves(nodes.size()/2);
		System.out.println("Passed messages to leaves.");
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

	private Element[] getDecodeState() {
		Element[] decodedWord = new Element[nodes.size()];
		
		for (Node n: nodes) {		
			Element decodedBit = n.getState();
			decodedWord[n.getNodeId()] = decodedBit;
		}
		
		return decodedWord;
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

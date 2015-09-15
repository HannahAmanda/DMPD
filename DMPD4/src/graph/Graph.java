package graph;

import java.util.List;

import f4.Element;
import node.GNode;

import java.util.ArrayList;

public class Graph {
	private List<MetaNode> nodes = new ArrayList<MetaNode>();
	private List<GNode> gNodes = new ArrayList<GNode>();
	
	
	public void addNode(MetaNode n){
		nodes.add(n);
	}
	
	public void addGNode(GNode gNode) {
		gNodes.add(gNode);
	}
	
	public void addEdge(int i, int j) {
		getNodeFromId(i).addNeighbor(getNodeFromId(j));
		getNodeFromId(j).addNeighbor(getNodeFromId(i));
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
	
	public Element[] decode(Element[] transmission) {
		passSoftInformation(transmission);
		init();
		propogateBeliefs();
		
		return getDecodeState();
	}
	
	private Element[] getDecodeState() {
		Element[] decodedWord = new Element[4];
		
		for (MetaNode n: nodes) {
			Element decodedBit = n.getVariable().getState();
			decodedWord[n.getNodeId()] = decodedBit;
		}
		
		return decodedWord;
	}

	private void propogateBeliefs() {
		// TODO Auto-generated method stub
		
	}

	private void init() {
		// initial message-passing
		for (MetaNode n: nodes) {
			n.passInitialMessages();
		}
		
	}
	
	private void passSoftInformation(Element[] transmission) {
		gNodeBitNotification(transmission);
		gNodeTransmit();
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
	

}

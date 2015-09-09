package graph;

import java.util.List;
import java.util.ArrayList;

public class Graph {
	private List<MetaNode> nodes = new ArrayList<MetaNode>();
	
	public void addNode(MetaNode n){
		nodes.add(n);
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

	public void addEdge(int i, int j) {
		getNodeFromId(i).addNeighbor(getNodeFromId(j));
		getNodeFromId(j).addNeighbor(getNodeFromId(i));
	}
	

}

package graph;

import java.util.ArrayList;
import java.util.List;

import f4.Element;
import node.Factor;
import node.GNode;
import node.Variable;

public class MetaNode {
	
	private Factor f;
	private Variable v;
	private GNode g;
	
	private int nodeId;
	private Element[] hermitianConstraint = new Element[4];
	
	private List<MetaNode> neighbors = new ArrayList<MetaNode>();
	
	public MetaNode(int i, double p) {
		
		nodeId = i;
		f = new Factor(i);
		v = new Variable(i);
		g = new GNode(p, v);
		
		makeBuddies();
	}

	
	public int getNodeId() {
		return nodeId;
	}
	
	public Factor getFactor() {
		return f;
	}
	
	public Variable getVariable() {
		return v;
	}
	
	public void setHermitianConstraint(String row) {
		for(int i = 0; i < row.length(); i++) {
			switch (row.charAt(i)) {

			case '0': hermitianConstraint[i] = Element.ZERO;
					  break;
			case '1': hermitianConstraint[i] = Element.ONE;
					  break;
			case 'v': hermitianConstraint[i] = Element.OMEGA;
					  break;
			case 'w': hermitianConstraint[i] = Element.OMEGASQ;
					  break;
			default: 
				
			}		
		}
	}
	
	public Element[] getHermitianConstraint() {
		return hermitianConstraint;
	}
	
	public void findConstraintCodewords() {
		// TODO:
		/**
		 * This method is intended to compute the constraint
		 * each underlying factor node carries with the MetaNode.
		 * 
		 * After computations have been made, the MetaNode will 
		 * notify the factor node of its definition. 
		 * 
		 * Exponential: 4^n
		 * 
		 */
		
	}
	
	
	public void addNeighbor(MetaNode n) {
		neighbors.add(n);
		f.addNeighbor(n.getVariable());
		v.addNeighbor(n.getFactor());
	}
	
	private void makeBuddies() {
		f.setBuddy(v);
		v.setBuddy(f);
	}
	
}

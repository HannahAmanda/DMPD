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
	
	private int nodeId;
	private int[] hermitianConstraint = new int[4];
	
	private List<MetaNode> neighbors = new ArrayList<MetaNode>();
	
	public MetaNode(int i) {
		
		nodeId = i;
		f = new Factor(i);
		v = new Variable(i);
				
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

			case '0': hermitianConstraint[i] = 0b00;
					  break;
			case '1': hermitianConstraint[i] = 0b01;
					  break;
			case 'v': hermitianConstraint[i] = 0b10;
					  break;
			case 'w': hermitianConstraint[i] = 0b11;
					  break;
			default: 
				
			}		
		}
	}
	
	public int[] getHermitianConstraint() {
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

	public void passInitialMessages() {
		v.passInitialMessages();
		f.passInitialMessages();
	}


	public void passMessageTo(MetaNode theOther) {
		internalMessagePassing();
		v.passMessageTo(theOther.getFactor());
		f.passMessageTo(theOther.getVariable());
		
	}


	private void internalMessagePassing() {
		f.passMessageTo(v);
		v.passMessageTo(f);	
	}


	public List<MetaNode> getNeighborList() {
		return neighbors;
	}
	
	
	
}

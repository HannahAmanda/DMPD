package decoder.embedded.metanode;


import code.graph.Node;
import decoder.embedded.factor_graph.Factor;
import decoder.embedded.factor_graph.Variable;
import f4.GF4Element;

public class MetaNode extends Node{
	
	private Factor f;
	private Variable v;
	
	public MetaNode(int i) {
		
		nodeName = "M" + i;
		nodeId = i;
		f = new Factor(i);
		v = new Variable(i);
		
		associateFactorAndVariable();
	}
	
	public Factor getFactor() {
		return f;
	}
	
	public Variable getVariable() {
		return v;
	}
	
	@Override
	public void finalSetup(){
		f.sortNeighbors();
		f.generateIndicatorVector();
	}
	
	
	@Override
	public void addNeighbor(Node m) {
		if (m instanceof MetaNode) {
			MetaNode n = (MetaNode) m;
			neighbors.add(n);
			f.addNeighbor(n.getVariable());
			v.addNeighbor(n.getFactor());
		}
	}

	@Override
	public void passIdentityMessages() {
		v.passInitialMessages();
		f.passInitialMessages();
	}

	
	@Override
	public void passMessageTo(Node m) {
		if( m instanceof MetaNode){
			MetaNode theOther = (MetaNode) m;
			v.passMessageTo(theOther.getFactor());
			f.passMessageTo(theOther.getVariable());
		}
	}
	
	public void internalMessagePassing() {
		f.passMessageTo(v);
		v.passMessageTo(f);	
	}

	public void reset() {
		f.reset();
		v.reset();
	}

	private void associateFactorAndVariable() {
		f.setAssociate(v);
		v.setAssociate(f);
	}

	@Override
	public void receiveSoftInfo(double[] softInfo) {
		v.recieveSoftInfo(softInfo);
	}


	@Override
	public GF4Element getState() {
		return v.getState();
	}

	@Override
	public boolean hasLeaves() {
		// TODO: unnecassary
		return false;
	}

	
	
}

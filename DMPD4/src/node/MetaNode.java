package node;

import message.Message;
import vertex.Factor;
import vertex.Variable;

public class MetaNode extends Node{
	
	private Factor f;
	private Variable v;
	
	public MetaNode(int i) {
		
		nodeId = i;
		f = new Factor(i);
		v = new Variable(i);
		
		makeBuddies();
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
		f.generateVector();
	}
	
	// TODO: Casting okay?
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
	public void passInitialMessages() {
		v.passInitialMessages();
		f.passInitialMessages();
	}

	
	// TODO: Casting okay?
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

	private void makeBuddies() {
		f.setBuddy(v);
		v.setBuddy(f);
	}

	@Override
	public void receiveSoftInfo(double[] softInfo) {
		v.receiveSoftInfo(softInfo);
	}

	@Override
	public void receiveMessage(Message m) {
		// TODO Auto-generated method stub
		
	}

	
	
}

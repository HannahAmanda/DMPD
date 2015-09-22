package node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import message.Message;
import node.Vertex;


public class Factor extends Vertex {
	
	private int[] constraintVector;

	public Factor(int i) {
		nodeId = i;
		nodeName = "f" + i;
	}
	
	public void setConstraint(int[] constraint) {
		constraintVector = constraint;
	}
	
	public int[] getConstraint() {
		return constraintVector;
	}
	
	@Override
	public double[] calculateTransmission(Vertex except) {
		
		/*System.out.println("NeighborList " + toString());
		for (Vertex n: neighborList) {
			System.out.println(n.toString() + " i: " + neighborList.indexOf(n));
		}*/
		
		// System.out.println("Done with neighborlist");
		// System.out.println("Calculating " + this.toString() + " transmission.");
		double[] transmission = new double[4];
		
		if (neighborList.size() == 1) {
			System.out.println("Has only one neighbor.");
			for(int i = 0; i < transmission.length; i++) {
				double value = getBinaryConstraint(constraintVector[i]);
				
				if ( value < 0 ) {
					System.out.println("Factor: Value of getBinaryConstraint is negative");
					return null;
				} else {
					transmission[i] = value;
					
				}
				
			}
			return transmission;
		} else {
			
			double[] product = productOfMessages(except);
			// System.out.println("Product: " + Arrays.toString(product));
			
			double[] summation = {0,0,0,0};
			

			int pos = neighborList.indexOf(except);
			int rep = (int) Math.pow(4, pos);
			int i = 0;
			int j = 1;
			int count = 0;

			while (i < constraintVector.length) {
				
				if ( j%(rep) == 0 ) {
					// switch to next element of F4
					summation[((count)%4)] += product[i];
					//System.out.println("i: " + i + " count: " + count +"(i % rep): " + (i%rep) + " product[i] = " + product[i] + " summation[" + (count%4)+"]");
					
					count++;
					count %= 4;
				} else {
					summation[count] += product[i];
					//System.out.println("i: " + i + " count: " + count +"(i % rep): " + (i%rep) + " product[i] = " + product[i] + " summation[" + (count)+"]");					
				}
				j++;
				i++;
			}
			
			return summation;
		}
		
	}
	
	private double[] productOfMessages(Vertex except) {
		double[] product = new double[constraintVector.length];
		for (int i = 0; i < constraintVector.length; i++) {
			product[i] = getBinaryConstraint(constraintVector[i]);
		}
		//System.out.println(Arrays.toString(product) + " constraint " + Arrays.toString(constraintVector));
		//System.out.println("Nr of neighbors: " + neighborList.size());
		for (Vertex n: neighborList) {
			if (!n.equals(except) && hasMessageFrom(n)) {
				//System.out.println(this.toString() + " has message from " + n.toString());
				int sizeN = 4;
				int repetitionLength = findLength(neighborList.indexOf(n));
				
				int i = 0;
				while (i < constraintVector.length) {
					int count = 0;
					while ( count < sizeN) {
						int j = 0;
						while (j < repetitionLength) {
							for (Message m: messageList) {
								if (m.getSender().equals(n)) {
									product[i] *= m.getMessage()[count];
								}
							}
							i++;
							j++;
						}
						count++;
					}
				}
			}
		}
		return product;
	}
	
	private int findLength(int pos) {
		if (pos == 0) {
			return 1;
		} else {
			int size = 1;
			int i = 0;
			while ( i < pos && i < neighborList.size()) {
				size *= 4;
				i++;
			}
			return size;
		}
	}

	private double getBinaryConstraint(int constraint) {
		if (constraint == 1) {
			return 1.0;
		} else if (constraint == 0) {
			return 0;
		}else {
			return -1349;
		}
	}

	/**
	 * Passes identity message to all neighboring 
	 * variable nodes such that they all have a 
	 * received message from |this|
	 */
	public void passInitialMessages() {
		double[] identityMessage = {1.0,1.0,1.0,1.0};
		
		for (Vertex n: neighborList) {
			n.receiveMessage(new Message(this, identityMessage));
		}
	}
	
	public void sortNeighbors() {
		List<Vertex> sorted = new ArrayList<Vertex>();
		int index = neighborList.size()-1;;
		while (index >= 0) {
			for (int i = 0; i < neighborList.size(); i++) {
				if (neighborList.get(i).nodeId == index) {
					sorted.add(neighborList.get(i));
					index--;
				}
			}
		}
		neighborList.clear();
		neighborList = sorted;
	}

	@Override
	public void reset() {
		messageList.clear();
	}
}

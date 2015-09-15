package node;

import java.util.ArrayList;
import java.util.List;

import message.Message;
import node.Vertex;


public class Factor extends Vertex {
	
	private List<Integer> constraintVector = new ArrayList<Integer>();

	public Factor(int i) {
		nodeId = i;
		nodeName = "f" + i;
	}
	
	public void setConstraint(List<Integer> constraint) {
		constraintVector = constraint;
	}
	
	public List<Integer> getConstraint() {
		return constraintVector;
	}
	
	@Override
	public double[] calculateTransmission(Vertex except) {
		if (canSendTo(except)) {
			double[] transmission = new double[4];
			
			if (neighborList.size() == 1) {
				for(int i = 0; i < transmission.length; i++) {
					double value = getBinaryConstraint(constraintVector.get(i));
					
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
				
				double zero = 0.0;
				double one = 0.0;
				double omega = 0.0;
				double omegasq = 0.0;
				
				int pos = neighborList.indexOf(except);
				int repetitionLength = findLength(pos);
				
				int i = 0;
				while ( i < product.length ) {
					int count = 0;
					while ( count < 4) {
						int j = 0;
						while (j < repetitionLength) {
							if ( count == 0) {
								zero += product[i];
							} else if (count == 1) {
								one += product[i];
							} else if (count == 3) {
								omega += product[i];
							} else if (count == 4) {
								omegasq += product[i];
							}
						}
					}
				}
				
				transmission[0] = zero;
				transmission[1] = one;
				transmission[2] = omega;
				transmission[3] = omegasq;
				
				return transmission;
			}
		}
		
		
		return null;
	}
	
	private double[] productOfMessages(Vertex except) {
		double[] product = new double[constraintVector.size()];
		for (int i = 0; i < constraintVector.size(); i++) {
			product[i] = getBinaryConstraint(constraintVector.get(i));
		}
		
		for (Vertex n: neighborList) {
			if (!n.equals(except) && hasMessageFrom(n)) {
				
				int sizeN = 4;
				int repetitionLength = findLength(neighborList.indexOf(n));
				
				int i = 0;
				while (i < constraintVector.size()) {
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
}

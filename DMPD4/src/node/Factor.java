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
		double[] transmission = new double[4];
		
		if (neighborList.size() == 1) {
			System.out.println("Has only one neighbor.");
			for(int i = 0; i < transmission.length; i++) {
				double value = constraintVector[i];
				
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
			
			return sumOver(product, except);
		}
		
	}
	
	private double[] sumOver(double[] product, Vertex except) {
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

	private double[] productOfMessages(Vertex except) {
		double[] product = new double[constraintVector.length];
		
		for (int i = 0; i < constraintVector.length; i++) {
			product[i] = constraintVector[i];
		}
		
		for (Vertex n: neighborList) {
			if (!n.equals(except) && hasMessageFrom(n)) {

				int repetitionLength = findRepitionLength(neighborList.indexOf(n));
				
				int i = 0;
				while (i < constraintVector.length) {
					int count = 0;
					while ( count < 4) {
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
	
	public void generateVector() {
		
		int nrOfZeros = (int) Math.pow(2, 2*(neighborList.size()-1));
		int[] rVector = findValuesForZero(nrOfZeros);
		
		int[] indicatorValues = new int[(int) Math.pow(4, neighborList.size())];
		
		for (Vertex n: neighborList) {
			int i = 0;
			while (i < indicatorValues.length) {
				
				int x = 0;
				while (x < rVector.length) {
					
					
				}
			}
		}
		
	}
	
	private int[] findValuesForZero(int zeros) {
		int[] rVector = new int[zeros];
		
		int complexity = 0;
		for (Vertex n: neighborList) {
			if (!n.equals(buddy)) {
				
				
				int repLength = findRepitionLength(neighborList.indexOf(n));
				int truthTableLength = (int) Math.pow(4,  neighborList.size());
				
				int i = 0;
				while (i < truthTableLength) {
					
					int element = 0;
					while (element < rVector.length) {
						
						int j = 0;
						while (j < repLength) {
							int x = rVector[element];
							rVector[element] = sum(sum(getB(element), x),1);
							complexity++;
							i++;
							j++;
							element++;
						}
						i += (3*repLength);
						
					}
				}
			}	
		}
		
		System.out.println(toString() + "complexity: " + complexity + " NEW: " + Arrays.toString(rVector));
		
		
		return rVector;
	}

	/**
	 * Implementation of single binary addition.
	 * 
	 * @param b - 0 || 1
	 * @param x - 0 || 1
	 * @return binary sum of b and x
	 */
	private int sum(int b, int x) {
		if (b == x) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Returns the b-value of the equation 
	 *        x = a + bw
	 * for the element in the truth table. 
	 * 
	 * @param element
	 * @return
	 */
	private int getB(int element) {
		if (element == 0 || element == 1) {
			return 0;
		} else if (element == 2 || element == 3) {
			return 1;
		}
		
		return -1349;
	}

	
	private int findRepitionLength(int pos) {
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
		
		int index = 0;
		while (index < neighborList.size()) {
			for (int i = 0; i < neighborList.size(); i++) {
				if (neighborList.get(i).nodeId == index) {
					sorted.add(neighborList.get(i));
					index++;
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

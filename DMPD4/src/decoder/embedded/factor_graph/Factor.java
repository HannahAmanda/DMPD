package decoder.embedded.factor_graph;

import java.util.Arrays;
import java.util.Collections;

import message.Message;

public class Factor extends FactorGraphNode {

	private int[] indicatorValues;

	public Factor(int i) {
		nodeId = i;
		nodeName = "f" + i;
	}

	public void setConstraint(int[] constraint) {
		indicatorValues = constraint;
	}

	public int[] getConstraint() {
		return indicatorValues;
	}

	@Override
	public double[] calculateTransmission(FactorGraphNode except) {
		double[] transmission = new double[4];

		if (neighborList.size() == 1) {
			// System.out.println(toString() + " has only one neighbor.");
			for (int i = 0; i < transmission.length; i++) {
				double value = indicatorValues[i];

				if (value < 0) {
					System.out
							.println("Factor: Value of getBinaryConstraint is negative");
					return null;
				} else {
					transmission[i] = value;

				}

			}
			return transmission;
		} else {
			double[] product = productOfMessages(except);

			return sumOver(product, except);
		}

	}

	private double[] sumOver(double[] product, FactorGraphNode except) {
		double[] summation = { 0, 0, 0, 0 };
		int pos = neighborList.indexOf(except);
		int rep = (int) Math.pow(4, pos);
		int i = 0;
		int j = 1;
		int count = 0;

		while (i < indicatorValues.length) {

			// System.out.println(toString() + " " + except.toString() +
			// " rep: " + rep + " pos " + pos);
			if (j % (rep) == 0) {
				// switch to next element of F4
				summation[((count) % 4)] += product[i];

				count++;
				count %= 4;
			} else {
				summation[count] += product[i];

			}

			j++;
			i++;
		}
		return summation;
	}

	private double[] productOfMessages(FactorGraphNode except) {
		double[] product = new double[indicatorValues.length];

		for (int i = 0; i < indicatorValues.length; i++) {
			product[i] = indicatorValues[i];
		}

		for (FactorGraphNode n : neighborList) {
			if (!n.equals(except) && hasMessageFrom(n)) {

				int repetitionLength = findRepetitionLength(neighborList
						.indexOf(n));

				int i = 0;
				while (i < indicatorValues.length) {
					int count = 0;
					while (count < 4) {
						int j = 0;
						while (j < repetitionLength) {
							for (Message m : messageList) {
								if (m.getSenderName().equals(n.nodeName)) {
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

		int repLength = findRepetitionLength(neighborList.indexOf(buddy));
		int[] rVector = getRVector();

		int[] indicatorValues = new int[(int) Math.pow(4, neighborList.size())];

		for (int i = 0; i < rVector.length; i++) {
			int b = i / repLength;
			int index = (b * 4 * repLength) + (i % repLength);

			// add indicator value for zero at |index|
			indicatorValues[index] = rVector[i];
			//System.out.println(" index: " + index + " val: " + rVector[i]);
			
			// copy-paste indicator values for the corresponding (1,w,w2)
			int element = 1;
			while (element < 4) {
				
				
				int elementIndex = index + (element * repLength);
				indicatorValues[elementIndex] = copyPaste(element,
						rVector[i]);
				
				//System.out.println(" elemIndex: " + elementIndex + " element: " + element);

				element++;	
			}

		}

		this.indicatorValues = indicatorValues;
		System.out.println(toString() +": " + Arrays.toString(indicatorValues));

	}

	private int copyPaste(int element, int i) {
		if (element == 0 || element == 2) {
			return i;
		} else if (element == 1 || element == 3) {
			return binarySum(i, 1);
		}
		
		throw new IllegalArgumentException("Invalid element: " + element);
	}


	private int[] getRVector() {
		int repLength = findRepetitionLength(neighborList.indexOf(buddy));
		
		int nrOfZeros = (int) Math.pow(2, 2 * (neighborList.size() - 1));
		int[] rVector = new int[nrOfZeros];
		

		for (FactorGraphNode n : neighborList) {
			if (!n.equals(buddy)) {
				
				int nRepLength = findRepetitionLength(neighborList.indexOf(n));
				
				for (int i = 0; i < rVector.length; i++) {
					int bulk = (i/repLength);
					int index = 4*bulk*repLength + i%repLength;
					
					int element;
					if (nRepLength == 1) {
						element = index%4;
					} else {
						element = (index/nRepLength)%4;
					}
					int b = getB(element);
					int x = rVector[i];
					
					
					rVector[i] = binarySum(b,x);
					//System.out.println("index: " + index +" element: " + element + " rVector[i]: " + rVector[i]+ " b: " + b + " x: " + x);
				}
			}
		}
		
		for (int i = 0; i < rVector.length; i++) {
			
			rVector[i] = binarySum(rVector[i], 1);
		}

		System.out.println(toString()  +": " +  neighborList.toString()); 
		System.out.println("rVector: " + Arrays.toString(rVector));

		return rVector;
	}

	/**
	 * Implementation of single binary addition.
	 * 
	 * @param b
	 *            - 0 || 1
	 * @param x
	 *            - 0 || 1
	 * @return binary sum of b and x
	 */
	private int binarySum(int b, int x) {
		
		if (b == x) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Returns the b-value of the equation x = a + bw for the element in the
	 * truth table.
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

		throw new IllegalArgumentException("Not an element: " + element);
	}

	private int findRepetitionLength(int pos) {
		if (pos == 0) {
			return 1;
		} else {
			int size = 1;
			int i = 0;
			while (i < pos && i < neighborList.size()) {
				size *= 4;
				i++;
			}
			return size;
		}
	}

	/**
	 * Passes identity message to all neighboring variable nodes such that they
	 * all have a received message from |this|
	 */
	public void passInitialMessages() {
		double[] identityMessage = { 1.0, 1.0, 1.0, 1.0 };

		for (FactorGraphNode n : neighborList) {
			n.receiveMessage(new Message(nodeName, identityMessage));
		}
	}

	public void sortNeighbors() {
		Collections.sort(neighborList);
	}

	@Override
	public void reset() {
		messageList.clear();
	}


}

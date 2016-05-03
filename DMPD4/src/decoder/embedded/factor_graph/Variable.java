package decoder.embedded.factor_graph;

import message.Message;
import f4.GF4Element;

public class Variable extends FactorGraphNode{
	
	
	private GF4Element state;
	private int a, b;
	private boolean receivedSoftInfo = false;
	private double[] softInfo = {0,0,0,0};
	private double[] marginalization = {1, 1, 1, 1};

	public Variable(int i) {
		state = null;
		nodeId = i;
		nodeName = "x" + i;
	}
	
	public GF4Element getState() {
		marginalize();
		return state;
	}
	
	public int getB() {
		return b;
	}
	
	public int getA() {
		return a;
	}

	@Override
	public double[] calculateTransmission(FactorGraphNode except) {
		double[] transmission = {1,1,1,1};
		
		for (int i = 0; i < messages.size(); i++) {
			if (!messages.get(i).getSenderName().equals(except.nodeName)) {
				
				double[] message = messages.get(i).getMessage();
				// System.out.println(toString() + " Message: " + Arrays.toString(message) + " from: " + messageList.get(i).getSender());
				for (int j = 0; j < 4; j++) {
					// System.out.println("i: " + i + " trans[j]: " + transmission[j] + " j: " + j + " message[j] " + message[j]);
					transmission[j] *= (message[j]);
				}
			}
		}
		
		/*transmission[0] *= softInfo[0];
		transmission[1] *= softInfo[1];
		transmission[2] *= softInfo[2];
		transmission[3] *= softInfo[3];*/
		
		return transmission;
	}

	/**
	 * Merely passes along the soft information 
	 * received from the channel. 
	 */
	@Override
	public void passInitialMessages() {
		if (receivedSoftInfo) {
			for (FactorGraphNode n:neighbors) {
				double[] tran = new double[4];
				tran[0] = (softInfo[0]);
				tran[1] = (softInfo[1]);
				tran[2] = (softInfo[2]);
				tran[3] = (softInfo[3]);
				
				n.receiveMessage(new Message(nodeName, tran));
			}
			
		} else {
			System.out.println("Houstons we have a situation. " 
					+ nodeName + " not received soft info.");
		}
	}

	public void recieveSoftInfo(double[] softInfo) {
		receivedSoftInfo = true;
		this.softInfo = softInfo;
		setAB();
		// System.out.println(toString() + " Soft info: " + softInfo[0]+ ", " + softInfo[1]+ ", " + softInfo[2] + ", " + softInfo[3]);
	}

	private void setAB() {
		int j = findReceivedState();
		
		if (j == 0) {
			a = 0;
			b = 0;
		
		} else if (j == 1) {
			a = 1;
			b = 0;
			
		} else if (j == 2) {
			a = 0;
			b = 1;
			
		} else if (j == 3) {
			a = 1;
			b = 1;
		}
		
	}

	private int findReceivedState() {
		int j = 0;
		for (int i = 0; i < 4; i++) {
			if (softInfo[i] > softInfo[j]) {
				j = i;
			}
		}
		return j;
	}

	private void marginalize() {
		double[] marge = marginalization;
		if (messages.size() == neighbors.size()){
			for (Message m: messages) {
				double[] message = m.getMessage();
				marge[0] *= message[0];
				marge[1] *= message[1];
				marge[2] *= message[2];
				marge[3] *= message[3];
			}
			
			marge[0] *= softInfo[0];
			marge[1] *= softInfo[1];
			marge[2] *= softInfo[2];
			marge[3] *= softInfo[3];
			
			if(sumsUp(marge)) {
				marginalization = marge;
				updateState();	
				
			} else {
				marginalization = marge;
				updateState();
				//System.out.println(nodeName + " does not sum up: " + marge[0] + ", " + marge[1] + ", " + marge[2] + ", " + marge[3]);
			}
		} else {
			System.out.println("Setting marginalization = soft information");
			marginalization = softInfo;
		}
		
		// System.out.println(nodeName + " : " + marge[0] + ", " + marge[1] + ", " + marge[2] + ", " + marge[3]);

	}

	private boolean sumsUp(double[] marge) {
		double sum = 0.0;
		for (int i = 0; i < marge.length; i++) {
			sum += marge[i];
		}
		
		for (int i = 0; i < marge.length; i++) {
			marge[i] /= sum;
		}
		
		return (marge[0] + marge[1] + marge[2] + marge[3]) == 1.0;
	}

	private void updateState() {
		int s = 0;
		for (int i = 0; i < marginalization.length; i++){ 
			if (marginalization[i] > marginalization[s]) {
				s = i;
			}
		}
		
		switch(s) {
		case 0: state = GF4Element.ZERO;
				break;
		case 1: state = GF4Element.ONE;
				break;
		case 2: state = GF4Element.OMEGA;
				break;
		case 3: state = GF4Element.OMEGASQ;
				break;
		}
	}

	@Override
	public void reset() {
		double[] s = {0,0,0,0};
		double[] m = {1,1,1,1};
			
		marginalization = m;
		softInfo = s;
		state = null;
		receivedSoftInfo = false;
		messages.clear();	
		
	}

	

	

}

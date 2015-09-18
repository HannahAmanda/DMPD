package node;

import f4.Element;
import message.Message;

public class Variable extends Vertex{
	
	private Element state;
	private boolean receivedSoftInfo = false;
	private double[] softInfo = {0,0,0,0};
	private double[] marginalization = {1, 1, 1, 1};

	public Variable(int i) {
		state = null;
		nodeId = i;
		nodeName = "x" + i;
	}
	
	public Element getState() {
		marginalize();
		return state;
	}

	@Override
	public double[] calculateTransmission(Vertex except) {
		double[] transmission = new double[4];
		
		for (int i = 0; i < messageList.size(); i++) {
			if (!messageList.get(i).getSender().equals(except)) {
				
				for (int j = 0; j < 4; j++) {
					transmission[j] = messageList.get(i).getMessage()[j];
				}
				
			}
		}
		return transmission;
	}

	/**
	 * Merely passes along the soft information 
	 * received from the channel. 
	 */
	public void passInitialMessages() {
		if (receivedSoftInfo) {
			for (Vertex n:neighborList) {
				n.receiveMessage(new Message(this, softInfo));
			}
			
		} else {
			System.out.println("Houstons we have a situation. " 
					+ nodeName + " not received soft info.");
		}
	}

	public void receiveSoftInfo(double[] softInfo) {
		receivedSoftInfo = true;
		this.softInfo = softInfo;
		// System.out.println("Soft info: " + softInfo[0]+ ", " + softInfo[1]+ ", " + softInfo[2] + ", " + softInfo[3]);
	}

	private void marginalize() {
		double[] marge = marginalization;
		if (messageList.size() == neighborList.size()){
			for (Message m: messageList) {
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
				System.out.println(nodeName + " does not sum up.");
				for (int i = 0; i < marge.length; i++) {
					System.out.print(marge[i] + " + ");
				}
				System.out.println();
				System.out.println(marge[0] + marge[1] + marge[2] + marge[3]);
			}
		} else {
			System.out.println("Setting marginalization = soft information");
			marginalization = softInfo;
		}

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
		case 0: state = Element.ZERO;
				break;
		case 1: state = Element.ONE;
				break;
		case 2: state = Element.OMEGA;
				break;
		case 3: state = Element.OMEGASQ;
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
		messageList.clear();	
		
	}

}

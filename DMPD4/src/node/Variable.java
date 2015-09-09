package node;

public class Variable extends Vertex{
	
	private GNode g;
	
	public Variable(int i) {
		nodeId = i;
		nodeName = "x" + i;
	}

	@Override
	public double[] calculateTransmission(Vertex except) {
		if (canSendTo(except)) {
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
		return null;
	}

}

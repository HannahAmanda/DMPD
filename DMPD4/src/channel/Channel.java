package channel;

import f4.GF4Element;

public interface Channel {
	public GF4Element[] sendThroughChannel(GF4Element[] message);
}

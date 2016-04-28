package channel;

import java.util.concurrent.ThreadLocalRandom;

import f4.GF4Element;

/**
 * Implemented Transmission Matrix 
 * 
 * ___|_0_|_1_|_w_|_w²_  
 *  0 | p | a | b | c
 *  1 | c | p | a | b
 *  w | b | c | p | a
 *  w²| a | b | c | p
 * 
 * ( def. 2 - http://ocw.usu.edu/Electrical_and_Computer_Engineering/Information_Theory/lecture9.pdf ) 
 */

public class QuaternarySymmetricChannel implements Channel{
	
	public double p;
	// a,b,c = q
	public double q;
	
	
	public QuaternarySymmetricChannel(double p) {
		this.p = p;
		q = ((1-p)/3);
	}
	
	
	/**
	 * Simulates sending a message consisting of Elements of GF(4)
	 * through the Quaternary Symmetric Channel.
	 * 
	 * @param message
	 * @return
	 */
	public GF4Element[] sendThroughChannel(GF4Element[] message) {
		GF4Element[] transmission = new GF4Element[message.length];
		int errorCount = 0;
		
		for (int index = 0; index < transmission.length; index++) {
			
			transmission[index] = perturb(message[index]);
			if (message[index] != (transmission[index])) errorCount++;
				
		}
		// System.out.println("Errors: " + errorCount);
		return transmission;	
	}
	
	/**
	 * Simulates the individual noise applied to each bit, 
	 * i.e. flips the bit to something different than the parameter
	 * with a probability of (1-p). 
	 * 
	 * @param element
	 * @return
	 */
	
	private GF4Element perturb(GF4Element element) {
		double multiCoin = randomNumber();		
		
		if (multiCoin <= p) {
			return element;
			
		} else if (multiCoin > p && multiCoin <= (p + q)) {
			return flip(element, "a");
			
		} else if (multiCoin > (p + q) && multiCoin <= (p + (2*q))) {
			return flip(element, "b");
			
		} else if (multiCoin > (p + (2*q)) && multiCoin <= 100) {
			return flip(element, "c");
			
		}
		
		return null;
	}


	/**
	 * According to "x" switches the given element to 
	 * a different element av GF(4).
	 * 
	 * @param element
	 * @param x
	 * @return
	 */
	private GF4Element flip(GF4Element element, String x) {
		if(element == GF4Element.ZERO) {
			if (x == "a") {
				return GF4Element.ONE;
			} else if (x == "b") {
				return GF4Element.OMEGA;
			} else if (x == "c") {
				return GF4Element.OMEGASQ;
			} else {
				// exception
			}
		} else if (element == GF4Element.ONE) {
			if (x == "a") {
				return GF4Element.OMEGA;
			} else if (x == "b") {
				return GF4Element.OMEGASQ;
			} else if (x == "c") {
				return GF4Element.ZERO;
			} else {
				// exception
			}
		} else if (element == GF4Element.OMEGA) {
			if (x == "a") {
				return GF4Element.OMEGASQ;
			} else if (x == "b") {
				return GF4Element.ZERO;
			} else if (x == "c") {
				return GF4Element.ONE;
			} else {
				// exception
			}
		} else if (element == GF4Element.OMEGASQ) {
			if (x == "a") {
				return GF4Element.ZERO;
			} else if (x == "b") {
				return GF4Element.ONE;
			} else if (x == "c") {
				return GF4Element.OMEGA;
			} else {
				// exception
			}
		} else {
			// exception
		}
		
	return null;	
		
	}

	private static double randomNumber() {
		return ThreadLocalRandom.current().nextDouble(1.1);
	}
	

}

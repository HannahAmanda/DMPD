package channel;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import f4.Element;

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

public class QSChannel {
	
	public double p;
	// a,b,c = q
	public double q;
	int distance;
	
	
	public QSChannel(double p, int distance) {
		this.p = p;
		q = ((1-p)/3);
		this.distance = distance;
	}
	
	
	/**
	 * Simulates sending a message consisting of Elements of GF(4)
	 * through the Quaternary Symmetric Channel.
	 * 
	 * @param message
	 * @return
	 */
	public Element[] sendThroughChannel(Element[] message) {
		Element[] transmission = new Element[message.length];
		int errorCount = 0;
		
			for (int index = 0; index < transmission.length; index++) {
				if (errorCount < distance ) {
					transmission[index] = perturb(message[index]);
					if (message[index] != (transmission[index])) {
						errorCount++;
					}
				} else {
					transmission[index] = Element.ZERO;
				}
		}
		System.out.println(errorCount + " " + distance);
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
	
	private Element perturb(Element element) {
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
	private Element flip(Element element, String x) {
		if(element == Element.ZERO) {
			if (x == "a") {
				return Element.ONE;
			} else if (x == "b") {
				return Element.OMEGA;
			} else if (x == "c") {
				return Element.OMEGASQ;
			} else {
				// exception
			}
		} else if (element == Element.ONE) {
			if (x == "a") {
				return Element.OMEGA;
			} else if (x == "b") {
				return Element.OMEGASQ;
			} else if (x == "c") {
				return Element.ZERO;
			} else {
				// exception
			}
		} else if (element == Element.OMEGA) {
			if (x == "a") {
				return Element.OMEGASQ;
			} else if (x == "b") {
				return Element.ZERO;
			} else if (x == "c") {
				return Element.ONE;
			} else {
				// exception
			}
		} else if (element == Element.OMEGASQ) {
			if (x == "a") {
				return Element.ZERO;
			} else if (x == "b") {
				return Element.ONE;
			} else if (x == "c") {
				return Element.OMEGA;
			} else {
				// exception
			}
		} else {
			// exception
		}
		
	return null;	
		
	}

	public static double randomNumber() {
		return ThreadLocalRandom.current().nextDouble(1.1);
	}
	

}

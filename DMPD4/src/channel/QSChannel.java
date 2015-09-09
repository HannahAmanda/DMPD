package channel;

import java.util.Random;

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
 * 
 * 
 */
public class QSChannel {
	
	private static double p;
	// a,b,c = q
	private static double q;
	
	private static final Random rand = new Random();
	
	public QSChannel(double p) {
		this.p = p;
		q = ((1-p)/3);
	}
	
	
	public static Element[] sendThroughChannel(Element[] message) {	
		Element[] transmission = new Element[message.length];
		
		for (int i = 0; i < message.length; i++ ) {
			transmission[i] = perturb(message[i]);
		}
		
		return transmission;	
	}
	
	/**
	 * TODO: UN-HARDCODE THIS METHOD
	 * For now p=40 and q=20
	 * @param element
	 * @return
	 */
	private static Element perturb(Element element) {
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


	private static Element flip(Element element, String x) {
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
		return (100*rand.nextDouble());
	}
	

}

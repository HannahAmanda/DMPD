package f4;


/**
 * 
 * _*_|_0_|_1_|_w_|_w²_  
 *  0 | 0 | 0 | 0 | 0
 *  1 | 0 | 1 | w | w²
 *  w | 0 | w | w²| 1
 *  w²| 0 | w²| 1 | w²
 * 
 * _+_|_0_|_1_|_w_|_w²_  
 *  0 | 0 | 1 | w | w²
 *  1 | 1 | 0 | w²| w
 *  w | w | w²| 0 | 1
 *  w²| w²| w | 1 | 0
 * 
 */



public class F4 {
	
	public static Element mult(Element x, Element y) {
		
		if (x.equals(Element.ZERO) || y.equals(Element.ZERO)) {
			return Element.ZERO;
		} else if (x.equals(Element.ONE)) {
			return y;
		} else if (y.equals(Element.ONE)) {
			return x;
		} else if (x.equals(Element.OMEGA)) {
			
			if (y.equals(Element.OMEGA)) {
				return Element.OMEGASQ;
			} else if (y.equals(Element.OMEGASQ)) {
				return Element.ONE;
			}
			
		} else if (x.equals(Element.OMEGASQ)) {
			
			if (y.equals(Element.OMEGA)) {
				return Element.ONE;
			} else {
				return Element.OMEGASQ;
			}
		}
		
		return null;
	}
	
	public static Element add(Element x, Element y) {
		if (x.equals(Element.ZERO)) {
			return y;
			
		} else if (y.equals(Element.ZERO)) {
			return x;
			
		} else if (x.equals(Element.ONE)) {
			
			if (y.equals(Element.ONE)) {
				return Element.ZERO;
			} else if (y.equals(Element.OMEGA)) {
				return Element.OMEGASQ;
			} else if (y.equals(Element.OMEGASQ)) {
				return Element.OMEGA;
			}
			
		} else if (x.equals(Element.OMEGA)) {
			
			if (y.equals(Element.ONE)) {
				return Element.OMEGASQ;
			} else if (y.equals(Element.OMEGA)) {
				return Element.ZERO;
			} else if (y.equals(Element.OMEGASQ)) {
				return Element.ONE;
			}
			
		} else if (x.equals(Element.OMEGASQ)) {
			
			if (y.equals(Element.ONE)) {
				return Element.OMEGA;
			} else if  (y.equals(Element.OMEGA)) {
				return Element.ONE;
			} else if (y.equals(Element.OMEGASQ)) {
				return Element.ZERO;
			}
			
		}

		return null;
	}
	
	
}

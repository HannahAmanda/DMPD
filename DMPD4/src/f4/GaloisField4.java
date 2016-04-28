package f4;




public class GaloisField4 {

	/**
	 * 
	 * _*_|_0_|_1_|_w_|_w²_  
	 *  0 | 0 | 0 | 0 | 0
	 *  1 | 0 | 1 | w | w²
	 *  w | 0 | w | w²| 1
	 *  w²| 0 | w²| 1 | w
	 * 
	 */

	public static GF4Element mult(GF4Element x, GF4Element y) {
		
		if (x.equals(GF4Element.ZERO) || y.equals(GF4Element.ZERO)) {
			return GF4Element.ZERO;
			
		} else if (x.equals(GF4Element.ONE)) {
			return y;
		
		} else if (y.equals(GF4Element.ONE)) {
			return x;
		
		} else if (x.equals(GF4Element.OMEGA)) {
			
			if (y.equals(GF4Element.OMEGA)) {
				return GF4Element.OMEGASQ;
		
			} else if (y.equals(GF4Element.OMEGASQ)) {
				return GF4Element.ONE;
			}
			
		} else if (x.equals(GF4Element.OMEGASQ)) {
			
			if (y.equals(GF4Element.OMEGA)) {
				return GF4Element.ONE;
			
			} else if (y.equals(GF4Element.OMEGASQ)){
				return GF4Element.OMEGA;
			}
		}
		
		return null;
	}

	

	 /* 
	 * _+_|_0_|_1_|_w_|_w²_  
	 *  0 | 0 | 1 | w | w²
	 *  1 | 1 | 0 | w²| w
	 *  w | w | w²| 0 | 1
	 *  w²| w²| w | 1 | 0
	 * 
	 */


	public static GF4Element add(GF4Element x, GF4Element y) {
		if (x.equals(GF4Element.ZERO)) {
			return y;
			
		} else if (y.equals(GF4Element.ZERO)) {
			return x;
			
		} else if (x.equals(GF4Element.ONE)) {
			
			if (y.equals(GF4Element.ONE)) {
				return GF4Element.ZERO;
			} else if (y.equals(GF4Element.OMEGA)) {
				return GF4Element.OMEGASQ;
			} else if (y.equals(GF4Element.OMEGASQ)) {
				return GF4Element.OMEGA;
			}
			
		} else if (x.equals(GF4Element.OMEGA)) {
			
			if (y.equals(GF4Element.ONE)) {
				return GF4Element.OMEGASQ;
			} else if (y.equals(GF4Element.OMEGA)) {
				return GF4Element.ZERO;
			} else if (y.equals(GF4Element.OMEGASQ)) {
				return GF4Element.ONE;
			}
			
		} else if (x.equals(GF4Element.OMEGASQ)) {
			
			if (y.equals(GF4Element.ONE)) {
				return GF4Element.OMEGA;
			} else if  (y.equals(GF4Element.OMEGA)) {
				return GF4Element.ONE;
			} else if (y.equals(GF4Element.OMEGASQ)) {
				return GF4Element.ZERO;
			}
			
		}

		return null;
	}
	
	
}

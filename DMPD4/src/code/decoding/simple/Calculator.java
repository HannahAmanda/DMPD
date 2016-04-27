package code.decoding.simple;

public class Calculator {
	
	public double[] dot(double[] result, double[] softInfo) {
		double a = result[0]*softInfo[0];
		double b = result[1]*softInfo[1];
		double c = result[2]*softInfo[2];
		double d = result[3]*softInfo[3];
		
		return new double[]{a,b,c,d};
	}
	
	public double[] dSS(double[] m, double[] n) {
		
		double a = m[0]*n[0] + m[1]*n[1];
		double b = m[2]*n[2] + m[3]*n[3];
		double c = m[0]*n[1] + m[1]*n[0];
		double d = m[2]*n[3] + m[3]*n[2];
		
		return new double[]{a,b,c,d};
	}
	

	public double[] dSX(double[] m, double[] n) {
		
		double a = m[0]*n[0] + m[1]*n[1];
		double b = m[0]*n[1] + m[1]*n[0];
		double c = m[2]*n[2] + m[3]*n[3];
		double d = m[2]*n[3] + m[3]*n[2];		
		
		return new double[]{a,b,c,d};
	}
	
	public double[] tSS(double[] n, double[] m) {
		double[] result;
		
		double a = m[0]*n[0] + m[1]*n[2];
		double b = m[2]*n[1] + m[3]*n[3];
		double c = m[0]*n[2] + m[1]*n[0];
		double d = m[2]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}
	
	public double[] tSX(double[] ac, double[] p) {
		double a = ac[0]*p[0] + ac[2]*p[1];
		double b = ac[0]*p[1] + ac[2]*p[0];
		double c = ac[1]*p[2] + ac[3]*p[3];
		double d = ac[1]*p[3] + ac[3]*p[2];
		
		return new double[]{a,b,c,d};
		
	}
	
	public double[] dTSS(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[1]*n[1] + m[3]*n[3];
		double c = m[0]*n[2] + m[2]*n[0];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}
	
	
	public double[] dTSX(double[] m, double[] n) {
		double[] result;
		
		double a = m[0]*n[0] + m[2]*n[2];
		double b = m[0]*n[2] + m[2]*n[0];
		double c = m[1]*n[1] + m[3]*n[3];
		double d = m[1]*n[3] + m[3]*n[1];
		
		result = new double[]{a,b,c,d};
		
		return result;
	}
	
	
	public double[] dSXLarge(double[] greek, double[] m) {
		
		double a = greek[0]*(m[0] + m[1]) + greek[1]*(m[2]+m[3]);
		double b = greek[1]*(m[0] + m[1]) + greek[0]*(m[2]+m[3]);
		double c = greek[2]*(m[0] + m[1]) + greek[3]*(m[2]+m[3]);
		double d = greek[3]*(m[0] + m[1]) + greek[2]*(m[2]+m[3]);
		
		
		return new double[]{a,b,c,d};
	}
	
	public double[] dSSLarge(double[] greek, double[] m) {
		
		double a = greek[0]*(m[0] + m[1]) + greek[1]*(m[2]+m[3]);
		double b = greek[2]*(m[0] + m[1]) + greek[3]*(m[2]+m[3]);
		double c = greek[1]*(m[0] + m[1]) + greek[0]*(m[2]+m[3]);
		double d = greek[3]*(m[0] + m[1]) + greek[2]*(m[2]+m[3]);
		
		return new double[]{a,b,c,d};
	}
	
}

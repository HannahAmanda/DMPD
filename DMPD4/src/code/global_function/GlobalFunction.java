package code.global_function;

import java.util.ArrayList;
import java.util.List;

import code.graph.Graph;
import code.graph.Node;
import f4.GF4Element;
import f4.GaloisField4;

public class GlobalFunction {
	
	
	private List<ArrayList<GF4Element>> adjMatrix = new ArrayList<ArrayList<GF4Element>>();
	private List<ArrayList<GF4Element>> codeSpace = new ArrayList<ArrayList<GF4Element>>();
	private List<ArrayList<GF4Element>> permutations = new ArrayList<ArrayList<GF4Element>>();
	
	private double[][] marginals;

	private GF4Element[] alphabet = {GF4Element.ZERO, GF4Element.ONE, GF4Element.OMEGA, GF4Element.OMEGASQ};
	
	private Graph g;
	private int nodes;
	
	public GlobalFunction(Graph g) {
		this.g = g;
		nodes  = g.getNrOfNodes();
		adjMatrix = g.getAdjMatrix();
		permutations = generatePermutations(nodes);
		marginals = new double[nodes][4];
		
		for (ArrayList<GF4Element> p: permutations) {
			if (isCodeWord(p)) {
				codeSpace.add(p); 
			} 
		}
		
		for (Node n: g.getNodes()) {
			int nodeId = n.getNodeId();
			for (int i = 0; i < 4; i++) {
				marginals[nodeId][i] = globalMarginal(nodeId, i);
			}
		}
	}
	
	public double[][] getGlobalMarginals() {
		for (int i = 0; i < nodes; i++) {
			marginals[i] = normalize(marginals[i]);
		}
		return marginals;
	}

	private ArrayList<ArrayList<GF4Element>> generatePermutations(int n) {	
		if (n == 1) {
			ArrayList<ArrayList<GF4Element>> permutations = new ArrayList<ArrayList<GF4Element>>();
			for (GF4Element e : alphabet) {
				ArrayList<GF4Element> p = new ArrayList<GF4Element>();
				p.add(e);
				permutations.add(p);
			}
			return permutations;
		} else {
			ArrayList<ArrayList<GF4Element>> perms = generatePermutations(n-1);
			ArrayList<ArrayList<GF4Element>> permutations = new ArrayList<ArrayList<GF4Element>>();
			
			for (ArrayList<GF4Element> p: perms) {
				for (GF4Element e: alphabet) {
					ArrayList<GF4Element> newp = new ArrayList<GF4Element>();
					newp.addAll(p);
					newp.add(e);
					permutations.add(newp);
				}
			}
			
			return permutations;
		}
	}
	
	private double globalMarginal(int id, int e) {
		GF4Element element = getElem(e);
		
		double channel = channelInfo(id, e);
		double sum = 0;
		
		for (ArrayList<GF4Element> c: codeSpace) {
			if (c.get(id).equals(element)) {
				double product = 1;
				for (int i = 0; i < c.size(); i++) {
					if (i != id) {
						product *= channelInfo(i, getNumberFromElement(c.get(i)));
					}
				}
				sum += product;
			}
		}
		
		double marginal = channel*sum;
		return marginal;
	}

	private boolean isCodeWord(ArrayList<GF4Element> p) {
		boolean isCodeWord = true;
		for (ArrayList<GF4Element> row: adjMatrix) {
			if (hermitianInnerProduct(row, p) != GF4Element.ZERO) {
				return false;
			}	
		}
		return isCodeWord;
	}

	private GF4Element hermitianInnerProduct(ArrayList<GF4Element> row, ArrayList<GF4Element> p2) {
		GF4Element sum = GF4Element.ZERO;
		
		for (int i = 0; i < row.size(); i++) {
			GF4Element one = GaloisField4.mult(GaloisField4.mult(row.get(i), row.get(i)), p2.get(i));
			GF4Element two = GaloisField4.mult(row.get(i), GaloisField4.mult(p2.get(i), p2.get(i)));
			
			sum = GaloisField4.add(sum, GaloisField4.add(one,two));
		}
		return sum;
	}
	
	
	private double channelInfo(int i, int e) {
		return g.getGNode(i).getSoftInfo()[e];
	}
	
	
	private int getNumberFromElement(GF4Element element) {
		if (element.equals(GF4Element.ZERO)) {
			return 0;
		} else if (element.equals(GF4Element.ONE)) {
			return 1;
		} else if (element.equals(GF4Element.OMEGA)) {
			return 2;
		} else {
			return 3;
		}
	}

	
	private GF4Element getElem(int e) {
		if (e == 0) {
			return GF4Element.ZERO;
		} else if (e == 1) {
			return GF4Element.ONE;
		} else if (e == 2) {
			return GF4Element.OMEGA;
		} else {
			return GF4Element.OMEGASQ;
		}
	}

	public void printOutCodespace() {
		for (ArrayList<GF4Element> p : codeSpace) {
			for (int i = 0; i < p.size(); i++) {
				if (i == p.size()-1) {
					System.out.println(p.get(i)+ " ");					
				} else {
					System.out.print(p.get(i) + " ");
				}
			}
		}
		System.out.println("CODESPACE " + codeSpace.size());
	}

	public double[] normalize(double[] t) {
		double sum = 0.0;
		
		for (int i = 0; i < t.length; i++) {
			sum+= t[i];
		}
		
		for (int i = 0; i < t.length; i++) {
			t[i] /= sum;
		}
		return t;
	}

	public boolean isCodeWord(GF4Element[] decoded) {
		for (ArrayList<GF4Element> c: codeSpace) {
			boolean codeword = true;
			for (int i = 0 ; i < decoded.length; i++) {
				if (!c.get(i).equals(decoded[i])) {
					codeword = false;
				}
			}
			
			if (codeword) {
				return true;
			}
		}
		return false;
	}
	
}

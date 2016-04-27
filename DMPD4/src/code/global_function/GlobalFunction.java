package code.global_function;

import java.util.ArrayList;
import java.util.List;

import code.graph.Graph;
import code.graph.Node;
import f4.Element;
import f4.GaloisField4;

public class GlobalFunction {
	
	
	private List<ArrayList<Element>> adjMatrix = new ArrayList<ArrayList<Element>>();
	private List<ArrayList<Element>> codeSpace = new ArrayList<ArrayList<Element>>();
	private List<ArrayList<Element>> permutations = new ArrayList<ArrayList<Element>>();
	
	private double[][] marginals;

	private Element[] alphabet = {Element.ZERO, Element.ONE, Element.OMEGA, Element.OMEGASQ};
	
	private Graph g;
	private int nodes;
	
	public GlobalFunction(Graph g) {
		this.g = g;
		nodes  = g.getNrOfNodes();
		adjMatrix = g.getAdjMatrix();
		permutations = generatePermutations(nodes);
		marginals = new double[nodes][4];
		
		for (ArrayList<Element> p: permutations) {
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

	private ArrayList<ArrayList<Element>> generatePermutations(int n) {	
		if (n == 1) {
			ArrayList<ArrayList<Element>> permutations = new ArrayList<ArrayList<Element>>();
			for (Element e : alphabet) {
				ArrayList<Element> p = new ArrayList<Element>();
				p.add(e);
				permutations.add(p);
			}
			return permutations;
		} else {
			ArrayList<ArrayList<Element>> perms = generatePermutations(n-1);
			ArrayList<ArrayList<Element>> permutations = new ArrayList<ArrayList<Element>>();
			
			for (ArrayList<Element> p: perms) {
				for (Element e: alphabet) {
					ArrayList<Element> newp = new ArrayList<Element>();
					newp.addAll(p);
					newp.add(e);
					permutations.add(newp);
				}
			}
			
			return permutations;
		}
	}
	
	private double globalMarginal(int id, int e) {
		Element element = getElem(e);
		
		double channel = channelInfo(id, e);
		double sum = 0;
		
		for (ArrayList<Element> c: codeSpace) {
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

	private boolean isCodeWord(ArrayList<Element> p) {
		boolean isCodeWord = true;
		for (ArrayList<Element> row: adjMatrix) {
			if (hermitianInnerProduct(row, p) != Element.ZERO) {
				return false;
			}	
		}
		return isCodeWord;
	}

	private Element hermitianInnerProduct(ArrayList<Element> row, ArrayList<Element> p2) {
		Element sum = Element.ZERO;
		
		for (int i = 0; i < row.size(); i++) {
			Element one = GaloisField4.mult(GaloisField4.mult(row.get(i), row.get(i)), p2.get(i));
			Element two = GaloisField4.mult(row.get(i), GaloisField4.mult(p2.get(i), p2.get(i)));
			
			sum = GaloisField4.add(sum, GaloisField4.add(one,two));
		}
		return sum;
	}
	
	
	private double channelInfo(int i, int e) {
		return g.getGNode(i).getSoftInfo()[e];
	}
	
	
	private int getNumberFromElement(Element element) {
		if (element.equals(Element.ZERO)) {
			return 0;
		} else if (element.equals(Element.ONE)) {
			return 1;
		} else if (element.equals(Element.OMEGA)) {
			return 2;
		} else {
			return 3;
		}
	}

	
	private Element getElem(int e) {
		if (e == 0) {
			return Element.ZERO;
		} else if (e == 1) {
			return Element.ONE;
		} else if (e == 2) {
			return Element.OMEGA;
		} else {
			return Element.OMEGASQ;
		}
	}

	public void printOutPermutations() {
		for (ArrayList<Element> p : codeSpace) {
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

	public boolean isCodeWord(Element[] decoded) {
		for (ArrayList<Element> c: codeSpace) {
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

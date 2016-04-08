

import graph.GlobalContext;
import graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import node.Dot;
import node.Spot;
import node.MetaNode;
import node.SimpleNode;
import channel.QSChannel;
import f4.Element;



public class Main {
	public static void main(String[] args) throws IOException, FileNotFoundException {
		try(BufferedReader br = new BufferedReader(new FileReader("7star.txt"))) {

			
			// INITIATE CHANNEL
			double p = Double.parseDouble(br.readLine());
			QSChannel channel = new QSChannel(p);
		
			
			// UNDERLYING FACTOR GRAPH
			int nodeType = Integer.parseInt(br.readLine());
			// TODO: contingency for when fg is not 0 or 1. 
			
			boolean tree = false;
			String t = br.readLine(); 
			if (t.equals("t")) {
				tree = true;
			}
			
			// CONSTRUCT GRAPH
			Graph g = new Graph(p, tree, nodeType);
			int nodes = Integer.parseInt(br.readLine());
			
			for (int i = 0; i < nodes; i++) {
				if (nodeType == 1) {
					g.addNode(new MetaNode(i));
				} else if (nodeType == 0) {
					g.addNode(new SimpleNode(i));
				} else if (nodeType == 2) {
					g.addNode(new Dot(i));
				} else if (nodeType == 3) {
					g.addNode(new Spot(i));
				}
			}
			
			System.out.println(g.getNodes().toString());
			
			// PARSE ADJACENCY MATRIX
			int k = 0;
			while (k < nodes) {
				String row = br.readLine();
				System.out.println(row);
				g.addRow(row);
				int j = k;
				while (j < nodes) {
					if (row.charAt(j) == '1') {
						g.addEdge(k, j);
					}
					j++;
				}
				k++;
			}
			
			
			// PRINT OUT GRAPH
			System.out.println("*** GRAPH ***");
			System.out.print(g.toString());
			System.out.println("*** ***** ***");
			
		
			// CALCULATE CONSTRAINT VECTORS
			g.finalSetup(); // IndicatorVectors made TODO: yet to be TESTED!!!
			
			
			// THE ZERO CODEWORD
			Element[] zero = new Element[nodes];
			for (int i = 0; i < nodes; i++) {
				zero[i] = Element.ZERO;
			}
			
			
			// PASS CODEWORD THROUGH CHANNEL
			//Element[] received = channel.sendThroughChannel(zero);
			
			Element[] received = {Element.ZERO, Element.ONE, Element.ZERO};
			//, Element.ZERO, Element.ONE, Element.ZERO
			// MESSAGE-PASSING
			Element[] decoded = g.decode(received);
			
			
			System.out.println();
			GlobalContext context = new GlobalContext(g);
			// context.printOutPermutations();
			double[][] marginals = context.getGlobalMarginals();
			for (int i = 0; i < marginals.length; i++) {
				System.out.print("Marginal  M" + i + ": ");
				for (int j = 0; j < marginals[i].length; j++) {
					System.out.print(marginals[i][j] + ", ");
				}
				System.out.println();
			}
			
			System.out.println();
			System.out.println("##### r" + Arrays.toString(received) + " #####");
			System.out.println("##### d" + Arrays.toString(decoded) + " #####");
			
			g.reset();
		
			
			
			/** ##################################################
			 *  ### The following code is UNDER CONSTRUCTION!! ###
			 *  ################################################## **/
			
//			// PERTURB CODESPACE
//			//Element[] 	zero 		= {Element.ZERO, Element.ZERO};
//			Element[] 	oneOmega 	= {Element.ONE, Element.OMEGA};
//			Element[] 	omegaOne 	= {Element.OMEGA, Element.ONE};
//			Element[] 	omegasq 	= {Element.OMEGASQ, Element.OMEGASQ};
//			
//			Element[][] codespace 			= {zero, oneOmega, omegaOne, omegasq};
//			Element[][] perturbedCodespace 	= new Element[4][2];
//			
//			
//			
//			for (int i = 0; i < codespace.length; i++) {
//				Element[] v = channel.sendThroughChannel(codespace[i]);
//				perturbedCodespace[i][0] = v[0];
//				perturbedCodespace[i][1] = v[1];
//			}
//			
//			
//			// DECODE PERTURBED CODESPACE
//			Element[][] decoded = new Element[4][2];
//			for (int i = 0; i < perturbedCodespace.length; i++) {
//				decoded[i] = g.decode(perturbedCodespace[i]);
//			
//				System.out.println();
//				System.out.println("Codeword : " + codespace[i][0] + ", " + codespace[i][1]);
//				System.out.println("Perturbed: " + perturbedCodespace[i][0] + ", " +  perturbedCodespace[i][1]);
//				System.out.println("Decoded  : " + decoded[i][0] + ", " + decoded[i][1]);
//				System.out.println("### ### ###");
//				System.out.println();
//			
//				g.reset();
//			}
		
			
			
		}
		
		/*catch(Exception e) {
			System.out.println(e);
		}*/
		

		
		
		
		
	}

}

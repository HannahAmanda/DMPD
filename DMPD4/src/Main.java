

import graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;

import node.MetaNode;
import channel.QSChannel;
import f4.Element;



public class Main {
	public static void main(String[] args) throws IOException, FileNotFoundException {
		try(BufferedReader br = new BufferedReader(new FileReader("IS3.txt"))) {

			// INITIATE CHANNEL
			double p = Double.parseDouble(br.readLine());
			QSChannel channel = new QSChannel(p);
		
			
			// UNDERLYING FACTOR GRAPH
			/*int fg = Integer.parseInt(br.readLine());*/
			// TODO: contingency for when fg is not 0 or 1. 
			
			
			// CONSTRUCT GRAPH
			Graph g = new Graph(p);
			int nodes = Integer.parseInt(br.readLine());
			int fg = 1;
			
			for (int i = 0; i < nodes; i++) {
				if (fg == 1) {
					g.addNode(new MetaNode(i));
				} else if (fg == 0) {
					// add Node
				}
			}
			
			System.out.println(g.getNodes().toString());
			
			// PARSE ADJACENCY MATRIX
			int k = 0;
			while (k < nodes) {
				String row = br.readLine();
				System.out.println(row);
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
			Element[] received = channel.sendThroughChannel(zero);
			
			
			// MESSAGE-PASSING
			Element[] decoded = g.decode(received);
			
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

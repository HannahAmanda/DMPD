package simulator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import simulator.decoding.embedded.metanode.MetaNode;
import simulator.decoding.simple.Point;
import code.global_function.GlobalFunction;
import code.graph.Graph;
import channel.QuaternarySymmetricChannel;
import f4.Element;


public class Main {
	public static void main(String[] args) throws IOException, FileNotFoundException {
		try(BufferedReader br = new BufferedReader(new FileReader("graphs/cyclic/IS6.txt"))) {

			
			// INITIATE CHANNEL
			double p = Double.parseDouble(br.readLine());
			QuaternarySymmetricChannel channel = new QuaternarySymmetricChannel(p);
		
			
			// TYPE OF NODE
			int nodeType = Integer.parseInt(br.readLine());
			
			boolean tree = false;
			String t = br.readLine(); 
			if (t.equals("t")) {
				tree = true;
			}
			
			// CONSTRUCT GRAPH
			Graph g = new Graph(p, tree);
			int nodes = Integer.parseInt(br.readLine());
			
			for (int i = 0; i < nodes; i++) {
				if (nodeType == 1) {
					g.addNode(new MetaNode(i));
				} else if (nodeType == 2) {
					g.addNode(new Point(i));
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
			
			
			// CREATING THE ZERO-CODEWORD
			Element[] codeword = new Element[nodes];
			for (int i = 0; i < nodes; i++) {
				codeword[i] = Element.ZERO;
			}
			
			// SEND TRANSMISSION THROUGH CHANNEL
			Element[] received = channel.sendThroughChannel(codeword);
			
			// MESSAGE-PASSING
			Element[] decoded = g.decode(received);
			
			
			System.out.println();
			GlobalFunction global = new GlobalFunction(g);
			// context.printOutPermutations();
			double[][] marginals = global.getGlobalMarginals();
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
			System.out.println("##### decoded to a codeword: " + global.isCodeWord(decoded));
			g.reset();
		
			
		} catch(Exception e) {
			System.out.println(e);
		}
		

		
		
		
		
	}

}

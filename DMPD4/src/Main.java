

import graph.Graph;
import graph.MetaNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import channel.QSChannel;
import f4.Element;
import node.Factor;
import node.GNode;
import node.Variable;
import node.Vertex;



public class Main {
	public static void main(String[] args) throws IOException, FileNotFoundException {
//		try(BufferedReader br = new BufferedReader(new FileReader("IS.txt"))) {
//
//			/**
//			 * --- ---
//			 * | w 1 |
//			 * | 1 w |
//			 * --- ---
//			 */
//			
//			
//			// CONSTRUCT GRAPH
//			Graph g = new Graph();
//			int nrNodes = Integer.parseInt(br.readLine());
//			for (int i = 0; i < nrNodes; i++) {
//				g.addNode(new MetaNode(i, 0.6));
//			}
//			
//			// PARSE ADJACENCY MATRIX
//			for (int i = 0; i < nrNodes; i++) {
//				String row = br.readLine();
//				for (int j = 0; j < nrNodes; j++) {
//					if(row.charAt(j) == '1') {
//						g.addEdge(i,j);
//					}
//				}
//			}
//		
//			
//			
//		} catch(Exception e) {
//			System.out.println(e);
//		}
		

		// GRAPH AND CHANNEL
		Graph g = new Graph();
		QSChannel channel = (new QSChannel(0.6));
		
		
		// CONSTRUCT GRAPH 
		for(int i = 0; i < 2; i++) {
			MetaNode node = new MetaNode(i);
			g.addNode(node);
			g.addGNode(new GNode(node.getVariable(), channel.p));
		}
		
		String[] adj = {"w1", "1w"};
		for(int i = 0; i < adj.length; i++) {
			for(int j = i+1; j < adj.length; j++) {
				if(adj[i].charAt(j) == '1') {
					g.addEdge(i,j);
				}
			}
		}
		
		int[] f0 = {1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1};
		int[] f1 = {1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1};
		
		g.getNodeFromId(0).getFactor().setConstraint(f0);
		g.getNodeFromId(1).getFactor().setConstraint(f1);		
		
		
		
		// PERTURB CODESPACE
		Element[] zero = {Element.ZERO, Element.ZERO};
		Element[] oneOmega = {Element.ONE, Element.OMEGA};
		Element[] omegaOne = {Element.OMEGA, Element.ONE};
		Element[] omegasq = {Element.OMEGASQ, Element.OMEGASQ};
		
		Element[][] codespace = {zero, oneOmega, omegaOne, omegasq};
		Element[][] perturbedCodespace = new Element[4][2];
		
		for (int i = 0; i < codespace.length; i++) {
			// System.out.println("Codeword: " + codespace[i][0] + "," + codespace[i][1]);
			Element[] v = channel.sendThroughChannel(codespace[i]);
			perturbedCodespace[i][0] = v[0];
			perturbedCodespace[i][1] = v[1];
			// System.out.println("Transmission: " + v[0] + "," + v[1]);
		}
		
		
		// DECODE PERTURBED CODESPACE
		Element[][] decoded = new Element[4][2];
		for (int i = 0; i < perturbedCodespace.length; i++) {
			decoded[i] = g.decode(perturbedCodespace[i]);
		
			System.out.println("Decoded : " + decoded[i][0] + "," + decoded[i][1]);
			System.out.println("Codeword: " + codespace[i][0] + "," + codespace[i][1]);
			System.out.println("### ### ###");
			System.out.println();
			
			g.reset();
		}
		
		
		
		
	}

}

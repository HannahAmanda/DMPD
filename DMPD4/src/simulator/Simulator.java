package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import channel.Channel;
import channel.QuaternarySymmetricChannel;
import code.global_function.GlobalFunction;
import code.graph.Graph;
import decoder.embedded.metanode.MetaNode;
import decoder.naive.Point;
import f4.GF4Element;

public class Simulator {
	
	private Graph graph;
	private Channel channel;
	private GlobalFunction globalFunction;

	public Simulator(double p, int nodeType, boolean isTree, int nrOfNodes, List<String> adjacencyMatrix) {
		graph = new Graph(p, isTree);
		channel = new QuaternarySymmetricChannel(p);
		
		// ADD NODES TO GRAPH
		for (int i = 0; i < nrOfNodes; i++) {
			if (nodeType == 1) {
				graph.addNode(new MetaNode(i));
			} else if (nodeType == 2) {
				graph.addNode(new Point(i));
			}
		}
		
		
		// PARSE ADJ.MATRIX and ADD EDGES
		int k = 0;
		for (String row: adjacencyMatrix) {
			graph.addRow(row);
			int j = k;
			while (j < nrOfNodes) {
				if (row.charAt(j) == '1') {
					graph.addEdge(k, j);
				}
				j++;
			}
			k++;
		}
		
		// PRINT OUT GRAPH
		System.out.println("*** GRAPH ***");
		System.out.print(graph.toString());
		System.out.println("*** ***** ***");
		
		graph.finalSetup();
	}
	
	
	public void runSimulations(int rounds) {
		for (int i = 0; i < rounds; i++) {
			simulate();
		}
	}


	private void simulate() {
		// THE ZERO CODEWORD
		GF4Element[] codeword = new GF4Element[graph.getNrOfNodes()];
		for (int i = 0; i < graph.getNrOfNodes(); i++) {
			codeword[i] = GF4Element.ZERO;
		}

		// SEND TRANSMISSION THROUGH CHANNEL
		GF4Element[] received = channel.sendThroughChannel(codeword);
		
		// MESSAGE-PASSING
		GF4Element[] decoded = graph.decode(received);
		globalFunction = new GlobalFunction(graph);
		
		System.out.println();
		System.out.println("##### recieved transmission: " + Arrays.toString(received) + " #####");
		System.out.println("##### decoded transmission: " + Arrays.toString(decoded) + " #####");
		System.out.println("Decoded to a codeword: " + globalFunction.isCodeWord(decoded));
		printOutMarginals();
		graph.reset();
		
	}
	
	public static void main(String[] args) throws IOException, FileNotFoundException{
		Scanner input = new Scanner(System.in);
	    System.out.print("Please provide a path to the code specification file: ");
	    //String file = input.nextLine();
	    String file = "graphs/trees/doubleP3.txt";
		Simulator simulator;
		
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			double p = Double.parseDouble(br.readLine());
			int nodeType = Integer.parseInt(br.readLine());
			
			boolean tree = false;
			String t = br.readLine(); 
			if (t.equals("t")) {
				tree = true;
			}
			
			int nodes = Integer.parseInt(br.readLine());
			
			List<String> adjacencyMatrix = new ArrayList<String>();
			int k = 0;
			while(k < nodes) {
				String row = br.readLine();
				adjacencyMatrix.add(row);
				k++;
			}
			
		 simulator = new Simulator(p, nodeType, tree, nodes, adjacencyMatrix);
		 if (nodeType == 1) {
			 System.out.println("Simulator using embedded factor graph generated. \nPlease, specifiy how many simulations you wish to run:");
		 } else {
			 System.out.println("Simulator using simple graph decoder generated. \nPlease specify how many simulations you wish to run:");
		 }
		 int rounds = input.nextInt();
		 
		 simulator.runSimulations(rounds);
		 
		 input.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}


	private void printOutMarginals() {
		double[][] gf = globalFunction.getGlobalMarginals();
		for (int i = 0; i < gf.length; i++){
			System.out.println("M" + i + " " + Arrays.toString(gf[i]));
			System.out.println("M" + i + " " + Arrays.toString(graph.getNodeFromId(i).getMarginal()));
		
		}
		
		//globalFunction.printOutCodespace();
		
		
	}
}

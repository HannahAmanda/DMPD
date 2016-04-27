package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import simulator.decoding.embedded.metanode.MetaNode;
import simulator.decoding.simple.Point;
import channel.Channel;
import channel.QuaternarySymmetricChannel;
import code.graph.Graph;
import f4.Element;

public class Simulator {
	
	private Graph graph;
	private Channel channel;

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
		
		// System.out.println(graph.getNodes().toString());
		
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
		Element[] codeword = new Element[graph.getNrOfNodes()];
		for (int i = 0; i < graph.getNrOfNodes(); i++) {
			codeword[i] = Element.ZERO;
		}

		// SEND TRANSMISSION THROUGH CHANNEL
		Element[] received = channel.sendThroughChannel(codeword);
		
		// MESSAGE-PASSING
		Element[] decoded = graph.decode(received);
		
		System.out.println("##### r" + Arrays.toString(received) + " #####");
		System.out.println("##### d" + Arrays.toString(decoded) + " #####");
		graph.reset();
		
	}
	
	
	public static void main(String[] args) throws IOException, FileNotFoundException{
		Scanner input = new Scanner(System.in);
	    System.out.print("Please provide a path to the specification file: ");
	    String file = input.nextLine();
	    // String file = "graphs/cyclic/IS6.txt";
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
		 System.out.println("Simulator generated. Please specify how many simulations you wish to run.");
		 int rounds = input.nextInt();
		 
		 simulator.runSimulations(rounds);
		 
		 input.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
}

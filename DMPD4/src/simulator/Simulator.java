package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import simulator.decoding.embedded.metanode.MetaNode;
import simulator.decoding.simple.Point;
import channel.Channel;
import channel.QuaternarySymmetricChannel;
import code.graph.Graph;

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
	
	
	public static void main(String[] args) throws IOException, FileNotFoundException{
		try(BufferedReader br = new BufferedReader(new FileReader("graphs/cyclic/IS6.txt"))) {
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
			
			Simulator simulator = new Simulator(p, nodeType, tree, nodes, adjacencyMatrix);
			
			
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

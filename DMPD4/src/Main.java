

import graph.Graph;
import graph.MetaNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import channel.QSChannel;
import node.Factor;
import node.Variable;



public class Main {
	public static void main(String[] args) throws IOException, FileNotFoundException {
		try(BufferedReader br = new BufferedReader(new FileReader("IS.txt"))) {

			/**
			 * --- ---
			 * | w 1 |
			 * | 1 w |
			 * --- ---
			 */
			
			
			// CONSTRUCT GRAPH
			Graph g = new Graph();
			int nrNodes = Integer.parseInt(br.readLine());
			for (int i = 0; i < nrNodes; i++) {
				g.addNode(new MetaNode(i, 0.6));
			}
			
			// PARSE ADJACENCY MATRIX
			for (int i = 0; i < nrNodes; i++) {
				String row = br.readLine();
				for (int j = 0; j < nrNodes; j++) {
					if(row.charAt(j) == '1') {
						g.addEdge(i,j);
					}
				}
			}
		
			
			
		} catch(Exception e) {
			System.out.println(e);
		}
		
		
		
		QSChannel channel = new QSChannel(0.6);
		
		int i = 100;
		while (i > 0) {
			System.out.println(QSChannel.randomNumber());
			i--;
		}
		
	}

}

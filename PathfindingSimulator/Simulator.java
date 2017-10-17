/**
Simulate a building pathfinding algorithm.
Includes functions for generating random adjacency matrices, stored as 2D Float ArrayLists,
representing the graph of a building with n rooms.

@author Lucas Wiebe-Dembowski
@since 10/15/2017
*/

import java.util.ArrayList;
import java.util.Arrays;

public class Simulator{
	public ArrayList<ArrayList<Float>> randomAdjMatrix(int numRooms, int numIntersections, int maxDirectionsPerIntersection, boolean verbose){
		/*
		Return a 2D Float ArrayList of size nxn. This represents the adjacency matrix of a graph.
		An entry of -1 in row i and column j means there is no edge connecting node i to j.
		An entry of 0 means i = j.
		An entry > 0 means there is an edge from i to j, and the value of the entry is the distance from i to j.
		The matrix is always symmetric since it represents an undirected graph.

		An intersection node is connected to maxDirectionsPerIntersection other nodes.
		A room node is connected to 2 other nodes.
		*/

		int numNodes = numIntersections + numRooms;
		int i = 0; //loop counter

		ArrayList<ArrayList<Integer>> nodesLists = randomAdjMatrixNodesList(numRooms, numIntersections, maxDirectionsPerIntersection);
		if(verbose){ printMatrix(nodesLists); }

		Float[] minusOneArray = new Float[numNodes]; //Need Float, not float. ArrayList requires boxed primitives.
		Arrays.fill(minusOneArray, -1.0f);
		ArrayList<ArrayList<Float>> matrix = new ArrayList<ArrayList<Float>>();
		for(i = 0; i < numNodes; i++){
			matrix.add(new ArrayList<Float>(Arrays.asList(minusOneArray))); //set all elements to -1
			matrix.get(i).set(i, 0.0f); //but then set diagonal elements to 0
		}

		float x; //temporary variable to store the random edge weight to put in an entry of the matrix
		int col = 0;
		for(i = 0; i < nodesLists.size(); i++){ //i is the row number in the nodes list AND the matrix.
			for(int j = 0; j < nodesLists.get(i).size(); j++){ //j is the column number in the nodes list, not the matrix
				x = (float)(Math.random() * 10);
				col = nodesLists.get(i).get(j); //column of the matrix. the row is i.
				if(matrix.get(i).get(col) <= 0){ //i.e. 0 or -1
					matrix.get(i).set(col, x);
					matrix.get(col).set(i, x);
				}
			}
		}

		if(verbose){ System.out.println("Done creating matrix"); }
		return matrix;
	}

	public ArrayList<ArrayList<Integer>> randomAdjMatrixNodesList(int numRooms, int numIntersections, int maxDirectionsPerIntersection){
		/*
		Randomly generate a list of lists representing an adjacency matrix.
		Distances are not generated here.
		Each list i in the big list contains the labels of all the nodes connected to node i.
		The adjacency matrix represented by this will be symmetric, and no node has an edge from itself to itself.

		An intersection node is connected to maxDirectionsPerIntersection other nodes.
		A room node is connected to 2 other nodes.
		*/

		int numNodes = numIntersections + numRooms;
		int i = 0;
		ArrayList<ArrayList<Integer>> nodesLists = new ArrayList<ArrayList<Integer>>(numNodes);
		while(i++ < numNodes){
			nodesLists.add(new ArrayList<Integer>());
		}
		int n = 0; //number of nodes one node is connected to

		int[] capacities = new int[numNodes]; //Entry i of capacities is the max number of nodes that can be connected to node i
		for(i = 0; i < numNodes; i++){ //assume nodes 0 up to numIntersections-1 are intersections
			capacities[i] = i < numIntersections ? maxDirectionsPerIntersection : 2;
		}
		
		int totalNodesAdded = 0;
		int j;
		for(i = 0; totalNodesAdded < numNodes && i < numNodes; i++){ //stop if all the nodes have been added
			if(i < numIntersections){ //It is an intersection node
				n = maxDirectionsPerIntersection;
			}else if(Math.random() > 0.1){ //It is a room node, in the middle of a hallway
				n = 2;
			}else{ //It is a room node at the end of a dead end hallway
				n = 1;
			}

			//Try to find n random nodes to connect to node i
			for(int aaa = 0; nodesLists.get(i).size() < capacities[i] && totalNodesAdded < numNodes && aaa < n; aaa++){
				//Don't add anything to list i if it's size is at max capacity or if all the nodes have been added
				j = randInt(0, numNodes);
				while(i == j || nodesLists.get(j).size() >= capacities[j] || nodesLists.get(j).contains(i)){
					//Keep trying until you find a node j that is not i, is not at max capacity and is not already connected to i
					//Important: exit condition on the for loop one level up protects this while loop from looping forever.
					j = randInt(0, numNodes);
				}
				nodesLists.get(i).add(j);
				nodesLists.get(j).add(i);
				totalNodesAdded++;
			}
		}
		return nodesLists;
	}

	public <T> void printMatrix(ArrayList<ArrayList<T>> A){
		//Print a 2D ArrayList of any size to the console.
		for(int i = 0; i < A.size(); i++){
			System.out.printf("%2d: ",i);
			System.out.println(A.get(i));
		}
	}

	public int randInt(int min, int max){
		//return a random int in [min, max).
		return min + (int)(Math.random() * (max - min));
	}
}

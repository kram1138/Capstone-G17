/**
Includes functions for generating random adjacency matrices, stored as 2D Float ArrayLists,
representing the graph of a building with n rooms.

@author Lucas Wiebe-Dembowski
@since 10/25/2017
*/
package MatrixGenerator;

import java.util.ArrayList;
import java.util.Arrays;

import GenericCode.Generic;

public class MatrixGenerator{
	public static ArrayList<ArrayList<Float>> randomConnectedAdjMatrix(int numRooms, int numIntersections, int maxDirectionsPerIntersection, boolean verbose){
		ArrayList<ArrayList<Float>> A = randomAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, verbose);
		makeConnected(A, numIntersections, verbose);
		return A;
	}

	public static ArrayList<ArrayList<Float>> randomAdjMatrix(int numRooms, int numIntersections, int maxDirectionsPerIntersection, boolean verbose){
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
		if(verbose){ Generic.printMatrix(nodesLists); }

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
				x = (float)(Math.random() * 10) + 0.1f; //the +0.1 is to avoid small numbers like 0.04 that will be rounded down to 0.0 when I print to the console.
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

	public static ArrayList<ArrayList<Integer>> randomAdjMatrixNodesList(int numRooms, int numIntersections, int maxDirectionsPerIntersection){
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
		double roomNodeProbability = 1.0;

		int[] capacities = new int[numNodes]; //Entry i of capacities is the max number of nodes that can be connected to node i
		for(i = 0; i < numNodes; i++){ //assume nodes 0 up to numIntersections-1 are intersections
			// capacities[i] = i < numIntersections ? maxDirectionsPerIntersection : 2;
			if(i < numIntersections){ //It is an intersection node
				n = maxDirectionsPerIntersection;
			}else if(Math.random() < roomNodeProbability){ //It is a room node, in the middle of a hallway
				n = 2;
			}else{ //It is a room node at the end of a dead end hallway
				n = 1;
			}
			capacities[i] = n;
		}
		
		int totalNodesAdded = 0;
		int j;
		for(i = 0; totalNodesAdded < numNodes && i < numNodes; i++){ //stop if all the nodes have been added
			if(i < numIntersections){ //It is an intersection node
				n = maxDirectionsPerIntersection;
			}else if(Math.random() < roomNodeProbability){ //It is a room node, in the middle of a hallway
				n = 2;
			}else{ //It is a room node at the end of a dead end hallway
				n = 1;
			}

			//Try to find n random nodes to connect to node i
			for(int aaa = 0; nodesLists.get(i).size() < capacities[i] && totalNodesAdded < numNodes && aaa < n; aaa++){
				//Don't add anything to list i if it's size is at max capacity or if all the nodes have been added
				j = Generic.randInt(0, numNodes);
				while(i == j || nodesLists.get(j).size() >= capacities[j] || nodesLists.get(j).contains(i)){
					//Keep trying until you find a node j that is not i, is not at max capacity and is not already connected to i
					//Important: exit condition on the for loop one level up protects this while loop from looping forever.
					j = Generic.randInt(0, numNodes);
				}
				nodesLists.get(i).add(j);
				nodesLists.get(j).add(i);
				totalNodesAdded++;
			}
		}
		return nodesLists;
	}

	public static void makeConnected(ArrayList<ArrayList<Float>> A, int numIntersections, boolean verbose){
		/*
		If the graph is already connected, do nothing.
		Otherwise, find vertices that have no path to vertex 0 and connect them to vertices with paths to vertex 0.

		This function only needs to exist because randomAdjMatrixNodesList() usually produces graphs that are almost but not connected.
		*/
		if(adjMatrixIsConnected(A, verbose)){ return; }
		ArrayList<ArrayList<Float>> P = pathMatrix(A, false);
		float x; //temporary variable to store the random edge weight to put in an entry of the matrix
		boolean found = false; //Indicates whether a nonzero entry was found in row 0.
		boolean done = false;
		int m = P.get(0).size() - 1;
		for(int i = numIntersections; !done && i < P.get(0).size(); i++){ //search for a 0
			//Starting at numIntersections because I don't want to connect an intersection to anything else
			if(P.get(0).get(i) < 0){ throw new IllegalArgumentException("Path matrix contains negative values!"); }
			found = false;
			if(P.get(0).get(i) == 0.0f){ //vertex i has no path to vertex 0
				for(int j = m; !found && j >= 0; j--){
					m--; //don't connect anything else to vertex j. We don't want vertices with more than 3 vertices attached to them.
					//search for something that is not 0. Start at the end because randomAdjMatrixNodesList() puts intersections at the beginning
					if(P.get(0).get(j) > 0){ //vertex j has a path to vertex 0, so I'll connect j to i
						found = true;
						x = (float)(Math.random() * 10) + 0.1f;
						A.get(i).set(j, x);
						A.get(j).set(i, x);
						if(verbose){
							System.out.printf("connecting vertices %d and %d\n", i, j);
						}
						P = pathMatrix(A, false);
						if(pathMatrixIsConnected(P, false)){ done = true; }
					}
				}
			}
		}
		if(verbose){ System.out.println("Done fixing disconnected matrix"); }
	}

	public static ArrayList<ArrayList<Float>> pathMatrix(ArrayList<ArrayList<Float>> A, boolean verbose){
		/*
		*** Time permitting, DELETE THIS and use allPairsShortestPaths() instead of this. ***

		Reads adjacency matrix A and returns path matrix P.
		This uses a slightly modified Warshall's algorithm.
		
		The definition of Warshall's algorithm is for adjacency matrices containing only zeros and ones,
		but this version works with any values. values <= 0 are treated like 0, and values > 0 are treated like 1.
		
		The resulting path matrix returned by this function will still contain only zeros and ones,
		exactly as if A contained only zeros and ones in the first place.
		*/
		if(!isSquare(A, verbose)){
			throw new IllegalArgumentException("Warshall's algorithm undefined for non-square matrix. Dimensions: " + A.size() + "x" + A.get(0).size());
		}
		ArrayList<ArrayList<Float>> P = Generic.matrixDeepCopy(A);
		int n = A.size();
		for(int k = 0; k < n; k++){
			for(int i = 0; i < n; i++){
				for(int j = 0; j < n; j++){
					if(P.get(i).get(k) > 0 && P.get(k).get(j) > 0){
						P.get(i).set(j, 1.0f);
					}else if(P.get(i).get(j) < 0){ //just to get rid of any negative entries, if there are any.
						P.get(i).set(j, 0.0f);
					}
				}
			}
		}
		return P;
	}

	public static boolean adjMatrixIsConnected(ArrayList<ArrayList<Float>> A, boolean verbose){
		/*
		Helper function for pathMatrixIsConnected() that generates the path matrix if the calling function didn't want/need to.
		Running time O(n^3).
		*/
		ArrayList<ArrayList<Float>> P = pathMatrix(A, verbose);
		return pathMatrixIsConnected(P, verbose);
	}

	public static boolean pathMatrixIsConnected(ArrayList<ArrayList<Float>> P, boolean verbose){
		/*
		Reads path matrix P and returns true if the corresponding adjacency is connected. Otherwise returns false.
		It does this by checking if there are any zeroes in the first row of the path matrix.
		Assumes that the path matrix will only have nonnegative values, and no negative or null values.
		Running time O(n).
		*/
		boolean result = true;
		for(int i = 0; i < P.get(0).size(); i++){
			if(P.get(0).get(i) == 0){
				if(verbose){
					System.out.printf("vertex %d has no path to vertex 0\n", i);
				}
				result = false;
			}
		}
		if(verbose){
			System.out.println("Path matrix:");
			Generic.printMatrix(P);
		}
		return result;
	}

	public static ArrayList<ArrayList<Float>> allPairsShortestPaths(ArrayList<ArrayList<Float>> A, boolean verbose){
		/*
		Reads adjacency matrix A and returns distance matrix D.
		This uses the Floyd Warshall algorithm
		*/
		if(!isSquare(A, verbose)){
			throw new IllegalArgumentException("Floyd-Warshall algorithm undefined for non-square matrix. Dimensions: " + A.size() + "x" + A.get(0).size());
		}
		ArrayList<ArrayList<Float>> D = Generic.matrixDeepCopy(A);
		int n = A.size();
		//Initialize nonexistant edges to infinity rather than -1 because it makes code for Floyd-Warshall easier.
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				if(D.get(i).get(j) < 0){ D.get(i).set(j, Float.MAX_VALUE); }
			}
		}
		float d;
		for(int k = 0; k < n; k++){
			for(int i = 0; i < n; i++){
				for(int j = 0; j < n; j++){
					d = D.get(i).get(k) + D.get(k).get(j);
					if(D.get(i).get(j) > d){
						D.get(i).set(j, d);
					}
				}
			}
		}
		//Put nonexistant edges back to -1 because the rest of this project requires that.
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				if(D.get(i).get(j) == Float.MAX_VALUE){ D.get(i).set(j, -1.0f); }
			}
		}
		return D;
	}

	public static boolean isSymmetric(ArrayList<ArrayList<Float>> A, boolean verbose){
		//A matrix A_nxn is symmetric iff it is square and if for all i,j in [0,n) : Aij = Aji
		//This method can't be generic because the comparison done inside the for loop depends on the data type.
		boolean result = true;
		if(isSquare(A, true)){
			int n = A.size();
			int i, j;
			for(i = 0; result && i < n; i++){ //Loop over upper triangle of the matrix
				for(j = i; result && j < n; j++){
					//Need to unbox Float to float so the comparison operator compares the value, NOT the instance!
					float Aij = A.get(i).get(j);
					float Aji = A.get(j).get(i);
					if(verbose && i == j && Aij != 0){ System.out.printf("row %d diagonal element is %f!\n", i, Aij); }
					if(Aij != Aji){
						if(verbose){
							System.out.printf("A(%d,%d) = %.20f but A(%d,%d) = %.20f\n", i, j, Aij, j, i, Aji);
						}
						result = false; //Aij != Aji <=> not symmetric
					}
				}
			}
		}else{
			if(verbose){ System.out.println("B is NOT square."); }
			result = false; //not square <=> not symmetric
		}
		return result;
	}

	public static <T> boolean isSquare(ArrayList<ArrayList<T>> A, boolean verbose){
		//A matrix is square if the number of rows is equal to the number of columns.
		//Can't have a matrix with variable length rows.
		//Variable length columns is impossible anyways due to the structure of an ArrayList<ArrayList<T>>
		boolean result = true;
		int numRows = A.size();
		for(int i = 1; i < numRows; i++){
			if(A.get(i).size() != numRows){
				if(verbose){
					System.out.printf("Row %d has length %d but the number of rows is %d\n", i, A.get(i).size(), numRows);
				}
				return false;
			}
			if(i > 0 && A.get(i).size() != A.get(i-1).size()){
				if(verbose){
					System.out.printf("Row %d has length %d but the row %d has length %d\n", i, A.get(i).size(), i-1, A.get(i-1).size());
				}
				return false;
			}
		}
		return result;
	}
}

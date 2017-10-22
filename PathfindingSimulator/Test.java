/**
Generic program for testing various things related to our project.

@author Lucas Wiebe-Dembowski
@since 10/22/2017
*/

import java.util.ArrayList;
import java.util.Arrays;

public class Test{

	final static boolean VERBOSE = true;
	final static int numIntersections = 4;

	public static void main(String[] args){
		// testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv");
		testSimulatorMatrixGenerator();
		// testMatrixMultiply("matrix2.csv", "matrix3.csv");
		// testPathMatrixFunction();

		System.out.println("Done testing.");
	}

	public static void testSimulatorMatrixGenerator(){
		Simulator S = new Simulator();
		int numRooms = 30;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> A = S.randomAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, VERBOSE);
		
		System.out.println("Randomly generated adjacency matrix:");
		System.out.println("A is " + (isSymmetric(A, VERBOSE) ? "" : "NOT") + " symmetric.");
		printMatrix(A);
		System.out.println("A is " + (adjMatrixIsConnected(A, VERBOSE) ? "" : "NOT") + " connected.");
		
		makeConnected(A, VERBOSE);
		printMatrix(A);
		System.out.println("A is " + (adjMatrixIsConnected(A, VERBOSE) ? "" : "NOT") + " connected.");
	}

	/*
	If the graph is already connected, do nothing.
	Otherwise, find vertices that have no path to vertex 0 and connect them to vertices with paths to vertex 0.

	This function only needs to exist because randomAdjMatrixNodesList() usually produces graphs that are almost but not connected.
	*/
	public static void makeConnected(ArrayList<ArrayList<Float>> A, boolean verbose){
		if(adjMatrixIsConnected(A, false)){ return; }
		ArrayList<ArrayList<Float>> P = pathMatrix(A);
		float x; //temporary variable to store the random edge weight to put in an entry of the matrix
		boolean found = false; //Indicates whether a nonzero entry was found in row 0.
		int m = P.get(0).size() - 1;
		for(int i = numIntersections; i < P.get(0).size(); i++){ //search for a 0
			//Starting at numIntersections because I don't want to connect an intersection to anything else
			if(P.get(0).get(i) < 0){ throw new IllegalArgumentException("Path matrix contains negative values!"); }
			found = false;
			if(P.get(0).get(i) == 0.0f){ //vertex i has no path to vertex 0
				for(int j = m; !found && j >= 0; j--){
					m--; //don't connect anything else to vertex j. We don't want vertices with more than 3 vertices attached to them.
					//search for something that is not 0. Start at the end because randomAdjMatrixNodesList() puts intersections at the beginning
					if(P.get(0).get(j) > 0){ //vertex j has a path to vertex 0, so I'll connect j to i
						found = true;
						x = (float)(Math.random() * 10);
						A.get(i).set(j, x);
						A.get(j).set(i, x);
						if(verbose){
							System.out.printf("connecting vertices %d and %d\n", i, j);
						}
						P = pathMatrix(A);
						if(pathMatrixIsConnected(P, false)){ return; }
					}
				}
			}
		}
	}

	public static void testCSVParser(){ testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv"); }
	public static void testCSVParser(String inputFile, String outputFile){
		CSVParser P = new CSVParser();
		ArrayList<ArrayList<Float>> B = P.matrixListFromCSV(inputFile);
		printMatrix(B);
		P.matrixToCSV(B, outputFile);
		System.out.println("B is " + (isSymmetric(B, VERBOSE) ? "" : "NOT") + " symmetric.");
	}

	public static void testMatrixMultiply(String inputFile1, String inputFile2){
		/*
		Test cases validated: 3x3 * 3x3, 4x4 * 4x4, 4x4 * 4x5, 4x5 * 4x4, 4x4 * 4x1
		*/
		CSVParser P = new CSVParser();
		ArrayList<ArrayList<Float>> m1 = P.matrixListFromCSV(inputFile1);
		ArrayList<ArrayList<Float>> m2 = P.matrixListFromCSV(inputFile2);
		printMatrix(m1);
		printMatrix(m2);
		printMatrix(matrixMultiply(m1, m2));
	}

	public static void testPathMatrixFunction(){
		//First test a randomly generated one.
		Simulator S = new Simulator();
		int numRooms = 8;
		int numIntersections = 2;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> B = S.randomAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, VERBOSE);
		printMatrix(B);
		CSVParser csv = new CSVParser();
		csv.matrixToCSV(B, "out/testSimulatorMatrixGenerator.csv");
		ArrayList<ArrayList<Float>> P = pathMatrix(B);
		printMatrix(P);

		//Then test a hand-generated one.
		ArrayList<ArrayList<Float>> m4 = csv.matrixListFromCSV("matrix4.csv");
		printMatrix(m4);
		ArrayList<ArrayList<Float>> P2 = pathMatrix(m4);
		printMatrix(P2);
	}

	public static boolean adjMatrixIsConnected(ArrayList<ArrayList<Float>> A, boolean verbose){
		/*
		Helper function for pathMatrixIsConnected() that generates the path matrix if the calling function didn't want/need to.
		Running time O(n^3).
		*/
		ArrayList<ArrayList<Float>> P = pathMatrix(A);
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
			printMatrix(P);
		}
		return result;
	}

	public static ArrayList<ArrayList<Float>> pathMatrix(ArrayList<ArrayList<Float>> A){
		/*
		Reads adjacency matrix A and returns path matrix P.
		This uses a slightly modified Warshall's algorithm.
		
		The definition of Warshall's algorithm is for adjacency matrices containing only zeros and ones,
		but this version works with any values. values <= 0 are treated like 0, and values > 0 are treated like 1.
		
		The resulting path matrix returned by this function will still contain only zeros and ones,
		exactly as if A contained only zeros and ones in the first place.
		*/
		if(!isSquare(A, VERBOSE)){
			throw new IllegalArgumentException("Warshall's algorithm undefined for non-square matrix. Dimensions: " + A.size() + "x" + A.get(0).size());
		}
		ArrayList<ArrayList<Float>> P = matrixDeepCopy(A);
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

	public static <T> ArrayList<ArrayList<T>> matrixDeepCopy(ArrayList<ArrayList<T>> A){
		/*
		Return a new 2D matrix containing the same elements as A.
		Only tested for primitive data types. 
		If T is an object, not a primitive type, this might not deep copy the objects.
		*/
		ArrayList<ArrayList<T>> B = new ArrayList<ArrayList<T>>();
		for(int i = 0; i < A.size(); i++){
			B.add(new ArrayList<T>());
			B.get(i).addAll(A.get(i));
		}
		return B;
	}

	public static ArrayList<ArrayList<Float>> matrixMultiply(ArrayList<ArrayList<Float>> m1, ArrayList<ArrayList<Float>> m2) {
		/**
		Matrix-multiply two arrays together.
		The arrays MUST be rectangular.
		@author Tom Christiansen & Nathan Torkington, Perl Cookbook version.

		http://www.java2s.com/Code/Java/Collections-Data-Structure/Multiplytwomatrices.htm
		
		Modified by Lucas Wiebe-Dembowski on October 20, 2017, to work with ArrayList<ArrayList<Float>> instead of int[][]
		Note this returns the product m1*m2, NOT m2*m1

		Yes, I know multiplying symmetric matrices can probably be done faster than this generic algorithm can do, 
		but I don't care. Performance gain not worth effort.
		*/
		int m1rows = m1.size();
		int m1cols = m1.get(0).size(); //Author didn't mention that this is where the code fails if the matrices are not rectangular
		int m2rows = m2.size();
		int m2cols = m2.get(0).size(); //Author didn't mention that this is where the code fails if the matrices are not rectangular
		if (m1cols != m2rows){
		 	throw new IllegalArgumentException("matrices don't match: " + m1cols + " != " + m2rows);
		}

		//Initialize a 0-matrix with dimensions m1rows X m2cols
		ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
		Float[] blankArray = new Float[m2cols]; //Need Float, not float. ArrayList requires boxed primitives.
		Arrays.fill(blankArray, 0.0f);
		for(int i = 0; i < m1rows; i++){
			result.add(new ArrayList<Float>(Arrays.asList(blankArray))); //add row to the matrix
		}

		//multiply m1*m2
		float x; //used for unboxing Float to float
		for (int i=0; i<m1rows; i++){
		 	for (int j=0; j<m2cols; j++){
		   		for (int k=0; k<m1cols; k++){
		   			x = result.get(i).get(j) + m1.get(i).get(k) * m2.get(k).get(j);
		   			result.get(i).set(j, x);
		   		}
		 	}
		}

		return result;
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

	public static void printMatrix(ArrayList<ArrayList<Float>> A){
		//Print a 2D Float ArrayList of any size to the console.
		//Formatting is designed to look good for square float matrices with only 1 decimal place of precision.
		//Row numbers and column numbers are displayed
		if(A.size() == 0){
			System.out.println("Matrix is empty");
			return;
		}
		System.out.print("   ");
		for(int i = 0; i < A.get(0).size(); i++){ //column numbers
			System.out.printf("%6d", i);
		}
		System.out.println();
		System.out.print("   ");
		for(int i = 0; i < A.get(0).size(); i++){ //horizontal line
			System.out.print("______");
		}
		System.out.println();
		for(int i = 0; i < A.size(); i++){
			System.out.printf("%2d|",i); //row numbers
			for(int j = 0; j < A.get(i).size(); j++){
				System.out.printf("%6.1f", A.get(i).get(j));
			}
			System.out.println();
		}
		System.out.println();
	}
}

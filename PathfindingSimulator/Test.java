/**
Generic program for testing various things related to our project.

@author Lucas Wiebe-Dembowski
@since 10/15/2017
*/

import java.util.ArrayList;
import java.util.Arrays;

public class Test{
	public static void main(String[] args){
		// testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv");
		// testSimulatorMatrixGenerator();
		testMatrixMultiply("matrix2.csv", "matrix3.csv");

		System.out.println("Done testing.");
	}

	public static void testSimulatorMatrixGenerator(){
		Simulator S = new Simulator();
		int numRooms = 8;
		int numIntersections = 2;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> B = S.randomAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, true);
		printMatrix(B);
		System.out.println("B is " + (isSymmetric(B, true) ? "" : "NOT") + " symmetric.");
	}

	public static void testCSVParser(){ testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv"); }
	public static void testCSVParser(String inputFile, String outputFile){
		CSVParser P = new CSVParser();
		ArrayList<ArrayList<Float>> B = P.matrixListFromCSV(inputFile);
		printMatrix(B);
		P.matrixToCSV(B, outputFile);
		System.out.println("B is " + (isSymmetric(B, true) ? "" : "NOT") + " symmetric.");
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

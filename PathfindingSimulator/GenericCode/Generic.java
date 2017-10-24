/**
Generic code not related to anythign specific in the project, and gets used in many places.

@author Lucas Wiebe-Dembowski
@since 10/24/2017
*/
package GenericCode;

import java.util.ArrayList;
import java.util.Arrays;

public class Generic{

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

	public static void printAdjMatrix(ArrayList<ArrayList<Float>> A){
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

	public static <T> void printMatrix(ArrayList<ArrayList<T>> A){
		//Print a 2D ArrayList of any size to the console.
		for(int i = 0; i < A.size(); i++){
			System.out.printf("%2d: ",i);
			System.out.println(A.get(i));
		}
	}

	public static int randInt(int min, int max){
		//return a random int in [min, max).
		return min + (int)(Math.random() * (max - min));
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
}
/**
Generic code not related to anything specific in the project, and gets used in many places.

@author Lucas Wiebe-Dembowski
@since 01/21/2018
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

	public static boolean intListContains(ArrayList<Integer> A, int x){
		boolean found = false;
		for(int i : A){
			if(i==x){ found = true; }
		}
		return found;
	}

	public static boolean matricesDeepEquals(ArrayList<ArrayList<Float>> A, ArrayList<ArrayList<Float>> B){
		/*
		Return true if the value of each element Aij is equal to Bij. A and B MUST BE THE SAME SIZE.
		*/
		boolean success = true;
		if(A.size() != B.size()){
			success = false;
		}else{
			for(int i = 0; success && i < A.size(); i++){
				if(A.get(i).size() != B.get(i).size()){
					success = false;
				}else{
					for(int j = 0; success && j < A.size(); j++){
						float a = A.get(i).get(j);
						float b = B.get(i).get(j);
						if(a != b){
							success = false;
						}
					}
				}
			}
		}
		return success;
	}

	public static int[] arrayListToArray(ArrayList<Integer> L){
		int[] A = new int[L.size()];
		for(int i = 0; i < A.length; i++){
			A[i] = (int)L.get(i);
		}
		return A;
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

}
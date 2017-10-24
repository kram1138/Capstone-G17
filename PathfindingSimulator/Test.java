/**
Generic program for testing various things related to our project.

@author Lucas Wiebe-Dembowski
@since 10/24/2017
*/

import java.util.ArrayList;
import java.util.Arrays;

import CSVParsing.CSVParsing;
import MatrixGenerator.MatrixGenerator;
import GenericCode.Generic;

public class Test{

	final static boolean VERBOSE = false;
	final static int numIntersections = 4;

	public static void main(String[] args){
		// testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv");
		testMatrixGenerator();
		// testMatrixMultiply("matrix2.csv", "matrix3.csv");
		// testPathMatrixFunction();

		System.out.println("Done testing.");
	}

	public static void testMatrixGenerator(){
		int numRooms = 30;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> A = MatrixGenerator.randomConnectedAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, VERBOSE);
		System.out.println("Randomly generated adjacency matrix:");
		Generic.printAdjMatrix(A);
		System.out.println("A is " + (MatrixGenerator.isSymmetric(A, VERBOSE) ? "" : "NOT") + " symmetric.");
		System.out.println("A is " + (MatrixGenerator.adjMatrixIsConnected(A, VERBOSE) ? "" : "NOT") + " connected.");
	}

	public static void testMakeConnected(){
		int numRooms = 30;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> A = MatrixGenerator.randomAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, VERBOSE);
		
		System.out.println("Randomly generated adjacency matrix:");
		System.out.println("A is " + (MatrixGenerator.isSymmetric(A, VERBOSE) ? "" : "NOT") + " symmetric.");
		Generic.printAdjMatrix(A);
		System.out.println("A is " + (MatrixGenerator.adjMatrixIsConnected(A, VERBOSE) ? "" : "NOT") + " connected.");
		
		MatrixGenerator.makeConnected(A, numIntersections, VERBOSE);
		Generic.printAdjMatrix(A);
		System.out.println("A is " + (MatrixGenerator.adjMatrixIsConnected(A, VERBOSE) ? "" : "NOT") + " connected.");
	}

	public static void testCSVParser(){ testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv"); }
	public static void testCSVParser(String inputFile, String outputFile){
		ArrayList<ArrayList<Float>> B = CSVParsing.matrixListFromCSV(inputFile);
		Generic.printAdjMatrix(B);
		CSVParsing.matrixToCSV(B, outputFile);
		System.out.println("B is " + (MatrixGenerator.isSymmetric(B, VERBOSE) ? "" : "NOT") + " symmetric.");
	}

	public static void testMatrixMultiply(String inputFile1, String inputFile2){
		/*
		Test cases validated: 3x3 * 3x3, 4x4 * 4x4, 4x4 * 4x5, 4x5 * 4x4, 4x4 * 4x1
		*/
		ArrayList<ArrayList<Float>> m1 = CSVParsing.matrixListFromCSV(inputFile1);
		ArrayList<ArrayList<Float>> m2 = CSVParsing.matrixListFromCSV(inputFile2);
		Generic.printAdjMatrix(m1);
		Generic.printAdjMatrix(m2);
		Generic.printAdjMatrix(Generic.matrixMultiply(m1, m2));
	}

	public static void testPathMatrixFunction(){
		//First test a randomly generated one.
		int numRooms = 8;
		int numIntersections = 2;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> B = MatrixGenerator.randomAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, VERBOSE);
		Generic.printAdjMatrix(B);
		CSVParsing.matrixToCSV(B, "out/testPathMatrixFunction.csv");
		ArrayList<ArrayList<Float>> P = MatrixGenerator.pathMatrix(B, VERBOSE);
		Generic.printAdjMatrix(P);

		//Then test a hand-generated one.
		ArrayList<ArrayList<Float>> m4 = CSVParsing.matrixListFromCSV("matrix4.csv");
		Generic.printAdjMatrix(m4);
		ArrayList<ArrayList<Float>> P2 = MatrixGenerator.pathMatrix(m4, VERBOSE);
		Generic.printAdjMatrix(P2);
	}
}

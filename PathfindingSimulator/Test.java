/**
Generic program for testing various things related to our project.

@author Lucas Wiebe-Dembowski
@since 10/26/2017
*/

import java.util.ArrayList;
import java.util.Arrays;

import CSVParsing.CSVParsing;
import MatrixGenerator.MatrixGenerator;
import GenericCode.Generic;
import Simulator.Simulator;

public class Test{

	final static boolean VERBOSE = false;
	final static int numIntersections = 4;

	public static void main(String[] args){
		// testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv");
		// testMatrixGenerator();
		// testPathMatrixFunction();
		// testMatrixMultiply("matrix2.csv", "matrix3.csv");
		// testAllPairsShortestPathFunction();
		testSimulator();

		System.out.println("Done testing.");
	}

	public static void testSimulator(){
		int numRooms = 30;
		int maxDirectionsPerIntersection = 3;

		// ArrayList<ArrayList<Float>> A = MatrixGenerator.randomConnectedAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, false);
		// ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV("matrix4.csv");
		ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV("matrix6.csv");

		ArrayList<ArrayList<Float>> D = MatrixGenerator.allPairsShortestPaths(A, VERBOSE);
		if(VERBOSE){
			System.out.println("Adjacency Matrix:");
			Generic.printAdjMatrix(A);
			System.out.println("Distance Matrix:");
			Generic.printAdjMatrix(D);
		}

		int nEL = 2000;
		int nCL = 15000;
		double initialTemperature = 2.0;
		double finalTemperature = 0.001;
		System.out.printf("Testing Simulated Annealing on %d nodes with nEL = %d, nCL = %d, Ti = %f and Tf = %f:\n", A.size(), nEL, nCL, initialTemperature, finalTemperature);
		
		int start = 0;
		int[] soln;
		float cost;
		ArrayList<ArrayList<Float>> costs_and_times = new ArrayList<ArrayList<Float>>();
		long startTime, stop, runtime = 0;
		int i = 0;
		// for(nCL = 1000; nCL <= 20000; nCL += 1000){
		// for(nEL = 1000; nEL <= 20000; nEL += 1000){
		for(int j = 0; j < 100; j++){
			startTime = System.nanoTime();
			soln = Simulator.optimizeSA(D, start, nEL, nCL, initialTemperature, finalTemperature, VERBOSE);
			stop = System.nanoTime();
			runtime = stop - startTime; //time in nanoseconds
			cost = Simulator.cost(D, start, soln);
			costs_and_times.add(new ArrayList<Float>());
			costs_and_times.get(i).add((float)runtime / 1000000000.0f);
			costs_and_times.get(i).add(cost);
			i++;
			System.out.printf("nEL = %d. nCL = %d. SOLUTION = %f. Runtime = %fs.\n\n", nEL, nCL, cost, (double)runtime / 1000000000.0);
		}
		String outputFile = "out/costsList.csv";
		System.out.printf("Sending results to %s... ", outputFile);
		CSVParsing.matrixToCSV(costs_and_times, outputFile);
		System.out.println(" done.");
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

	public static void testAllPairsShortestPathFunction(){
		//Test a hand-generated one.
		//Compare to results at https://www-m9.ma.tum.de/graph-algorithms/spp-floyd-warshall/index_en.html
		//Validated on a 5x5 connected graph and an 8x8 graph with two connected components,
		//and the 6x6 connected graph in my notebook.
		ArrayList<ArrayList<Float>> m4 = CSVParsing.matrixListFromCSV("matrix4.csv");
		Generic.printAdjMatrix(m4);
		System.out.println("A is " + (MatrixGenerator.isSymmetric(m4, VERBOSE) ? "" : "NOT") + " symmetric.");
		System.out.println("A is " + (MatrixGenerator.adjMatrixIsConnected(m4, VERBOSE) ? "" : "NOT") + " connected.");
		ArrayList<ArrayList<Float>> P2 = MatrixGenerator.allPairsShortestPaths(m4, VERBOSE);
		Generic.printAdjMatrix(P2);
	}
}

/**
Generic program for testing various things related to our project.

@author Lucas Wiebe-Dembowski
@since 01/21/2018
*/

import java.util.ArrayList;
import java.util.Arrays;

import CSVParsing.CSVParsing;
import MatrixGenerator.MatrixGenerator;
import GenericCode.Generic;
import Simulator.Simulator;

public class Test{

	final static boolean VERBOSE = false;
	final static int numIntersections = 5;

	public static void main(String[] args){
		// testCSVParser("adjMatrix1.csv", "out/adjMatrix1_out.csv");
		// testMatrixGenerator();
		// testPathMatrixFunction();
		// testAllPairsShortestPathFunction();
		// testDijkstra();

		// testCompletePath();
		testEncodedPath();

		// testMapMatrixFromCSV();

		// testSimulator();

		System.out.println("Done testing.");
	}

	public static void testEncodedPath(){
		String file = "smallGraph3Map.csv";
		ArrayList<ArrayList<Float>> adj = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Integer>> dir = new ArrayList<ArrayList<Integer>>();
		ArrayList<Character> rooms = new ArrayList<Character>();
		ArrayList<Character> roomDirs = new ArrayList<Character>();
		Simulator.mapMatrixFromCSV(file, adj, dir, rooms, roomDirs, false); //fills adj, dir, rooms, roomDirs with stuff

		int[] path = {0, 13, 5, 3, 15, 2, 11, 8, 4, 12, 10, 14, 9, 6, 1, 16, 7}; //matrix4Updated
		System.out.printf("Compact path starting at %d is %s\n", path[0], Arrays.toString(path));
		System.out.println(rooms);
		int[] completePath = Simulator.completePath(adj, path, rooms, roomDirs); //modifies rooms, roomDirs
		System.out.println(rooms);
		System.out.printf("Complete path starting at %d is %s\n", completePath[0], Arrays.toString(completePath));

		String encodedPath = Simulator.encodedPath(dir, completePath, rooms, roomDirs, true);
		System.out.println("\n----------------------------------------------\n");
		System.out.println(encodedPath);
		System.out.println("\n----------------------------------------------\n");
	}

	public static void testSimulator(){
		int numRooms = 50;
		int maxDirectionsPerIntersection = 3;

		// ArrayList<ArrayList<Float>> A = MatrixGenerator.randomConnectedAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, false);
		// CSVParsing.matrixToCSV(A, "matrix50_5_3.csv");
		String file = "matrix4MSP";
		// ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV("matrix4.csv");
		// ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV("matrix30.csv");
		ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV(file + ".csv");

		ArrayList<ArrayList<Float>> D = MatrixGenerator.allPairsShortestPaths(A, VERBOSE);

		System.out.println("\n----------------------------------------------\n");
		int[] what1 = {0, 3, 2, 8, 5, 7, 1, 4, 6, 10, 9};
		int[] what2 = {8, 2, 3, 0, 5, 7, 1, 4, 6, 10, 9};
		int[] what3 = {9, 10, 6, 1, 4, 7, 0, 5, 3, 2, 8};
		System.out.printf("cost is %f", Simulator.cost(D, 9, what3));
		System.out.println("\n----------------------------------------------\n");

		if(VERBOSE){
			System.out.println("Adjacency Matrix:");
			Generic.printAdjMatrix(A);
			System.out.println("Distance Matrix:");
			Generic.printAdjMatrix(D);
			Generic.printAdjMatrix(MatrixGenerator.binaryMatrix(A));
		}

		int nEL = 2000;
		int nCL = 2000;
		double initialTemperature = 2.0;
		double finalTemperature = 0.001;
		int coolingSchedule = Simulator.EXPONENTIAL;

		System.out.printf("Testing Simulated Annealing on %d nodes with nEL = %d, nCL = %d, Ti = %f and Tf = %f using %s cooling schedule:\n", 
			A.size(), nEL, nCL, initialTemperature, finalTemperature, Simulator.schedules[coolingSchedule]);
		
		int start = 0;
		int[] soln;
		float cost;
		ArrayList<ArrayList<Float>> costs_and_times = new ArrayList<ArrayList<Float>>();
		long startTime, stop, runtime = 0;
		int i = 0;
		// for(int j = 0; j < 100; j++){
			startTime = System.nanoTime();
			int numIterations = 6;
			soln = Simulator.optimizeSA(D, start, nEL, nCL, initialTemperature, finalTemperature, coolingSchedule, numIterations, VERBOSE);
			System.out.println("solution is " + Arrays.toString(soln));
			stop = System.nanoTime();
			runtime = stop - startTime; //time in nanoseconds
			cost = Simulator.cost(D, start, soln);
			costs_and_times.add(new ArrayList<Float>());
			costs_and_times.get(i).add((float)runtime / 1000000000.0f);
			costs_and_times.get(i).add(cost);
			i++;
			System.out.printf("nEL = %d. nCL = %d. SOLUTION = %.1f. Runtime = %fs.\n\n", nEL, nCL, cost, (double)runtime / 1000000000.0);
		// }
		String outputFile = "out/costsList.csv";
		System.out.printf("Sending results to %s... ", outputFile);
		CSVParsing.matrixToCSV(costs_and_times, outputFile);

		CSVParsing.listToFile(soln, file + "_path.csv");

		System.out.println(" done.");
	}

	public static void testMapMatrixFromCSV(){
		//Successfully tested on smallGraph3Map.csv.
		String file = "smallGraph3Map.csv";
		ArrayList<ArrayList<Float>> adj = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Integer>> dir = new ArrayList<ArrayList<Integer>>();
		ArrayList<Character> rooms = new ArrayList<Character>();
		ArrayList<Character> roomDirs = new ArrayList<Character>();
		Simulator.mapMatrixFromCSV(file, adj, dir, rooms, roomDirs, true);
		System.out.println("adj is " + (MatrixGenerator.isSymmetric(adj, VERBOSE) ? "" : "NOT") + " symmetric.");
		Generic.printAdjMatrix(adj);
		System.out.println("dir is " + (MatrixGenerator.isNegativeSymmetric(dir, VERBOSE) ? "" : "NOT") + " negative symmetric.");
		Generic.printMatrix(dir);
	}

	public static void testCompletePath(){
		//Successfully tested on matrix4.csv (no corners or directions) 
		//and matrix4Updated.csv (with corners, no directions)

		String file = "smallGraph3Map.csv";
		ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV("matrix4Updated.csv");
		ArrayList<ArrayList<Float>> adj = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Integer>> dir = new ArrayList<ArrayList<Integer>>();
		ArrayList<Character> rooms = new ArrayList<Character>();
		ArrayList<Character> roomDirs = new ArrayList<Character>();
		Simulator.mapMatrixFromCSV(file, adj, dir, rooms, roomDirs, false);
		Generic.printAdjMatrix(A);
		Generic.printAdjMatrix(adj);
		System.out.println("A is " + (Generic.matricesDeepEquals(A, adj) ? "" : "NOT") + " equal to adj.");

		// int[] path = {0, 5, 3, 2, 8, 4, 10, 9, 6, 1, 7}; //matrix4
		int[] path = {0, 13, 5, 3, 15, 2, 11, 8, 4, 12, 10, 14, 9, 6, 1, 16, 7}; //matrix4Updated
		System.out.printf("Compact path starting at %d is %s\n", path[0], Arrays.toString(path));
		int[] completePath = Simulator.completePath(adj, path, rooms, roomDirs); //modifies rooms, roomDirs
		System.out.printf("Complete path starting at %d is %s\n", completePath[0], Arrays.toString(completePath));
	}

	public static void testDijkstra(){
		//paths tested successfully on matrix4.csv: 8-1, 8-7, 5-9, 2-9
		String file = "matrix4";
		ArrayList<ArrayList<Float>> A = CSVParsing.matrixListFromCSV(file + ".csv");
		int S = 2;
		int F = 9;
		int[] path = Simulator.shortestPath(A, S, F, true);
		System.out.printf("Shortest path from %d to %d is %s\n", S, F, Arrays.toString(path));
	}

	public static void testMatrixGenerator(){
		int numRooms = 8;
		int maxDirectionsPerIntersection = 3;
		ArrayList<ArrayList<Float>> A = MatrixGenerator.randomConnectedAdjMatrix(numRooms, numIntersections, maxDirectionsPerIntersection, VERBOSE);
		System.out.println("Randomly generated adjacency matrix:");
		Generic.printAdjMatrix(A);
		Generic.printAdjMatrix(MatrixGenerator.binaryMatrix(A));
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

	public static void testCSVParser(String inputFile, String outputFile){
		ArrayList<ArrayList<Float>> B = CSVParsing.matrixListFromCSV(inputFile);
		Generic.printAdjMatrix(B);
		CSVParsing.matrixToCSV(B, outputFile);
		System.out.println("B is " + (MatrixGenerator.isSymmetric(B, VERBOSE) ? "" : "NOT") + " symmetric.");
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
		//Test a hand-generated graph.
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

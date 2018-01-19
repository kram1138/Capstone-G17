/**
Pathfinding Simulator.

@author Lucas Wiebe-Dembowski
@since 01/16/2018
*/
package Simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

import GenericCode.Generic;

public class Simulator{

	final public static int LINEAR = 0;
	final public static int EXPONENTIAL = 1;
	final public static int ADAPTIVE = 2;
	final public static String[] schedules = {"linear", "exponential", "adaptive"};

	//I could also have called UP, RIGHT, DOWN, LEFT as NORTH, EAST, SOUTH, WEST respectively instead.
	final private static int LEFT = -1;
	final private static int RIGHT = 1;
	final private static int UP = 2;
	final private static int DOWN = -2;
	final private static int NO_DIRECTION = 0;

	public static int[] optimizeSA(ArrayList<ArrayList<Float>> distMatrix, 
									int start, 
									int nEL, 
									int nCL, 
									double initialTemperature, 
									double finalTemperature, 
									int coolingSchedule, 
									int numIterations,
									boolean verbose)
	{
		//Helper function that calls Simulated Annealing numIterations times and resturns the best of all the obtained solutions.
		int n = distMatrix.size();
		int[] soln = new int[n];
		int[] bestSoln = new int[n];
		float cost = Float.MAX_VALUE, best = Float.MAX_VALUE;
		for(int i = 0; i < numIterations; i++){
			soln = optimizeSA(distMatrix, start, nEL, nCL, initialTemperature, finalTemperature, coolingSchedule, verbose);
			cost = cost(distMatrix, start, soln);
			if(cost < best){
				best = cost;
				System.arraycopy(soln, 0, bestSoln, 0, n);
			}
		}
		return bestSoln;
	}

	public static int[] optimizeSA(ArrayList<ArrayList<Float>> distMatrix, 
									int start, 
									int nEL, 
									int nCL, 
									double initialTemperature, 
									double finalTemperature, 
									int coolingSchedule, 
									boolean verbose)
	{
		//Based mostly on ECE 3790 lab 3

		int r1 = -1, r2 = -1; //random array indices, initialized to different invalid index values
		int t; //temporary variable
		int n = distMatrix.size(); //number of nodes in the graph
		int[] soln = new int[n]; //currently the current solution
		float currentCost; //cost of the current solution
		float bestCost; //cost of the best solution seen so far
		int[] temp = new int[n]; //the solution currently being checked
		float tempCost = -1.0f; //cost of the solution currently being checked

		int i, j; //loop counters

		//Initially pick random solution
		soln[0] = start; //soln[0] stays the same throughout this function.
		i = 1;
		while(i < n){
			r1 = Generic.randInt(1, n); //random integer in [1, n)
			if(soln[r1] == 0){
				//put this integer in a random spot if another int has not already been put there
				soln[r1] = i;
				i++; //do not put this outside the if statement.
			}
		}

		currentCost = cost(distMatrix, start, soln);
		bestCost = currentCost;

		float dC; //Change in cost
		double p; //Probability of choosing a worse solution
		double dCavg = 0; //average change in cost
		double currentTemperature = initialTemperature;
		double a = 1.0; //default is 1.
		double u = 1.0; //default is 1.
		double B = 1.05;
		double x = 0, y = 0;
		if(coolingSchedule == LINEAR){
			a = (initialTemperature - finalTemperature) / (double)nCL;
		}else if(coolingSchedule == EXPONENTIAL){
			a = Math.pow(finalTemperature/initialTemperature, 1/(double)nCL);
		}else if(coolingSchedule == ADAPTIVE){
			a = Math.pow(finalTemperature/initialTemperature, 1/(double)nCL);
		}

		if(verbose){
			System.out.printf("Initial solution = %s\n", Arrays.toString(soln));
			System.out.printf("Initial solution cost = %f\n", currentCost);
			System.out.printf("Cooling schedule is %s\n", schedules[coolingSchedule] );
		}
		for(int zzzz = 0; zzzz < nCL && tempCost != 0 && currentCost != 0; zzzz++){ //cooling loop.
			dCavg = 0;
			for(i = 0; i < nEL && currentCost != 0; i++){ //equilibrium loop
				//Loop until the specified max number of iterations has been reached, or a solution with cost 
				//of 0 has been found, or a lot of consecutinve random swaps get performed with no cost improvement.

				r1 = Generic.randInt(1, n); //random int in [1, n). soln[0] stays the same throughout this function.
				r2 = r1; //initialize while loop
				while(r2 == r1){
					r2 = Generic.randInt(1, n);
				}
				// public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
				System.arraycopy(soln, 1, temp, 1, n-1); //soln[0] stays the same throughout this function.

				//swap the element at index r1 with the element at index r2
				t = temp[r1];
				temp[r1] = temp[r2];
				temp[r2] = t;

				tempCost = cost(distMatrix, start, temp);

				//keep current solution if its cost is lower or equal to the previous one's cost.
				if(tempCost <= currentCost){
					System.arraycopy(temp, 1, soln, 1, n-1);
					currentCost = tempCost;
					bestCost = currentCost;
				}else{
					dC = tempCost - currentCost;

					if(i == 0){
						dCavg = (double)dC; //The average of one data point is itself
					}else{
						//Update the average of a set of data points after adding a data point
						dCavg = (i * dCavg + dC) / (i + 1); //(n*xbar + x_n+1) / (n + 1)
					}

					p = Math.exp((double)(-dC) / (dCavg * currentTemperature)); //Boltzmann probability
					if(p > Math.random()){
						//Keep the worse solution
						System.arraycopy(temp, 1, soln, 1, n-1);
						currentCost = tempCost;
					}
				}
			} //end quilibrium loop

			if(coolingSchedule == LINEAR){
				currentTemperature -= a;
			}else if(coolingSchedule == EXPONENTIAL){
				currentTemperature *= a;
			}else if(coolingSchedule == ADAPTIVE){
				x = currentCost - bestCost;
				y = -Math.log(1-a) / Math.log(B);
				u = 1 - Math.pow(B, -x - y);
				currentTemperature *= u;
			}
			// System.out.println(currentTemperature);
		} //end cooling loop
		return soln;
	}

	public static float cost(ArrayList<ArrayList<Float>> distMatrix, int start, int[] soln){
		/*
		Calculate the cost of some configuration of the graph by adding the edge weights
		between the left and right side.
		int[] soln is an array containing the sequence of node numbers representing a solution.
		ArrayList<ArrayList<Float>> distMatrix is the all pairs shortest distance matrix of the graph.
		*/

		float result = 0;
		int n = soln.length;
		for(int i = 0; i < n-1; i++){
			result += distMatrix.get(soln[i]).get(soln[i+1]);
		}
		result += distMatrix.get(soln[n-1]).get(start); //go back to the starting point at the end.
		return result;
	}

	public static String encodedPath(ArrayList<ArrayList<Integer>> dir, int[] path, ArrayList<Character> rooms, boolean verbose){
		/*

l = left intersection
r = right intersection
a = left room
b = right room
s = straight
smallGraph3Map path 
0, 13, 5, 13, 0, 3, 15, 2, 11, 8, 4, 12, 1, 6, 10, 14, 9, 14, 10, 6, 1, 16, 7
s  l   b  r   r  a  r   s  r   a  a  r   l  a  a   r   a  l   s   s  l  r   a






		Take a complete path through a map that includes duplicate nodes, such as
		one returned by completePath(), and encode it in a way that the robot can understand.
		e.g. a, b, c might become 3 li_2 ri_3 ri_4.
		dir is a direction matrix that stores values that are either LEFT, RIGHT, UP, DOWN or NO_DIRECTION.

		Every node in path must be guaranteed by the calling function to be adjacent to each other in the graph. 
		i.e. for all i, path[i-1] always has an edge to path[i].

		Example for smallGraph3Map.csv: path = {0, 13, 5, 13, 0, 3, 15, 2, 11, 8, 4, 12, 1, 6, 10, 14, 9, 14, 10, 6, 1, 16, 7};


	final private static int LEFT = -1;
	final private static int RIGHT = 1;
	final private static int UP = 2;
	final private static int DOWN = -2;
	final private static int NO_DIRECTION = 0;
	final private static String[] directions = {"d", "l", "", "r", "u"};
		*/
		if(verbose){
			// Generic.printMatrix(dir);
			// System.out.println("\npath is " + Arrays.toString(path));
			System.out.println("Path length is " + Integer.toString(path.length));
			System.out.println("Rooms length is " + rooms.size());
		}
		String result = Integer.toString(path.length); //first character is number of nodes
		int leftNodes = 0;
		int rightNodes = 0;

		//get initial direction, relative to one fixed orientation of the map, 
		//because if you're travelling left, turning toward a node that is "up" from the current one means turning right,
		//but going to a node that is "left" of the current one means going straight, etc.
		int	nodeDirection = dir.get(path[0]).get(path[1]); //relative to world frame
		String str1 = String.format(" s%c_%d", rooms.get(0), 0); //Node #0. Robot goes straight from where you place it, index is 0 because why not.
		//Not checking that rooms.get(0) is an intersection, but it should be.
		// result += str1;

		int currentDirection = nodeDirection; //relative to world frame
		String dirChar = "";
		int index = 0; //Equal to either leftNodes or rightNodes, depending on the cases
		for(int i = 1; i < path.length; i++){
			nodeDirection = dir.get(path[i-1]).get(path[i]); //the direction from node i-1 to i tells you what direction you should turn from ***** node i-1 ***** !!!!!!
			//In the first iteration of this loop, nodeDirection is from 1 to 2, not from 0 to 1! The case from 0 to 1 was handled just before this for loop.

			// System.out.println(nodeDirection);
			switch(currentDirection){
			//nodeDirection is the direction node i is from node i-1 relative to world frame.
			//Each of the following cases must reason out the direction to turn relative to the robot's current frame.
			case DOWN:
				switch(nodeDirection){
					case DOWN: //robot goes straight
						dirChar = "s";
						index = 0; //have to deal with this weird case later.
					break;
					case UP: //turn around?
						dirChar = "t";
						index = 0; //have to deal with this weird case later.
					break;
					case LEFT: //robot turns right
						dirChar = "r";
						rightNodes++;
						index = rightNodes;
					break;
					case RIGHT: //robot turns left
						dirChar = "l";
						leftNodes++;
						index = leftNodes;
					break;
					default: //should be impossible
					break;
				}
				break;
			case UP:
				switch(nodeDirection){
					case UP: //robot goes straight
						dirChar = "s";
						index = 0;
					break;
					case DOWN: //turn around?
						dirChar = "t";
						index = 0; //have to deal with this weird case later.
					break;
					case RIGHT: //robot turns right
						dirChar = "r";
						rightNodes++;
						index = rightNodes;
					break;
					case LEFT: //robot turns left
						dirChar = "l";
						leftNodes++;
						index = leftNodes;
					break;
					default: //should be impossible
					break;
				}
				break;
			case LEFT:
				switch(nodeDirection){
					case LEFT: //robot goes straight
						dirChar = "s";
						index = 0;
					break;
					case RIGHT: //turn around?
						dirChar = "t";
						index = 0; //have to deal with this weird case later.
					break;
					case UP: //robot turns right
						dirChar = "r";
						rightNodes++;
						index = rightNodes;
					break;
					case DOWN: //robot turns left
						dirChar = "l";
						leftNodes++;
						index = leftNodes;
					break;
					default: //should be impossible
					break;
				}
				break;
			case RIGHT:
				switch(nodeDirection){
					case RIGHT: //robot goes straight
						dirChar = "s";
						index = 0;
					break;
					case LEFT: //turn around?
						dirChar = "t";
						index = 0; //have to deal with this weird case later.
					break;
					case DOWN: //robot turns right
						dirChar = "r";
						rightNodes++;
						index = rightNodes;
					break;
					case UP: //robot turns left
						dirChar = "l";
						leftNodes++;
						index = leftNodes;
					break;
					default: //should be impossible
					break;
				}
				break;
			default: //should be impossible
				break;
			}
			currentDirection = nodeDirection;
			// System.out.println(currentDirection);
			String str = String.format(" %s%c_%d", dirChar, rooms.get(i-1), index);
			System.out.println(path[i-1]);
			System.out.println(str);
			result += str;
		}
		return result;
	}

	public static int[] completePath(ArrayList<ArrayList<Float>> A, int[] path, ArrayList<Character> rooms){
		/*
		Calculate the complete, literal path through the building, including duplicate nodes,
		 given a path that omits duplicate nodes, such as one returned by optimizeSA().

		rooms is the rooms list of the compact path. When this function is called, path.length must be equal to rooms.size().
		rooms is modified by this function to be the rooms list for the complete path, so it will be longer.
		The length of the return value of this function will be equal to the final size of rooms.
		*/

		ArrayList<Character> oldRooms = new ArrayList<Character>();
		oldRooms.addAll(rooms);
		rooms.clear();

		ArrayList<Integer> result = new ArrayList<Integer>();
		if(path.length > 0){
			result.add(path[0]);
			rooms.add(oldRooms.get(path[0])); //add room entry for this node.
			for(int i = 1; i < path.length; i++){
				if((float)A.get(i-1).get(i) > 0.0f){
					result.add(path[i]);
					rooms.add(oldRooms.get(path[i])); //add room entry for this node.
				}else{ //no edge from path[i-1] to path[i]
					int[] subpath = shortestPath(A, path[i-1], path[i], false);
					for(int k = 1; k < subpath.length; k++){ //ignore subpath[0] because it was already added on the previous iteration
						result.add(subpath[k]);
						rooms.add(oldRooms.get(subpath[k])); //add room entry for this node.
					}
				}
			}
		}
		return Generic.arrayListToArray(result);
	}

	public static int[] shortestPath(ArrayList<ArrayList<Float>> A, int S, int F, boolean verbose){
		/*
		Returns the shortest path between nodes S and F. A is the weighted adjacency matrix.
		The calling function can compute path cost by calling Simulator.cost() on the return value of this function.

		 Uses Dijkstra's algorithm, as described by MATH 3120 Fall 2015, Section 5.9, page 128
		Translating this into Java from a pseudocode language designed to be readable for mathematicians was a bit of a job, but oh well,
		example Java code for Dijkstra on the internet is all crap anyways, they want to literally implement Node and Edge objects to store the graph, 
		rather than just using a matrix :/
		*/

		int n = A.size(); //number of nodes in the graph

		ArrayList<ArrayList<Integer>> pathTable = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < n; i++){
			pathTable.add(new ArrayList<Integer>());
			pathTable.get(i).add(S); //every S-V path starts with S.
		}

		ArrayList<Integer> U = new ArrayList<Integer>();
		U.add(S);
		float[] d = new float[n]; //list of shortest distances from S to n
		d[S] = 0; //distance from S to itself is 0
		for(int v = 0; v < n; v++){ //initialization loop for d
			if(v != S && (float)A.get(S).get(v) > 0.0f){ //there is an edge from S to v
				d[v] = A.get(S).get(v);
				pathTable.get(v).add(v); //right now, the shortest path we know so far to v is just the edge from S to v.
			}else{
				d[v] = Float.POSITIVE_INFINITY;
			}
		}
		while(true){
			float mindist = Float.POSITIVE_INFINITY;
			int u = -1;
			for(int v = 0; v < n; v++){ //this loop seeks the next uk
				//check if U contains v
				boolean contains = false;
				for(int k = 0; !contains && k < U.size(); k++){
					if((int)U.get(k) == v){ contains = true; } //need to compare the unboxed primitive
				}

				if(!contains){ //v not in U
					if(d[v] < mindist){
						u = v;
						mindist = d[v];
					}
				}
			}
			if(u == F){ break; } //Tests the termination condition
			U.add(u);
			if(verbose){
				System.out.print("U is ");
				System.out.println(U);
			}
			for(int v = 0; v < n; v++){ //this loop updates distances for vertices adjacent to Uk
				//check if U contains v
				boolean contains = false;
				for(int k = 0; !contains && k < U.size(); k++){
					if((int)U.get(k) == v){ contains = true; } //need to compare the unboxed primitive
				}

				if(!contains && (float)A.get(v).get(u) > 0.0f){ //v not in U and there's no u,v edge
					if(d[v] > d[u] + (float)A.get(u).get(v)){
						//change the minimum S-v distance to the minimum S-u distance plus the u-v edge weight. 
						//and replace the path to v with the path to u, plus v.
						d[v] = d[u] + (float)A.get(u).get(v); //d[v] = min( d[v], d[u] + A.get(u).get(v) )
						pathTable.get(v).clear();
						pathTable.get(v).addAll(pathTable.get(u)); //path to u
						pathTable.get(v).add(v); //plus v
						if(verbose){
							System.out.printf("Updating path to v=%d via u=%d\n", v, u);
							System.out.print("New path is ");
							System.out.println(pathTable.get(v));
						}
					}
				}
			}
		}
		if(verbose){
			System.out.printf("Dijkstra: S = %d, F = %d, cost = %f\n", S, F, (float)d[F]);
			Generic.printMatrix(pathTable);
		}

		return Generic.arrayListToArray(pathTable.get(F)); //return type is int[]
	}

	public static void mapMatrixFromCSV(String file,  ArrayList<ArrayList<Float>> adj,  ArrayList<ArrayList<Integer>> dir, ArrayList<Character> rooms, boolean verbose){
		/*
		adj, dir and rooms are modified by this function.

		This function is not in CSVParsing because LEFT, RIGHT, UP, DOWN are constants that are specific to this application and should be defined in this file.
		Read a 2-D matrix of any size from a csv file that represents a building map.
		The calling function MUST be responsible for ensuring that the first n rows are the weighted adjacency matrix, 
		and the last row is the list of 'i' and 'r' characters, which indicate whether the node corresponding to that column is an intersection or a room.
		The last row will be copied into rooms.

		IMPORTANT!!! This function requires the file to be formatted in a VERY SPECIFIC WAY:
		Matrix organized like a weighted adjacency matrix, except each entry has either l, r, u or d, 
		corresponding to left, right, up, down respectively, immediately after the number with no characters separating them.
		If there is no direction, there is no letter after the number.
		If the matrix is not organized in this way, this function will not work at all.

		Example of a valid csv file smallGraph3Map.csv: (TABS ADDED ONLY FOR READABILITY. ACTUAL CSV FILE MUST CONTAIN NO TABS OR SPACES)
		0,		-1,		-1,		2l,		-1,		-1,		-1,		3r,		-1,		-1,		-1,		-1,		-1,		0.5u,	-1,		-1,		-1
		-1,		0,		-1,		-1,		-1,		-1,		3r,		-1,		-1,		-1,		-1,		-1,		4u,		-1,		-1,		-1,		2d
		-1,		-1,		0,		-1,		-1,		7r,		-1,		-1,		-1,		-1,		-1,		4u,		-1,		-1,		-1,		3d,		-1
		2r,		-1,		-1,		0,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		1l,		-1
		-1,		-1,		-1,		-1,		0,		-1,		-1,		-1,		8l,		-1,		-1,		-1,		1r,		-1,		-1,		-1,		-1
		-1,		-1,		7l,		-1,		-1,		0,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		0.5r,	-1,		-1,		-1
		-1,		3l,		-1,		-1,		-1,		-1,		0,		-1,		-1,		-1,		6r,		-1,		-1,		-1,		-1,		-1,		-1
		3l,		-1,		-1,		-1,		-1,		-1,		-1,		0,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		1r
		-1,		-1,		-1,		-1,		8r,		-1,		-1,		-1,		0,		-1,		-1,		1l,		-1,		-1,		-1,		-1,		-1
		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		0,		-1,		-1,		-1,		-1,		2u,		-1,		-1
		-1,		-1,		-1,		-1,		-1,		-1,		6l,		-1,		-1,		-1,		0,		-1,		-1,		-1,		2r,		-1,		-1
		-1,		-1,		4d,		-1,		-1,		-1,		-1,		-1,		1r,		-1,		-1,		0,		-1,		-1,		-1,		-1,		-1
		-1,		4d,		-1,		-1,		1l,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		0,		-1,		-1,		-1,		-1
		0.5d,	-1,		-1,		-1,		-1,		0.5l,	-1,		-1,		-1,		-1,		-1,		-1,		-1,		0,		-1,		-1,		-1
		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		2d,		2l,		-1,		-1,		-1,		0,		-1,		-1
		-1,		-1,		3u,		1r,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		0,		-1
		-1,		2u,		-1,		-1,		-1,		-1,		-1,		1l,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		0


		Store weights in adj, directions in dir. Directions for l, r, u, d become -1, 1, 2 and -2 respectively.
		*/

		final String DEFAULT_DELIMITER = ","; //commas for csv, could potentially change this for other formats

		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			String[] lineArray = line.split(DEFAULT_DELIMITER);
			int n = lineArray.length; //This is the number of columns in the matrix. Calling function must ensure that the number of rows is n+1.

			int i = 0;
			while(line != null){
				if(i == n){ //last row of the csv file
					for(int k = 0; k < lineArray.length; k++){
						rooms.add(lineArray[k].charAt(0));
					}
					break;
				}
				adj.add(new ArrayList<Float>());
				dir.add(new ArrayList<Integer>());
				for(int j = 0; j < lineArray.length; j++){
					String entry = lineArray[j];
					char last = entry.charAt(entry.length() - 1);
					if(last == 'l'){
						String x = entry.substring(0, entry.length() - 1); //remove last character
						adj.get(i).add(Float.parseFloat(x));
						dir.get(i).add(LEFT);
					}else if(last == 'r'){
						String x = entry.substring(0, entry.length() - 1); //remove last character
						adj.get(i).add(Float.parseFloat(x));
						dir.get(i).add(RIGHT);
					}else if(last == 'u'){
						String x = entry.substring(0, entry.length() - 1); //remove last character
						adj.get(i).add(Float.parseFloat(x));
						dir.get(i).add(UP);
					}else if(last == 'd'){
						String x = entry.substring(0, entry.length() - 1); //remove last character
						adj.get(i).add(Float.parseFloat(x));
						dir.get(i).add(DOWN);
					}else{ //no direction
						adj.get(i).add(Float.parseFloat(entry));
						dir.get(i).add(NO_DIRECTION);
					}
				}
				line = br.readLine();
				if(line != null){
					lineArray = line.split(DEFAULT_DELIMITER);
				}
				i++;
			}
			if(verbose){
				System.out.println(adj.size());
				System.out.println(dir.size());
			}

		}catch(IOException e){
			System.out.println(e);
		}
	}
}
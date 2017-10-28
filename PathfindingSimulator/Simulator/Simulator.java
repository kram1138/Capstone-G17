/**
Pathfinding Simulator.

@author Lucas Wiebe-Dembowski
@since 10/26/2017
*/
package Simulator;

import java.util.ArrayList;
import java.util.Arrays;

import GenericCode.Generic;

public class Simulator{
	public static int[] optimizeSA(ArrayList<ArrayList<Float>> distMatrix, int start, int nEL, int nCL, double initialTemperature, double finalTemperature, boolean verbose){
		//Based mostly on ECE 3790 lab 3

		int r1 = -1, r2 = -1; //random array indices, initialized to different invalid index values
		int t; //temporary variable
		int n = distMatrix.size(); //number of nodes in the graph
		int[] soln = new int[n]; //currently the best solution seen so far
		float currentCost; //cost of the best solution seen so far
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

		float dC; //Change in cost
		double p; //Probability of choosing a worse solution
		double dCavg = 0; //average change in cost
		double currentTemperature = initialTemperature;
		double dT = (initialTemperature - finalTemperature) / nCL;

		if(verbose){
			System.out.printf("Initial solution = %s\n", Arrays.toString(soln));
			System.out.printf("Initial solution cost = %f\n", currentCost);
		}
		for(int x = 0; x < nCL && tempCost != 0 && currentCost != 0; x++){ //cooling loop.
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
				}else{
					dC = tempCost - currentCost;

					if(i == 0){
						dCavg = (double)dC; //The average of one data point is itself
					}else{
						//Update the average of a set of data points after adding a data point
						dCavg = (i * dCavg + dC) / (i + 1);
					}

					p = Math.exp((double)(-dC) / (dCavg * currentTemperature)); //Boltzmann probability
					if(p > Math.random()){
						//Keep the worse solution
						System.arraycopy(temp, 1, soln, 1, n-1);
						currentCost = tempCost;
					}
				}
			}

			currentTemperature -= dT; //linear cooling schedule
		}
		return soln;
	}

	public static float cost(ArrayList<ArrayList<Float>> distMatrix, int start, int[] soln){
		//Calculate the cost of some configuration of the graph by adding the edge weights
		//between the left and right side.
		//int[] soln is an array containing the sequence of node numbers representing a solution.
		//ArrayList<ArrayList<Float>> distMatrix is the all pairs shortest distance matrix of the graph.

		float result = 0;
		int n = soln.length;
		for(int i = 0; i < n-1; i++){
			result += distMatrix.get(soln[i]).get(soln[i+1]);
		}
		result += distMatrix.get(soln[n-1]).get(start); //go back to the starting point at the end.
		return result;
	}
}
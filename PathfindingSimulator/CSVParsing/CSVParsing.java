/**
Includes functions for reading a 2D float matrix from a csv file into a 
2D square Float ArrayList, or printing a 2D Float ArrayList to a csv file,
or printing a 1-D float list to a file in one column.

Floats were used instead of doubles because the application using this will not need double precision.

@author Lucas Wiebe-Dembowski
@since 10/26/2017
*/
package CSVParsing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVParsing{

	public static ArrayList<ArrayList<Float>> matrixListFromCSV(String file){
		//Read a 2-D float matrix of any size from a csv file, and return the result as a 2D Arraylist of Floats
		//If a square matrix is required, the calling function must be responsible for ensuring that that is the case.

		ArrayList<ArrayList<Float>> matrix = new ArrayList<ArrayList<Float>>();
		final String DEFAULT_DELIMITER = ","; //commas for csv, could potentially change this for other formats

		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			String[] lineArray = line.split(DEFAULT_DELIMITER);

			int i = 0;
			while(line != null){
				matrix.add(new ArrayList<Float>());
				for(int j = 0; j < lineArray.length; j++){
					matrix.get(i).add(Float.parseFloat(lineArray[j]));
				}
				line = br.readLine();
				if(line != null){
					lineArray = line.split(DEFAULT_DELIMITER);
				}
				i++;
			}

		}catch(IOException e){
			e.printStackTrace();
		}

		return matrix;
	}

	public static <T> void matrixToCSV(ArrayList<ArrayList<T>> matrix, String file){
		//Print a 2D float matrix to a file. Assume the file is a .csv file.
		//Prints everything in the matrix, regardless of size.
		//If a square matrix is required, the calling function must be responsible for ensuring that that is the case.

		try{
			PrintWriter pr = new PrintWriter(file);
			
			for(int i = 0; i < matrix.size(); i++){
				for(int j = 0; j < matrix.get(i).size(); j++){
					pr.print(matrix.get(i).get(j));
					if(j + 1 < matrix.get(i).size()){
						pr.print(",");
					}
				}

				if(i < matrix.size() - 1){
					//skip a line after every row of the matrix except for the last one
					pr.println();
				}
			}

			pr.close();

		}catch(IOException e){
			e.printStackTrace();
		}		
	}

	public static void listToFile(List<Float> list, String file){
		//Print a float list to a file in one column. File can be csv or txt.

		try{
			PrintWriter pr = new PrintWriter(file);
			
			for(int i = 0; i < list.size(); i++){
				pr.print(list.get(i));
				if(i < list.size() - 1){
					//skip a line after every entry of the list except for the last one
					pr.println();
				}
			}

			pr.close();

		}catch(IOException e){
			e.printStackTrace();
		}		
	}
}

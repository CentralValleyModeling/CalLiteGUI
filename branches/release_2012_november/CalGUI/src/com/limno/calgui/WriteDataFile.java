package com.limno.calgui;

import java.io.*;
import java.util.Scanner;
public class WriteDataFile {
	private static int writeTable(String outputFileName, float[][] result) throws IOException {
		OutputStream outputStream;
		try 
		{
			outputStream = new FileOutputStream(outputFileName);
		}
		catch (FileNotFoundException e) {
			System.out.println("Cannot open input file " + outputFileName);
			return -1;
		} 
		PrintStream output = new PrintStream(outputStream);
		output.println("Generic header line");
		output.println("Month WYT Value");
		for (int m = 0; m < result.length; m++) 
			for (int w = 0; w < result[m].length; w++){
				output.printf("%d %d %f\n",m+1,w+1,result[m][w]);
			}
		output.close();
		outputStream.close();
		return 0;
	}
	private static float[][] readTable(String inputFileName){
		// Open input file
		Scanner input;
		try 
		{
			input = new Scanner(new FileReader(inputFileName));
		}
		catch (FileNotFoundException e) {
			System.out.println("Cannot open input file " + inputFileName);
			return null;
		}
		// Create result array
		float[][] result = new float[12][5];
		for (int m = 0; m < 12; m ++)
			for (int w = 0; w < 5; w++)
				result[m][w] = (float) -99999.0;
		// Read all lines
		int lineCount = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lineCount++;
			if (lineCount > 2) {
				// Parse, assuming space and/or tab-delimited
				String[] parts = line.split("[ \\t]+");
				if (parts.length == 3) {
					try
					{
						int month = Integer.parseInt(parts[0]);
						int wyt = Integer.parseInt(parts[1]);
						float value = Float.parseFloat(parts[2]);
						result[month-1][wyt-1] = value;
						// System.out.println(Integer.toString(month) + " " + Integer.toString(wyt) + " " + Float.toString(value));
					}
					catch (NumberFormatException nfe)
					{
						System.out.println("NumberFormatException: " + nfe.getMessage());
						return null;
					}
				}
			}
		}
		input.close();
		return result;
	}
	public static void main(String[] args) {
		float[][] test = readTable("D:\\calgui\\gui_qwest.table");
		if (test == null) {
			System.out.println("No table read");
		}
		else {
			try {
				writeTable("D:\\calgui\\gui_qwest.table.dump",test);
			}
			catch (IOException ioe) {
				System.out.println("Exception: " + ioe.getMessage());
			}
		}
	}
}

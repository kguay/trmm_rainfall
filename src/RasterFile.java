import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.omg.CORBA.portable.InputStream;


public class RasterFile {
	public float[][] data;
	public String filename;
	public String size;
	
	/** Construction **/	

	
	/**
	 * @param month: number (int) representing the month (1-12)
	 * @param year:  number (int) representing the year (1998-2009)
	 */
	public RasterFile(int month, int year) {
		String monthName = intToMonth(month);

		data = new float[100][360];

		filename = "tot" + monthName + year + ".tif.txt";
		//System.out.println("data/"+filename);

		Scanner in;
		in = new Scanner(getClass().getResourceAsStream("data/" + filename));

		for (int r = 0; r < 100; r++){
			for (int c = 0; c < 360; c++){
				if(in.hasNext()){
					//String nextLine = in.nextLine();
					//Scanner line = new Scanner(nextLine);
					//data[r][c] = line.;
					//System.out.println("(" + r + "," + c + ") " + data[r][c]);
					
					try
					{
						data[r][c] = Float.valueOf(in.next().trim()).floatValue();
					}
					catch (NumberFormatException nfe)
					{
						System.out.println("NumberFormatException: " + nfe.getMessage());
					}

					//System.out.println("(" + r + "," + c + ") " + data[r][c]);
					
				}
				else{
					System.out.println("nope!");
				}
			}
		}

	}

	/** Getters & Setters **/

	public float[][] getData() {
		return data;
	}

	public String getFilename() {
		return filename;
	}


	/**
	 * @param intMonth number representing a month (from 1 to 12)
	 * @return the name of the month represented by the number (intMonth)
	 * returns "" (empty string) if the number is out of range (not 1 to 12)
	 */
	public static String intToMonth(int intMonth){
		switch (intMonth){

		case 1: return "jan";
		case 2: return "feb";
		case 3: return "mar";
		case 4: return "apr";
		case 5: return "may";
		case 6: return "jun";
		case 7: return "jul";
		case 8: return "aug";
		case 9: return "sep";
		case 10: return "oct";
		case 11: return "nov";
		case 12: return "dec";
		default: return "";
		}
	}

	/**
	 * 
	 * @param r row
	 * @param c column
	 * @return the float value in cell (r,c)
	 */
	public float getCell(int row, int col){
		return data[row][col];
	}


}

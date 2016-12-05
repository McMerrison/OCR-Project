//import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.io.*;
//import java.util.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.InputMismatchException;

public class Main {

	/*public static void runner() {
		String s = null;
		ArrayList<String> keys;
		ArrayList<String> outputs;

		try {

			// run the Unix "ps -ef" command
			// using the Runtime exec method:
			Process p = Runtime.getRuntime().exec("ls -al");

			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			//System.exit(0);
		}
		catch (IOException e) {
			System.out.println("Exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}
	
		//compare(keys, outputs);
	}*/


	/**
	* Compares corresponding indexes of the key and output arrays
	* Calls the .pl script to compare key vs. output of OCR
	* TODO: Redirect input in .pl file so key/output come from arrays, not files
	* TODO: Redirect output scores to matrix
	* @param key string, output of OCR string
	* @return score from this alignment
	**/
	public static int compare(String key, String output) {
		String[] cmd = {"perl", "NWA.pl", key, output};
		StringBuilder perlout = new StringBuilder();
		//Runtime runtime = Runtime.getRuntime();
		ProcessBuilder pb = new ProcessBuilder(cmd);
		try {
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				perlout.append(line);
				perlout.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Running command: " + cmd[1]);
		String score = perlout.toString();
		score = score.trim();
		int s = Integer.valueOf(score);
		//System.out.println(s);
		//System.out.println(score);
		return s;
	}

	/**
	* Given a folder, parse each text file into a string
	* Store string in ArrayList
	* @param path of folder containing text files
	* @return array of strings, each containing the contents of one text file
	**/
	public static ArrayList<String> getAllKeys(String path) throws FileNotFoundException {
		// for all files in ImageKeys
		// scan in all text to String
		// add to arrayList and return
		String s = "";
		String filepath;
		ArrayList<String> allKeys = new ArrayList<>();
		Path dir = Paths.get(path);
		int files = 0;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			//For each file
			for (Path file: stream) {
				//System.out.println("Reading from " + file.getFileName());
				//Scanner in = new Scanner(new File("ImageKeys/Demolm01.txt"));
				filepath = path + file.getFileName();
				Scanner in = new Scanner(new File(filepath));
				//Pattern grab = Pattern.compile("[a-z]");
				while (in.hasNext()) {
					s += in.next();
					//System.out.println(s);
				}
				allKeys.add(s);
				s = "";
				files++;
			}
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
		return allKeys;
		//Scanner in = new Scanner(new file ());

	}
	
	/**
	* @param array of scores
	* @return average of scores
	**/
	public static int getAverage(int[] array) {
		int sum = 0;
		int i = array.length;
		for (int n = 0; n < i; n++) {
			sum += array[n];
		}
		return sum/i;
	}
	
	/**
	* Use given directories of keys and outputs, parse text files into strings
	* Store strings in ArrayLists
	* Run compare() on each index to determine accuracy of OCR based on expected output
	* @param directory containing keys, directory containing outputs
	* (TODO): @return score array
	*/
	public static int[] align(String keydir, String outputdir) {
		try {
			//Array of keys and outputs
			ArrayList<String> keys;
			ArrayList<String> outputs;
			
			//Get key values from given directory
			keys = getAllKeys(keydir);
			//Get outputs from given directory
			outputs = getAllKeys(outputdir);
			
			//Create score array from # of keys (matches # of outputs)
			int[] scores = new int[keys.size()];
			
			//Uncomment to see what's in the arrays
			/*System.out.println("List of Keys:");
			System.out.println(keys);
			System.out.println("List of Outputs:");
			System.out.println(outputs);*/
			
			//For each key/output matching, get the alignment score and store in array
			//Indexes will match
			for (int i = 0; i < keys.size(); i++) {
				scores[i] = compare(keys.get(i), outputs.get(i));
				//System.out.println("Score of alignment " + i + ": " + scores[i]);
			}
			//System.out.println("Average score is " + getAverage(scores));
			return scores;
		} catch (FileNotFoundException e) {
			System.err.println("FAIL: " + e.getMessage() );
			return null;
		}
	}
	
	public static void printScores(int[] array) {
		for (int n = 0; n < array.length; n++) {
			System.out.println("Image " + n + ": " + array[n]);
		}
	}
	
	public static void printScoresCompare(String ocr1, String ocr2, int[] array1, int[] array2) {
		System.out.println("                 " + ocr1 + "   " + ocr2);
		for (int n = 0; n < array1.length; n++) {
			System.out.println("Image " + n + ": " 
			+ "\n" + ocr1 + ": " + array1[n] 
			+ "\n" + ocr2 + ": " + array2[n] + "\n"); 
		}
	}
	
	public static void printOCR(String[] array) {
		System.out.println("OCRS:");
		System.out.println("ID: Name");
		for (int n = 0; n < array.length; n++) {
			System.out.println(n + ": " + array[n]);
		}
	}
	
	public static void pressAny()
	{ 
        System.out.println("Press Enter to continue...");
        try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
	}

	/**
	* For each OCR:
	* (TODO): Run OCR on image files, generate a folder of outputs
	* (TODO): Pass the name of this folder to outputdir
	* (Done): Pass outputdir to analyze method.
	* (Done): analyzeOCR will call:
	* 	(Done): getAllKeys() to parse data 
	*	(Done): compare() to generate scores
	* (Done): Keep scores in separate data structures, allow user to select options
	* (Done): Implement each method of analysis
	*/
	public static void main(String args[]) throws FileNotFoundException {
		//This is where the keys are stored, does not change during session
		String f = "parameters.txt";
		Scanner s = new Scanner(new File(f)); 
		Scanner t = new Scanner(System.in);
		System.out.println("Provide name of directory containing key outputs");
		String keydir = s.next() + "/";
		String outputdir = "";
		
		//For now: Choose '1' OCR and name it "Tess" (arbitrary)
		System.out.println("How many OCRs?");
		int numOCR = s.nextInt();
		System.out.println("How many images in this set?");
		int numImg = s.nextInt();
		int[][] scores = new int[numOCR][numImg];
		String[] OCRs = new String[numOCR];
		System.out.println();
		//Testing OCRs
		for (int x = 0; x < numOCR; x++) {
			System.out.println("Select a name for OCR " + (x+1));
			OCRs[x] = s.next();
			//Run the OCR
			//generate a folder of outputs
			System.out.println("Please provide directory of outputs");
			outputdir = s.next() + "/";
			System.out.println("Analyzing...");
			scores[x] = align(keydir, outputdir);
			System.out.println("Done.");
		}	
		
		//Analyzing OCRs
		System.out.println();
		boolean analyzing = true;
		//Error to fix: Goes into infinite loop when letter is entered
		while (analyzing) {
			printOCR(OCRs);
			System.out.println("----------");
			System.out.println("Select an option:\n" +
			"0 - Show scores of OCR\n" +
			"1 - Show Average Scores\n" +
			"2 - Compare OCRs\n" +
			"3 - Tabulate data by image\n" +
			"4 - Save scores to file\n" +
			"5 - Exit");
			
			//ERROR to fix: 
			//Goes into infinite loop when letter is entered instead of number
			int option;
			try {
				option = t.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Please enter a number 1-6");
				option = 7;
			}
			
			switch (option) {
				case 0: System.out.println("Display scores of which OCR? (enter ID)");
						int ID = t.nextInt();
						System.out.println("Scores of "+
						OCRs[ID] + ": ");
						printScores(scores[ID]);
						System.out.println();
						break;						
				case 1: for (int n = 0; n < numOCR; n++) {
							System.out.println("Average of "+
							OCRs[n] + " is: " + getAverage(scores[n]));
						}
						System.out.println();
						break;
						
				case 2: System.out.println("ID of First OCR: ");
						int first = t.nextInt();
						System.out.println("ID of Second OCR: ");
						int second = t.nextInt();
						System.out.println("Average of " + OCRs[first] + ": " + getAverage(scores[first]));
						System.out.println("Average of " + OCRs[second] + ": " + getAverage(scores[second]));
						System.out.println("Compare by image? (y/n)");
						String ans = t.next();
						if (ans.equals("y")) {
							printScoresCompare(OCRs[first], OCRs[second], scores[first], scores[second]);
						} else if (ans.equals("n")) {
							break;
						} else {
							System.out.println("Please enter (y/n)");
						}
						break;
						
				case 3:
						break;
						
				case 4: try {
							PrintWriter out = new PrintWriter("OCRData.txt");
							for (int n = 0; n < numOCR; n++) {
								out.println(OCRs[n] + ":");
								for (int m = 0; m < scores[n].length; m++) {
									out.println("Image " + m + ": " + scores[n][m]);
								}
								out.println("Average Score: " + getAverage(scores[n]));
								out.println();
							}
							System.out.println("Saved to 'OCRData.txt'");
							out.close();
						} catch (FileNotFoundException e) {
							
						}
						break;
						
				case 5: analyzing = false;
						break;
						
				case 6: analyzing = false;
						break;
						
				default: break;
			}
			pressAny();
			System.out.println();
		}
	}
}
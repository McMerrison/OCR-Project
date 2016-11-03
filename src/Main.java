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

public class Main {

	public static void runner() {
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
	
		//compare(/*keys, outputs*/);
	}


	/**
	* Compares corresponding indexes of the key and output arrays
	* Calls the .pl script to compare key vs. output of OCR
	* TODO: Redirect input in .pl file so key/output come from arrays, not files
	* TODO: Redirect output scores to matrix
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

	public static ArrayList<String> getAllKeys() throws FileNotFoundException {
		// for all files in ImageKeys
		// scan in all text to String
		// add to arrayList and return
		String s = "";
		String filepath;
		ArrayList<String> allKeys = new ArrayList<>();
		Path dir = Paths.get("ImageKeys");
		int files = 0;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			//For each file
			for (Path file: stream) {
				//System.out.println("Reading from " + file.getFileName());
				//Scanner in = new Scanner(new File("ImageKeys/Demolm01.txt"));
				filepath = "ImageKeys/" + file.getFileName();
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
	
	public static int getAverage(int[] array) {
		int sum = 0;
		int i = array.length;
		for (int n = 0; n < i; n++) {
			sum += array[n];
		}
		return sum/i;
	}

	public static void main(String args[]) {
		try {
			//Array of keys and outputs
			ArrayList<String> keys;
			ArrayList<String> outputs;
			
			//Get key values from given directory
			keys = getAllKeys();
			//Get outputs from given directory
			//NOTE: Currently reads from same directory as keys, change later
			outputs = getAllKeys();
			
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
				System.out.println("Score of alignment " + i + ": " + scores[i]);
			}
			System.out.println("Average score is " + getAverage(scores));
		} catch (FileNotFoundException e) {
			System.err.println("FAIL: " + e.getMessage() );
		}
	}
}
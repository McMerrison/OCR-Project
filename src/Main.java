import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

	public static void runner() {
		String s = null;
		String[] keys = new String[50];
		String[] outputs = new String[50];

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

		compare(keys, outputs);
	}


	/**
	* Compares corresponding indexes of the key and output arrays
	* Calls the .pl script to compare key vs. output of OCR
	* TODO: Redirect input in .pl file so key/output come from arrays, not files
	* TODO: Redirect output scores to matrix
	**/
	public static void compare(String[] keys, String[] outputs) {
		String cmd = "NWA.pl";
		System.out.println("Running command: " + cmd);
		try {
			Process p = new ProcessBuilder("perl").redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT).start();
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			w.write(cmd);
			w.close();
			p.waitFor();
			System.out.println("Script executed successfully");
		} 	catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getAllKeys() throws FileNotFoundException {
		// for all files in ImageKeys
		// scan in all text to String
		// add to arrayList and return
		ArrayList<String> allKeys = new ArrayList<>();
		Path dir = Paths.get("./src/ImageKeys");
		int files = 0;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path file: stream) {
				//System.out.println(file.getFileName());
				files++;
			}
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
		Scanner in = new Scanner(new File("./src/ImageKeys/Demolm01.txt"));
		//Pattern grab = Pattern.compile("[a-z]");

		System.out.println(in.next());
		return allKeys;
		//Scanner in = new Scanner(new file ());

	}

	public static void main(String args[]) {
		try {
			getAllKeys();
		} catch (FileNotFoundException e) {
			System.err.println("FAIL: " + e.getMessage() );
		}
	}
}

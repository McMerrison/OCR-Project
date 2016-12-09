import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.List;

public class Main {

	public static boolean saveListToFile(String folderName, ArrayList<Image> output) {
		File dir = new File(folderName);
		if (dir.exists() || dir.mkdir()) {
			int i = 1;
			try {
				for (Image s : output) {
					PrintWriter out = new PrintWriter(String.format("%sOutput%02d.txt", folderName,i));
					out.print(s.output);
					out.close();
					i++;
				}
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: Couldn't print outputs to file");
				return false;
			}
		} else {
			System.out.println("ERROR: Couldn't create directory");
			return false;
		}
		return true;
	}

	/**
	 * Takes in a command and an image directory and then runs the command on
	 * all images in the image directory and returns the result as an arraylist.
	 * TODO: Possibly make pipes work TODO: Function is currently in testing
	 * mode
	 * 
	 * @param cmd
	 *            String, this is the command with picture.bmp
	 * @param imageDir
	 *            String, output of OCR string
	 * @return outputs ArrayList<String>, returns an arraylist with all outputs
	 **/
	public static ArrayList<Image> runner(String cmd, String imageDir) {
		String filepath;
		ArrayList<Image> outputs = new ArrayList<>();
		Path dir = Paths.get(imageDir);
		int pIndex = cmd.indexOf("image.bmp");
		if (pIndex != -1) {	// this if makes sure that picture.bmp exists in the command
			String firstHalf = cmd.substring(0, pIndex);
			String secondHalf = cmd.substring(pIndex + 9);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
				for (Path file : stream) {
					filepath = imageDir + file.getFileName();
					String command = firstHalf + filepath + secondHalf;
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
					BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";
					String output = "";
					while ((line = buf.readLine()) != null) {
						output += line + "\n";
					}
					Image img = new Image(output);
					outputs.add(img);
				}
			} catch (IOException | DirectoryIteratorException x) {
				// IOException can never be thrown by the iteration.
				// In this snippet, it can only be thrown by newDirectoryStream.
				System.err.println(x);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("ERROR: image.bmp not in command.");
		}
		return outputs;
	}

	/**
	 * Compares corresponding indexes of the key and output arrays Calls the .pl
	 * script to compare key vs. output of OCR TODO: Redirect input in .pl file
	 * so key/output come from arrays, not files TODO: Redirect output scores to
	 * matrix
	 * 
	 * @param key
	 *            string, output of OCR string
	 * @return score from this alignment
	 **/
	public static int compare(String key, String output) {
		String[] cmd = { "perl", "NWA.pl", key, output };
		StringBuilder perlout = new StringBuilder();
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
		String score = perlout.toString();
		score = score.trim();
		int s = Integer.valueOf(score);
		return s;
	}

	/**
	 * Given a folder, parse each text file into a string Store string in
	 * ArrayList
	 * 
	 * @param path
	 *            of folder containing text files
	 * @return array of strings, each containing the contents of one text file
	 **/
	public static ArrayList<String> getAllKeys(String path) {
		String s = "";
		String filepath;
		ArrayList<String> allKeys = new ArrayList<>();
		Path dir = Paths.get(path);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path file : stream) {
				filepath = path + file.getFileName();
				Scanner in = new Scanner(new File(filepath));
				while (in.hasNext()) {
					s += in.next();
				}
				allKeys.add(s);
				s = "";
				in.close();
			}
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
		return allKeys;
	}

	/**
	 * Given a folder, parse each text file into a string Store string in
	 * ArrayList
	 * 
	 * @param path
	 *            of folder containing text files
	 * @return array of strings, each containing the contents of one text file
	 **/

	public static ArrayList<Image> loadOCR(String path) {
		ArrayList<String> outputs = getAllKeys(path);
		ArrayList<Image> images = new ArrayList<Image>();
		for (String s : outputs) {
			images.add(new Image(s));
		}
		return images;
	}

	public static void BoxAndWhisker(ArrayList<OCR> OCRs) {
		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		for (OCR o : OCRs) {
			List<Double> list = new ArrayList<Double>();
			for (Image img : o.Images) {
				list.add((double) (img.score));
			}
			dataset.add(list, "OCR", o.name);
		}
		final CategoryAxis xAxis = new CategoryAxis("OCR");
		final NumberAxis yAxis = new NumberAxis("Score");
		yAxis.setAutoRangeIncludesZero(false);
		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setSeriesPaint(0, Color.WHITE);
		renderer.setSeriesOutlinePaint(0, Color.BLACK);
		renderer.setUseOutlinePaintForWhiskers(true);
		renderer.setMeanVisible(false);
		final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
		final JFreeChart chart = new JFreeChart("OCR BoxPlot", new Font("SansSerif", Font.BOLD, 14), plot, false);
		final File file = new File("Chart.png");
		try {
			ChartUtilities.saveChartAsPNG(file, chart, 800, 500);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use given directories of keys and outputs, parse text files into strings
	 * Store strings in ArrayLists Run compare() on each index to determine
	 * accuracy of OCR based on expected output
	 * 
	 * @param directory
	 *            containing keys, directory containing outputs
	 * @return o OCR
	 */
	public static OCR align(ArrayList<String> a, OCR o) {
		int size = a.size();
		if (a.size() > o.Images.size()) {
			size = o.Images.size();
		}
		for (int i = 0; i < size; i++) {
			o.Images.get(i).score = compare(a.get(i), o.Images.get(i).output);
		}
		o.calcScores();
		return o;
	}

	public static void printScores(ArrayList<Image> Images) {
		int i = 1;
		for (Image img : Images) {
			System.out.println(String.format("Image %02d: " + img.score, i));
			i++;
		}
	}

	public static void printScoresCompare(OCR f, OCR s) {
		int amt = f.Images.size();
		if (f.Images.size() > s.Images.size()) {
			amt = s.Images.size();
		}
		System.out.println(String.format("\n%-10s   %10s   %10s", "Image", f.name, s.name));
		for (int i = 0; i < amt; i++) {
			System.out.println(String.format("%-10s   %10d   %10d", String.format("Image %02d:", i + 1),
					f.Images.get(i).score, s.Images.get(i).score));
		}
	}

	public static void printOCRs(ArrayList<OCR> OCRs) {
		int i = 0;
		for (OCR o : OCRs) {
			System.out.println("[" + i + "] - " + o.name);
			i++;
		}
	}

	public static void analyzeOCRs(ArrayList<OCR> ocrs, ArrayList<String> keys) {
		for (OCR o : ocrs) {
			align(keys, o);
			o.calcScores();
		}
	}

	/**
	 * For each OCR: (Done): Run OCR on image files, generate a folder of
	 * outputs (Done): Pass the name of this folder to outputdir (Done): Pass
	 * outputdir to analyze method. (Done): analyzeOCR will call: (Done):
	 * getAllKeys() to parse data (Done): compare() to generate scores (Done):
	 * Keep scores in separate data structures, allow user to select options
	 * (Done): Implement each method of analysis
	 * 
	 * @throws FileNotFoundException
	 */
	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);
		ArrayList<OCR> OCRs = new ArrayList<>();
		System.out.println("\n  ___   ____ ____       _                _                    ");
		System.out.println(" / _ \\ / ___|  _ \\     / \\   _ __   __ _| |_   _ _______ _ __ ");
		System.out.println("| | | | |   | |_) |   / _ \\ | '_ \\ / _` | | | | |_  / _ \\ '__|");
		System.out.println("| |_| | |___|  _ <   / ___ \\| | | | (_| | | |_| |/ /  __/ |   ");
		System.out.println(" \\___/ \\____|_| \\_\\ /_/   \\_\\_| |_|\\__,_|_|\\__, /___\\___|_|  ");
		System.out.println("                                           |___/              \n");
		
		System.out.println("Loading config file 'parameters.txt'");
		ArrayList<String> Keys = new ArrayList<>();
		String keyDir = "ImageKeys/";
		String imageDir = "TestImages/";
		try {
			File file = new File("parameters.txt");
			Scanner input = new Scanner(file);

			while (input.hasNextLine()) {
				String line = input.nextLine();
				if (line.contains("(Keys Folder)")) {
					keyDir = input.nextLine();
				} else if (line.contains("(Images Folder)")) {
					imageDir = input.nextLine();
				} else if (line.contains("(OCR)")) {
					if (input.hasNextLine())
						System.out.println("Loading the follwing OCRs:");
					while (input.hasNextLine()) {
						String oName = input.nextLine();
						oName = oName.replace("\n", "").replace("\r", "").replace(" ", "");
						System.out.println("- " + oName);
						OCR ocr = new OCR(oName, loadOCR(oName + "/"));
						OCRs.add(ocr);
					}
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Couldn't find 'parameters.txt'");
		}
		Keys = getAllKeys(keyDir);
		analyzeOCRs(OCRs, Keys);

		boolean analyzing = true;
		while (analyzing) {
			System.out.println("\n----------------");
			System.out.println("Select an option:\n" + 
			"0 - Show scores of OCR\n" + 
			"1 - Show Average Scores\n" +
			"2 - Compare OCRs\n" + 
			"3 - Add an OCR\n" + 
			"4 - Save scores to file\n" + 
			"5 - Graph Data\n" +
			"6 - Exit");

			int option = 7;
			do {
				while (!s.hasNextInt())
					s.next();
				option = s.nextInt();
			} while (option < 0 || option > 6);
			switch (option) {
			case 0:
				if (OCRs.size() == 0) {
					System.out.println("You need to have at least one OCR loaded to use this option.");
					break;
				}else if(OCRs.size() == 1){
					OCR tmp = OCRs.get(0);
					System.out.println("Scores of " + tmp.name + ": ");
					printScores(tmp.Images);
					System.out.println();
					break;
				}else{
					System.out.println("Display scores of which OCR? (Enter ID)");
					printOCRs(OCRs);
					int id;
					do {
						while (!s.hasNextInt())
							s.next();
						id = s.nextInt();
					} while (id < 0 || id > OCRs.size()-1);
					OCR tmp = OCRs.get(id);
					System.out.println("Scores of " + tmp.name + ": ");
					printScores(tmp.Images);
					System.out.println();
					break;
				}

			case 1:
				if(OCRs.isEmpty()){
					System.out.println("You need to have at least one OCR loaded to use this option.");
					break;
				}
				for (OCR i : OCRs) {
					i.calcScores();
					i.printScores();
				}
				System.out.println();
				break;

			case 2:
				int first;
				int second;
				if (OCRs.size() < 2) {
					System.out.println("You need to have atleast two OCRs loaded to use this option.");
					break;
				}else if(OCRs.size() == 2){
					first = 0;
					second = 1;
				}else{
					printOCRs(OCRs);
					System.out.println("ID of First OCR: ");
					do {
						while (!s.hasNextInt())
							s.next();
						first = s.nextInt();
					} while (first < 0 || first > OCRs.size()-1);
					System.out.println("ID of Second OCR: ");
					do {
						while (!s.hasNextInt())
							s.next();
						second = s.nextInt();
					} while (second < 0 || second > OCRs.size()-1 && second != first);
				}
				OCR fOCR = OCRs.get(first);
				OCR sOCR = OCRs.get(second);
				printScoresCompare(fOCR, sOCR);
				break;

			case 3:
				System.out.println("Please enter a name for this OCR");
				OCR otmp = new OCR(s.next());
				System.out.println("Would you like to load this OCR via a command or a folder?\n"
						+ "Enter 1 for Command or 2 for Folder");
				int answer;
				do {
					while (!s.hasNextInt())
						s.next();
					answer = s.nextInt();
				} while (answer < 1 || answer > 2);
				if (answer == 1) {
					System.out.println("Please enter the command used to run this OCR\n"
							+ "Use image.bmp where you would normally place the image filename.");
					s.useDelimiter("\n");
					otmp.cmd = s.next();
					otmp.Images = runner(otmp.cmd, imageDir);
					if (otmp.Images.isEmpty()) {
						System.out.println("ERROR: Command failed.");
						break;
					}
					align(Keys, otmp);
					otmp.calcScores();
					saveListToFile(otmp.name+"/",otmp.Images);
					if (OCRs.add(otmp)) {
						System.out.println("Added: " + otmp.name);
					}
				} else if (answer == 2) {
					System.out.println("Please enter the folder name or path\n" + "Example: gocr\\ ");
					String oName = s.next();
					oName = oName.replace("\n", "").replace("\r", "").replace(" ", "");
					otmp.folder = oName;
					otmp.Images = loadOCR(otmp.folder);
					if(otmp.Images.isEmpty()){
						System.out.println("ERROR: Failed to load images from "+otmp.folder);
						break;
					}
					align(Keys, otmp);
					otmp.calcScores();
					if (OCRs.add(otmp))
						System.out.println("Successfully added: " + otmp.name);
				}
				break;

			case 4:
				if(OCRs.isEmpty()){
					System.out.println("You need to have at least one OCR loaded to use this option.");
					break;
				}
				try {
					PrintWriter out = new PrintWriter("OCRData.txt");
					for (OCR o : OCRs) {
						o.calcScores();
						out.println("\n-----" + o.name + "-----");
						out.println("Average Score: " + o.avgScore);
						out.println("Highest Score: " + o.highScore);
						out.println("Lowest Score: " + o.lowScore + "\n");
					}
					out.close();
					System.out.println("Summary of scores saved to ODRData.txt");
				} catch (FileNotFoundException e) {
					System.out.println("ERROR: Failed to write OCR data to file.");
				}
				break;

			case 5:
				if(OCRs.isEmpty()){
					System.out.println("You need to have at least one OCR loaded to use this option.");
					break;
				}
				BoxAndWhisker(OCRs);
				System.out.println("Graph saved to 'Chart.png'");
				break;

			default:
				s.close();
				analyzing = false;
				System.exit(0);
				break;
			}
			System.out.println();
		}
	}
}

class OCR {
	public String name;
	String cmd;
	String folder;
	ArrayList<Image> Images = new ArrayList<Image>();
	int avgScore;
	int highScore;
	int lowScore;

	public OCR(String name) {
		this.name = name;
	}

	public OCR(String name, ArrayList<Image> i) {
		String fname = name + "/";
		folder = fname;
		this.name = name;
		Images = i;
	}

	/**
	 * @param array
	 *            of scores
	 * @return average of scores
	 **/
	public void calcScores() {
		avgScore = getAverage();
		highScore = getHighest();
		lowScore = getLowest();
	}

	public void printScores() {
		System.out.println("\n-----" + name + "-----");
		System.out.println("Average Score: " + avgScore);
		System.out.println("Highest Score: " + highScore);
		System.out.println("Lowest Score: " + lowScore);
	}

	private int getHighest() {
		int highscore = Integer.MIN_VALUE;
		for (Image i : Images) {
			if (i.score > highscore) {
				highscore = i.score;
			}
		}
		return highscore;
	}

	private int getLowest() {
		int lowscore = Integer.MAX_VALUE;
		for (Image i : Images) {
			if (i.score < lowscore) {
				lowscore = i.score;
			}
		}
		return lowscore;
	}

	private int getAverage() {
		int sum = 0;
		int i = Images.size();
		for (int n = 0; n < i; n++) {
			sum += Images.get(n).score;
		}
		return sum / i;
	}

}

class Image {
	String output;
	int score;

	public Image(String output) {
		this.output = output;
	}
}

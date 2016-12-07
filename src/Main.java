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

import java.util.InputMismatchException;
import java.util.List;

public class Main {

	public static boolean saveListToFile(String folderName,ArrayList<String> output){
		File dir = new File(folderName);
		if(dir.mkdir()){
			int i = 0;
			try {
				for(String s: output){
				PrintWriter out = new PrintWriter(folderName+"Output" + i +".txt");
				out.print(s);
				out.close();
				i++;
				}
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: Couldn't print to file");
				//e.printStackTrace();
				return false;
			}
		}else{
			System.out.println("ERROR: Couldn't create directory");	
			return false;
		}
		return true;
	}
	
	/**
	* Takes in a command and an image directory and then runs the command on all images
	* in the image directory and returns the result as an arraylist.
	* TODO: Possibly make pipes work
	* TODO: Function is currently in testing mode
	* @param cmd String, this is the command with picture.bmp
	* @param imageDir String, output of OCR string
	* @return outputs ArrayList<String>, returns an arraylist with all outputs
	**/
	public static ArrayList<Image> runner(String cmd,String imageDir) {
		String filepath;
		ArrayList<Image> outputs = new ArrayList<>();
		imageDir = "TestImages/";//for testing purposes
		cmd = "bmptopnm picture.bmp | gocr -v 0 -m 0 -e - -f UTF8 -"; //this string doesnt work because of the pipe
		Path dir = Paths.get(imageDir);
		int pIndex = cmd.indexOf("picture.bmp");
		
		//this if makes sure that picture.bmp exists in the command
		if(pIndex > -1){
			//System.out.println("Picture.bmp exists in file");
			String firstHalf = cmd.substring(0,pIndex);
			String secondHalf = cmd.substring(pIndex+11);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
				//For each file
				for (Path file: stream) {
					
					System.out.println("Reading from " + file.getFileName());
					filepath = imageDir + file.getFileName();
					String command = firstHalf+filepath+secondHalf;
					
					//Process p = Runtime.getRuntime().exec(command);//run the command
					Process p = Runtime.getRuntime().exec("ls");
					p.waitFor();
					BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";
					String output = "";

					while ((line = buf.readLine()) != null) {
						output += line + "\n";
					 }
					 Image img = new Image(output);
					 outputs.add(img);

					  System.out.println(output);

				}
			} catch (IOException | DirectoryIteratorException x) {
				// IOException can never be thrown by the iteration.
				// In this snippet, it can only be thrown by newDirectoryStream.
				System.err.println(x);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}else{
			System.out.println("ERROR: picture.bmp not in command.");
		}
		return outputs;
	}
	


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
		String score = perlout.toString();
		score = score.trim();
		int s = Integer.valueOf(score);
		return s;
	}

	/**
	* Given a folder, parse each text file into a string
	* Store string in ArrayList
	* @param path of folder containing text files
	* @return array of strings, each containing the contents of one text file
	**/
	public static ArrayList<String> getAllKeys(String path){
		String s = "";
		String filepath;
		ArrayList<String> allKeys = new ArrayList<>();
		Path dir = Paths.get(path);
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
			}
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
		return allKeys;
	}
	
	/**
	* Given a folder, parse each text file into a string
	* Store string in ArrayList
	* @param path of folder containing text files
	* @return array of strings, each containing the contents of one text file
	**/

	public static ArrayList<Image> loadOCR(String path){
		ArrayList<String> outputs = getAllKeys(path);
		ArrayList<Image> images = new ArrayList<Image>();
		for(String s: outputs){
			images.add(new Image(s));
		}
		return images;
	}
	
    public static void BoxAndWhisker(ArrayList<OCR> OCRs) {

        final DefaultBoxAndWhiskerCategoryDataset dataset 
        = new DefaultBoxAndWhiskerCategoryDataset();
        for (OCR o: OCRs) {
        	List<Double> list = new ArrayList<Double>();
            for(Image img : o.Images){
                list.add((double)(img.score));
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
        final JFreeChart chart = new JFreeChart(
            "OCR BoxPlot",
            new Font("SansSerif", Font.BOLD, 14),
            plot,
            false
        );
        final File file= new File("Chart.png");
        try {
			ChartUtilities.saveChartAsPNG(file, chart, 800, 500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	
	/**
	* Use given directories of keys and outputs, parse text files into strings
	* Store strings in ArrayLists
	* Run compare() on each index to determine accuracy of OCR based on expected output
	* @param directory containing keys, directory containing outputs
	* (TODO): @return score array
	*/
	public static OCR align(ArrayList<String> a, OCR o) {
		int size = a.size();
		if(a.size()>o.Images.size()){
			size = o.Images.size();
		}
		for (int i = 0; i < size; i++) {
			o.Images.get(i).score = compare(a.get(i), o.Images.get(i).output);
		}
		o.calcScores();
		return o;
	}
	
	public static void printScores(ArrayList<Image> Images) {
		int i = 0;
		for (Image img: Images) {
			System.out.println("Image " + i + ": " + img.score);
			i++;
		}
	}
	
	public static void printScoresCompare(OCR f, OCR s) {
		int amt = f.Images.size();
		if(f.Images.size()>s.Images.size()){
			amt = s.Images.size();
		}
		System.out.println(String.format("\n%-10s   %10s   %10s","Image",f.name,s.name));
		for (int i = 0; i<amt; i++) {
			System.out.println(String.format("%-10s   %10d   %10d","Image"+i+":",f.Images.get(i).score,s.Images.get(i).score ));
		}
	}
	
	public static void printOCRs(ArrayList<OCR> OCRs) {
		int i = 0;
		for(OCR o: OCRs){
			System.out.println("["+i+"] - " + o.name);
			i++;
		}
	}
	
	public static void analyzeOCRs(ArrayList<OCR> ocrs,ArrayList<String> keys){
		for(OCR o: ocrs){
			align(keys,o);
			o.calcScores();
		}
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
	 * @throws FileNotFoundException 
	*/
	public static void main(String args[]){
		//This is where the keys are stored, does not change during session
		Scanner s = new Scanner(System.in);
		ArrayList<OCR> OCRs = new ArrayList<>();
		System.out.println("Loading config file 'parameters.txt' .....");
		ArrayList<String> Keys = new ArrayList<>();
		String keyDir = "ImageKeys/";
		String imageDir = "TestImages/";
        try {
    	    File file = new File("parameters.txt");
            Scanner input = new Scanner(file);

            while (input.hasNextLine()) {
                String line = input.nextLine();
                if(line.contains("(Keys Folder)")){
                	keyDir = input.nextLine();
                }else if(line.contains("(Images Folder)")){
                	imageDir = input.nextLine();
                }else if(line.contains("(OCR)")){
                	if(input.hasNextLine())
                		System.out.println("Loading the follwing OCRs:");
                	while(input.hasNextLine()){
                		String oName = input.nextLine();
                		oName = oName.replace("\n", "").replace("\r", "").replace(" ", "");
                		System.out.println("- "+oName);
                		OCR ocr = new OCR(oName,loadOCR(oName+"/"));
                		OCRs.add(ocr);
                	}
                }
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
		Keys = getAllKeys(keyDir);
		analyzeOCRs(OCRs,Keys);
		System.out.println("Done.\n");
		
		boolean analyzing = true;
		//Error to fix: Goes into infinite loop when letter is entered
		while (analyzing) {
			System.out.println("----------");
			System.out.println("Select an option:\n" +
			"0 - Show scores of OCR\n" +
			"1 - Show Average Scores\n" +
			"2 - Compare OCRs\n" +
			"3 - Add an OCR\n" +
			"4 - Save scores to file\n"+
			"5 - Graph Data\n"+
			"6 - Exit");
			
			int option = 7;
			try {
				do{
					while (!s.hasNextInt()) s.next();//This should fix the infinite loop
					option = s.nextInt();
				}while(option < 0 || option > 6);//Make sure selected number is in requested range
			} catch (InputMismatchException e) {
				System.out.println("Please enter a number 0-6");
			}
			switch (option) {
				case 0: System.out.println("Display scores of which OCR? (Enter ID)");
						printOCRs(OCRs);
						int id = s.nextInt();
						OCR tmp = OCRs.get(id);
						System.out.println("Scores of "+
						tmp.name + ": ");
						printScores(tmp.Images);
						System.out.println();
						break;						
				case 1: for (OCR i: OCRs) {
							i.calcScores();
							i.printScores();
						}
						System.out.println();
						break;
						
				case 2: printOCRs(OCRs);//this needs work------------------------ 
						System.out.println(OCRs.size());
						if(OCRs.size()<2){
							System.out.println("You need to have atleast 2 OCRs Loaded to use this option");
							break;
						}
						System.out.println("ID of First OCR: ");
						int first = s.nextInt();//make sure int is in list 
						System.out.println("ID of Second OCR: ");
						int second = s.nextInt();//make sure int is in list
						OCR fOCR = OCRs.get(first);
						OCR sOCR = OCRs.get(second);
						printScoresCompare(fOCR,sOCR);
						break;
						
				case 3:
						OCR otmp = new OCR();
						System.out.println("Please enter a name for this OCR");
						otmp.name = s.next();
						System.out.println("Would you like to load this OCR via a command or a folder?\n"
										 + "Enter 1 for Command or 2 for Folder");
						int answer = s.nextInt();
						if(answer == 1){
							System.out.println("Please enter the command used to run this OCR\n" +
									"Use image.bmp where you would normally place the image filename.");
							otmp.cmd = s.next();
							otmp.Images = runner(otmp.cmd, imageDir);
							if(otmp.Images == null){
								System.out.println("ERROR: Command failed to ");
								break;
							}
							align(Keys,otmp);
							otmp.calcScores();
							if(OCRs.add(otmp)){
								System.out.println("Added: "+ otmp.name+"\n");
							}
						}else if(answer == 2){
							System.out.println("Please enter the folder name or path\n"
									+ "Example: gocr\\ ");
	                		String oName = s.next();
	                		oName = oName.replace("\n", "").replace("\r", "").replace(" ", "");
							otmp.folder = oName;
							System.out.println(otmp.folder);
							otmp.Images = loadOCR("ImageKeys/");
							align(Keys,otmp);
							otmp.calcScores();
							if(OCRs.add(otmp)){
								System.out.println("Added: "+ otmp.name+"\n");
							}
						}else{
							System.out.println("You didn't enter 1 or 2");
						}
						break;
						
				case 4: try {
							PrintWriter out = new PrintWriter("OCRData.txt");
							for(OCR o: OCRs){
								o.calcScores();
								out.println("\n-----"+ o.name +"-----");
								out.println("Average Score: "+ o.avgScore);
								out.println("Highest Score: "+ o.highScore);
								out.println("Lowest Score: "+ o.lowScore +"\n");
							}
							out.close();
							System.out.println("Summary of scores saved to ODRData.txt");
						} catch (FileNotFoundException e) {
							
						}
						break;
						
				case 5: BoxAndWhisker(OCRs);
						System.out.println("Graph saved to 'Chart.png'");
						break;
						
				case 6: analyzing = false;
						System.exit(0);
						break;
						
				default: break;
			}
			System.out.println();
		}
	}
}

class OCR {
	public String name;//name of ocr
	String cmd;//cmd to run ocr
	String folder;
	ArrayList<Image> Images = new ArrayList<Image>();//arraylist containing output and score
	int avgScore;//average score the ocr produced
	int highScore;//highest score the ocr produced
	int lowScore;//lowest score the ocr produced
	
	public OCR(){
	}
	public OCR(String name, ArrayList<Image> i){
		String fname = name+"/";
		folder = fname;
		this.name = name;
		Images = i;
	}
	
	/**
	* @param array of scores
	* @return average of scores
	**/
	public void calcScores(){
		avgScore = getAverage();
		highScore = getHighest();
		lowScore = getLowest();
	}
	
	public void printScores(){
		System.out.println("\n-----"+ name +"-----");
		System.out.println("Average Score: "+ avgScore);
		System.out.println("Highest Score: "+ highScore);
		System.out.println("Lowest Score: "+ lowScore);
	}
	
	private int getHighest(){
		int highscore = -10000;
		for(Image i: Images){
			if(i.score>highscore){
				highscore = i.score;
			}
		}
		return highscore;
	}
	
	private int getLowest(){
		int lowscore = +10000;
		for(Image i: Images){
			if(i.score<lowscore){
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
		return sum/i;
	}
	
}

class Image {
	String output;
	int score;
	
	public Image(String output){
		this.output = output;
	}
}

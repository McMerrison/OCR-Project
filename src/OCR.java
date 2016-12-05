import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class OCR {
	String name;//name of ocr
	String cmd;//cmd to run ocr
	ArrayList<Image> Images = new ArrayList<>();//arraylist containing output and score
	int avgScore;//average score the ocr produced
	int highScore;//highest score the ocr produced
	int lowScore;//lowest score the ocr produced
	
	public OCR(Scanner s,String imageDir){
		System.out.println("Please enter a name for this OCR");
		name = s.next();
		System.out.println("Please enter the command used to run this OCR\n" +
				"Use image.bmp where you would put the image");
		cmd = s.next();
		runner(cmd,imageDir);
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
	
	public void runner(String cmd,String imageDir) {
		/*for this we need to find out how many images are in the directory
		 * then with this we cycle through, creating a new image for each, 
		 * adding the image output via the constructor and then add that image
		 * to Images. 
		 * 
		 * We should get this working and then worry about when a file is created later
		 * as normally the ocr will print the output to the console.
		 */
		String s = null;
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
	
	//add calculate highest score
	
	//add calculate lowest score
}

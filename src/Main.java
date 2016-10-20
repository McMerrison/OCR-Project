import java.io.*;

public class Main {

    public static void main(String args[]) {

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
}

# CISC475 Project
OCR Statistical Comparative Analysis
Team 5: Talha Ehtasham, Sam Flomenberg, Pravallika Santhil, Jae Yoo, Gary Sidoti


https://github.com/McMerrison/CISC475-Project 


## Features
Prompt user for different functionality:

1) Show average score of a given OCR: given an OCR, test images, test keys, give the average score of an OCR across all test images.

2) Save some or all data to text file.

3) Compare against other OCRs: given average score of an OCR across all test images, aggregate scores.

4) Tabulate scores across OCRs by image: Tabulate scores across OCRs by image: show all images in rows and different OCRs in columns; populate cells with scores.

## Design
### Pseudocode: 
While Testing:

Read command line input: OCR_Program (arg 1) optional flag (images) (test keys)
	Runtime()
	
Redirect output of OCR program to a data structure 
	String[] subjects = new String[len]
	
Build array of expected outputs for each image (provided by user)
	String[] keys = new String[len]
	
Compare test data structure with key data structure
	Needleman-Wunsch Algorithm
	
Create array of scores, assign to test data structure
	int scores_OCRname[] = new int[len]
	
	scores_OCRname[n] = compare(subject[n], key[n])
Prompt for next OCR



### While Analyzing:
Prompt user for different functionality:

1) show average score of a given OCR: given an OCR, test images, test keys, give the average score of an OCR across all test images.

2) compare against other OCRs: given average score of an OCR across all test images, aggregate scores.

3) tabulate scores across OCRs by image: Tabulate scores across OCRs by image: show all images in rows and different OCRs in columns; populate cells with scores.

4) Save some or all data to text file.

## Instructions
1. Download "src" folder

2. chdir into src

4. Open "Parameters.txt"

5. Edit as such:

	line 2: Path to directory of images
	
	line 4: Path to directory of keys (text files with expected output of image set)*
	
	line 6 onwards: list of directories containing output from each OCR
	

6. run "javac -cp jars/jfreechart-1.0.19.jar:jars/jcommon-1.0.23.jar: *.java"
7. run "java -cp jars/jfreechart-1.0.19.jar:jars/jcommon-1.0.23.jar: Main"

8. Follow prompt, edit "Parameters.txt" accordingly

*Note: Ideally, place key/output directories in src folder

### Format of output directories
See "ImageKeys" and "TesseractOutput" folders for reference. Each text file should correspond to an image.

1. Run OCR on image and redirect output to text file (any name)

2. Repeat for all images and place text files in a folder (any name)

3. Provide this folder name in Parameters.txt under correpsonding OCR nickname

4. Keys will need to be entered manually (as only a human can determine the actual contents of an image). However, ImageKeys has already been provided for the image set provided. If a new image set is used, new key outputs will need to be written.


## Testing

We used manual testing to test the functionality of our program. The following is a list of all the design requirements for the project, and their testing status.

Read command line input: OCR_Program (arg 1) optional flag (images) (test keys)
	Runtime() - TESTED.
	
Redirect output of OCR program to a data structure 
	String[] subjects = new String[len] - TESTED.
	
Build array of expected outputs for each image (provided by user)
	String[] keys = new String[len] - TESTED.
	
Compare test data structure with key data structure
	Needleman-Wunsch Algorithm - TESTED.
	
Create array of scores, assign to test data structure
	int scores_OCRname[] = new int[len] - TESTED.
	
	scores_OCRname[n] = compare(subject[n], key[n]) - TESTED.
	
Show average score of a given OCR: given an OCR, test images, test keys, give the average score of an OCR across all test images.  - TESTED.

Compare against other OCRs: given average score of an OCR across all test images, aggregate scores.  - TESTED.

Tabulate scores across OCRs by image: Tabulate scores across OCRs by image: show all images in rows and different OCRs in columns; populate cells with scores.  - TESTED.

Prompt for next OCR  - TESTED.

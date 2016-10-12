# CISC475-Project
OCR Statistical Comparative Analysis

# Requirements
Test and compare the effectiveness of various Optical Character Recognition software.

# Design
While Testing:  
	1) Read command line input: OCR_Program (arg 1) optional flag (images) (test keys)  
		- Runtime()  
	2) Redirect output of OCR program to a data structure   
		- String[] subjects = new String[len]  
	3) Build array of expected outputs for each image (provided by user)  
		- String[] keys = new String[len]  
	4) Compare test data structure with key data structure  
		- Needleman-Wunsch Algorithm  
	5) Create array of scores, assign to test data structure  
		- int scores_OCRname[] = new int[len]  
		- scores_OCRname[n] = compare(subject[n], key[n])  
	6) Prompt for next OCR  
  
While Analyzing:  
	Prompt user for different functionality:  
	1) show average score of a given OCR: given an OCR, test images, test keys, give the average score of an OCR across all test images   
	2) compare against other OCRs: given average score of an OCR across all test images, aggregate scores  
	3) graph data: specify x axis and y axis, then graph; x can be image file, y can be OCR score  
	4) tabulate scores across OCRs by image: Tabulate scores across OCRs by image: show all images in rows and different OCRs in columns; populate cells with scores  
	5) Save some or all data to text file.  


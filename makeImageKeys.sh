#!/bin/sh

# reads in a files with all the image keys
# outputs individual files with image keys and appropriate titles

rm -rf ImageKeys/
mkdir ImageKeys
while read f; do # while reading in each line of the file
	# if it starts with Demo, touch a new file
	# else, write to previous file
	if [ `echo $f | grep -c "Demo" ` -gt 0 ]
	then 
		if [ `echo $f | grep -c "Demolm[0-9]\." ` -gt 0 ]
		then
			MY_DIGIT="0$(echo $f | grep -o [0-9])"
	    else
			MY_DIGIT=$(echo $f | grep -o [0-9][0-9])
		fi
		PREVIOUS_FILE="ImageKeys/Demolm${MY_DIGIT}.txt"
		touch ${PREVIOUS_FILE}
		#echo $f
		echo "file made: ${PREVIOUS_FILE}"
	else 
		echo "appended: ${f}"
		echo $f >> ${PREVIOUS_FILE}
	fi
done < imageKeys.txt

#!/bin/sh

#check if there's an equal number of imageKeys and tess output
rm -rf TesseractAnalysis/
mkdir TesseractAnalysis

MASTER_OUT=TesseractAnalysis/masterOut.txt
SCORES=TesseractAnalysis/scores.csv

touch $MASTER_OUT
touch $SCORES

IMAGEKEY_FILE_CT=`ls ImageKeys/ | wc -l `
TESS_FILE_CT=`ls TesseractOutput/ | wc -l `

if [ $IMAGEKEY_FILE_CT -eq $TESS_FILE_CT ]
then # get scores
	for i in `seq 1 $IMAGEKEY_FILE_CT `
	do
		if [ $i -lt 10 ] 
		then
			ID=0$i
		else
			ID=$i
		fi
		KEY="ImageKeys/"`ls ImageKeys | grep $ID`
		TEST="TesseractOutput/"`ls TesseractOutput | grep $ID`
		TO_APPEND=$(perl NWAlgorithm/NWA.pl $KEY $TEST)
		#echo "${ID}_Scores: " >> $SCORES
		#perl NWAlgorithm/NWA.pl $KEY $TEST | grep -A 1 "Score" | grep -v "Score" >> $SCORES
		SCORE=$(grep -o "Score".* <<< $TO_APPEND | grep -o [-]*[0-9][0-9]*)
		echo "${ID}, ${SCORE}" >> $SCORES	
		echo $TO_APPEND >> $MASTER_OUT	
	done
else # exit
	echo "ERROR: Mismatch in number of keys and tests"
fi

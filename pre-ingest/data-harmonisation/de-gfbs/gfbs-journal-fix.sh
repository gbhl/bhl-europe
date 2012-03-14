#!/bin/bash -e

####################################################
#
# Harmonization script for the GFBS uploads 
# which is suitable for the old and new folder structure,
#  
####################################################

if [ -z $1 ]; then
	echo "Missing required 1st parameter: <input-folder-path>"
fi
if [ -z $2 ]; then
	echo "Missing required 2nd parameter: <target-root-folder>"
fi
if [ $3 = 'clear' ]; then
	CLEAR_TARGET=1
fi

if [ -z $BHL_UTILS ]; then
	echo "environment variabel BHL_UTILS not set, see https://github.com/bhle/bhle/tree/master/pre-ingest/data-harmonisation"
fi


IN_FOLDER=$1
OUT_ROOT=$2

#  
# postprocess-xml() fixes invalid xml inperparation for using xmlstarlet
# expetcs two parameters:
# 	$1 : input file
#	$1 : output file
#
postprocess-xml() {
	tidy -xml --quiet yes --show-warnings no $1 | sed -e "s/ce://g" -e "s/sb://g" -e "s/xlink://g" -e "s/mml://g" -e "s/tb://g" >	$2
}


# 
# STEP 1: rename the IN_FOLDER /eg:./journal) to reflect the serial ID 
#  * SerialID : <ce:pii>S1439-6092.*</ce:pii>
#
IFS_TMP=$IFS
IFS='
'
TMP_ARRAY=(`egrep  -r -o --no-filename "<ce:pii>[^(<]*"  ${IN_FOLDER} | sed 's/<ce:pii>//'`)
IFS=$IFS_TMP


SERIAL_ID=${TMP_ARRAY[0]}
echo "new folder name: " $SERIAL_ID
OUT_FOLDER=$OUT_ROOT/$SERIAL_ID

if [ -d $OUT_FOLDER ]; then
	echo "out folder '$OUT_FOLDER' exists"
	if [ $CLEAR_TARGET ]; then
		echo "  clearing out folder ..."
		rm -rf $OUT_FOLDER
		mkdir -p $OUT_FOLDER
	fi
else 
	echo "creating out folder '$OUT_FOLDER'"
	mkdir -p $OUT_FOLDER
fi
echo ""

#
# STEP 2: copy full strucure into new out folder
#
cp -R $IN_FOLDER/* $OUT_FOLDER/
cd $OUT_FOLDER
WORKDIR=(`pwd`)
echo "WORKDIR: $WORKDIR"

#
# STEP 3: split article level folder names into InternalID_SequenceNumber 
#
# HINT: pdf 2 tiff: http://stackoverflow.com/questions/6002261/pdf-to-tiff-imagemagick-problem
 
# we need to process the 'old' and 'new' folder structure differently 
# => STEP 3.A and STEP 3.B
IFS_TMP=$IFS
IFS='
'
ISSUE_FOLDERS_OLD=(`find -maxdepth 1  -type d -regex "\./v[0-9]i.*"`)
VOLUME_FOLDERS_NEW=(`find -maxdepth 1  -type d -regex "\./VOL=.*"`)
IFS=$IFS_TMP

# STEP 3.A: process "old" folder structure
#   * InternalID: = $SERIAL_ID
#   * SequenceNumber: page number  :<sb:pages><sb:first-page>(/d*)</sb:first-page	
#
#
for ISSUE_FOLDER in "${ISSUE_FOLDERS_OLD[@]}"
do
	#create volume level folder if it is missing
	VOLUME_FOLDER=(`echo $ISSUE_FOLDER | sed -e "s,\(v[0-9]*\)\(i.*\),\1,g" -e "s,v,VOL=,"`)
	if [ ! -d $VOLUME_FOLDER ]; then
		mkdir $VOLUME_FOLDER
	fi

	cd $ISSUE_FOLDER
	# fix invalid xml
	postprocess-xml "$WORKDIR/$ISSUE_FOLDER/issue.xml" "/tmp/gfbs-issue.xml"

	IFS_TMP=$IFS
	IFS='
	'
	ARTICLE_FOLDERS=(`find -maxdepth 1  -type d -regex "\./S[^\.]*"`)
	IFS=$IFS_TMP
	for ARTICLE_FOLDER in "${ARTICLE_FOLDERS[@]}"
	do
		echo "processing article folder '$ISSUE_FOLDER/$ARTICLE_FOLDER'"
	 	cd $WORKDIR/$ISSUE_FOLDER/$ARTICLE_FOLDER
	 	
	 	# fix invalid xml
	 	postprocess-xml "$WORKDIR/$ISSUE_FOLDER/$ARTICLE_FOLDER/main.xml" "/tmp/gfbs-main.xml"
	 	# find pii in article metadata 
	 	PII=(`xmlstarlet sel -t -v "//pii" /tmp/gfbs-main.xml`)
	 	echo "  PII: $PII"

		# find first page in isuue metadata
	 	XPATH_QUERY="//include-item/pii[text() = '$PII']/following-sibling::pages/first-page"
	 	FIRST_PAGE=(`xmlstarlet sel -t -v "$XPATH_QUERY" /tmp/gfbs-issue.xml`)
	 	echo "  FIRST_PAGE: $FIRST_PAGE"

	 	# turn FIRST_PAGE string into plain number
		# eg. 244.e1 => 244
		ARTICLE_ID=(`echo $FIRST_PAGE | sed -e "s,^\([^\.]*\)\(.*$\),\1,"`)
		
		# check for roman numerical
		# the awk script returns 0 if it was an arabic number
		# roman numbers are beeing used at the end of the jounal
		# so add 100000 to the roman number value to move it at the end
		ARTICLE_ID_CONVERTED=(`echo $ARTICLE_ID |  awk -f $BHL_UTILS/roman_arabic.awk`)
		if [ $ARTICLE_ID_CONVERTED  -gt 0 ]; then
			echo "  has roman page number: $ARTICLE_ID_CONVERTED"
			ARTICLE_ID=$[100000+$ARTICLE_ID_CONVERTED]
	 	fi

		ARTICLE_ID=(`printf %06d $ARTICLE_ID`)
		
	 	# create new folder name
	 	NEW_FOLDER_NAME="${SERIAL_ID}_${ARTICLE_ID}"
	 	echo "  new folder name: $ARTICLE_FOLDER $NEW_FOLDER_NAME"
	 	cd ..
	 	mv $ARTICLE_FOLDER $NEW_FOLDER_NAME
	done
	cd $WORKDIR
	echo "  moving new issue folder into volume folder $VOLUME_FOLDER/"
	mv $ISSUE_FOLDER $VOLUME_FOLDER/
done

# STEP 3.B: process "new" folder structure
#   * InternalID: = $SERIAL_ID
#   * SequenceNumber: page number  :<sb:pages><sb:first-page>(/d*)</sb:first-page	
#
#
for VOLUME_FOLDER in "${VOLUME_FOLDERS_NEW[@]}"
do
	cd $VOLUME_FOLDER
	IFS_TMP=$IFS
		IFS='
		'
		ISSUE_FOLDERS=(`find -maxdepth 1  -type d -regex "\./ISU=[^\.]*"`)
		IFS=$IFS_TMP
	for ISSUE_FOLDER in "${ISSUE_FOLDERS[@]}"
	do
		cd $ISSUE_FOLDER
		# fix invalid xml
		postprocess-xml "$WORKDIR/$ISSUE_FOLDER/issue.xml" "/tmp/gfbs-issue.xml"

		set -x
		IFS_TMP=$IFS
		IFS='
		'
		ARTICLE_FOLDERS=(`find -maxdepth 1  -type d -regex "\./ART=[^\.]*"`)
		IFS=$IFS_TMP
		for ARTICLE_FOLDER in "${ARTICLE_FOLDERS[@]}"
		do
			NEW_FOLDER_NAME=(`echo $ARTICLE_FOLDER | sed -e "s,ART=,${SERIAL_ID}_,"`)
			mv $ARTICLE_FOLDER $NEW_FOLDER_NAME
		done
		cd ..
	done
	cd ..
done




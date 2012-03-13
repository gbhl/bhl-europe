#!/bin/bash -e

####################################################
#
# Harmonization script for the GFBS uploads 
# which is suitable for the old issue folder structure,
# that is all folders named like  v1i1 etc.
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
# STEP 3: split article level folder names into InternalID_SequenceNumber use from articel 
#         level metadatfile for getting information
#   * InternalID: = $SERIAL_ID
#   * SequenceNumber: page number  :<sb:pages><sb:first-page>(/d*)</sb:first-page	
#
#	HINT: pdf 2 tiff: http://stackoverflow.com/questions/6002261/pdf-to-tiff-imagemagick-problem
#
IFS_TMP=$IFS
IFS='
'
ISSUE_FOLDERS=(`find -maxdepth 1  -type d -regex "\./v[^\.=]*"`)
IFS=$IFS_TMP
for ISSUE_FOLDER in "${ISSUE_FOLDERS[@]}"
do
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
		# eg. 244.e1 => 244 AND ii => 9999
		ARTICLE_ID=(`echo $FIRST_PAGE | sed -e "s,^\([0-9i]*\)\(.*$\),\1,"`)
		
		# check for roman numerical
		# the awk script returns 0 if it was an arabic number
		# roman numbers are beeing used at the end of the jounal
		# so add 100000 to the roman number value to move it at the end
		ARTICLE_ID_CONVERTED=(`echo $ARTICLE_ID |  awk -f $BHL_UTILS/roman_arabic.awk`)
		if [ $ARTICLE_ID_CONVERTED  -gt 0 ]; then
			ARTICLE_ID=$[100000+$ARTICLE_ID_CONVERTED]
	 	fi

		ARTICLE_ID=(`printf %06d $ARTICLE_ID`)

		
	 	# create new folder name
	 	NEW_FOLDER_NAME="${SERIAL_ID}_${ARTICLE_ID}"
	 	echo "  new folder name: $NEW_FOLDER_NAME"
	 	#mv $ARTICLE_FOLDER $NEW_FOLDER_NAME
	done
	cd $WORKDIR
done


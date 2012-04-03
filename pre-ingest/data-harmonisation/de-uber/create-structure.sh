#!/bin/bash
#
#
# Structure to create:
#  Monograph
#

# Setup
#
IN_FOLDER=uber-fetched
OUT_FOLDER=uber-fixed

WORKDIR=(`pwd`)
 
OUT_FOLDER=${WORKDIR}/${OUT_FOLDER}

NAMESPACES_OAIDC="-N oai_dc=http://www.openarchives.org/OAI/2.0/oai_dc/ -N dc=http://purl.org/dc/elements/1.1/ -N xsi=http://www.w3.org/2001/XMLSchema-instance" 

#
# prepare
#
if [[ $BHL_UTILS == "" && -r ./prepare-env.sh ]]; then
	source ./prepare-env.sh
	echo "./prepare-env.sh sourced"
else 
	echo "./prepare-env.sh not found you may whant to specify BHL_UTILS manually !!!"
	exit;
fi

echo "clearing old target folder ..."
rm -rf $OUT_FOLDER
mkdir -p $OUT_FOLDER
cd $IN_FOLDER

#
# Main loop over folders
#
find -type d -regex "^./[0-9]*$" | egrep -o "[0-9]*" | while read DIR; do
	echo "processing $DIR ..."
	cd $DIR
	
	BOOK_OUT_FOLDER=${OUT_FOLDER}/$DIR
	SCANS_FOLDER=tiff
	
	# create top level folder
	mkdir $BOOK_OUT_FOLDER
	
	# copy metadata for top level
	cp *.oai_* $BOOK_OUT_FOLDER

	# convert .oai_dc metadata to pure .dc
	xmlstarlet tr $BHL_UTILS/oai_dc-to-dc.xsl metadata.oai_dc >  $BOOK_OUT_FOLDER/metadata-dc.xml

	# read doc-types
	DOC_TYPES=(`xmlstarlet sel $NAMESPACES_OAIDC -t -m "//dc:type[contains(text(), 'doc-type:')]" -v "text()" -n  metadata.oai_dc`)
	

	if [ {`echo $DOC_TYPES | grep "doc-type:book" -`} ]; then
		echo "  processing as Monograph"

		if [[ -d work ]]; then

			# if a txt metadata file exists
			METADATAFILE=(`find -name "*.txt" | xargs grep -l -e "-- STRUCTURE --"`)
			if [[ $METADATAFILE ]]; then
				echo "  using structure from txt metadata file $METADATAFILE"
				iconv -f ISO-8859-15 -t UTF-8  $METADATAFILE > $BOOK_OUT_FOLDER/metadata-and-structure.txt

				##DELETE# echo "  preparing chapter level metadata template"
				# 1. rename all dc:identifier to dc:isPartOf
				# 2. delete dc:format
				# 3. delete dc:title
				# 4. add placeholder <dc:title>{$title}</dc:title>
				#DELETE# xmlstarlet tr $BHL_UTILS/chapter-level-dc.xsl metadata.oai_dc > $BOOK_OUT_FOLDER/chapter-template-dc.xml

				echo "  running create-structure.awk  ..."

				# awk defaults on debian to mawk which causes memory corruptions !!!
				gawk -v targetFolder=$BOOK_OUT_FOLDER -v scansFolder=$SCANS_FOLDER -f $WORKDIR/create-structure.awk $BOOK_OUT_FOLDER/metadata-and-structure.txt


				# clean up
				#DELETE# rm $BOOK_OUT_FOLDER/chapter-template-dc.xml
				
			else 
				echo "  no metadata txt file found in ./work/, thus doing plain copy of scans"
				cp $SCANS_FOLDER/*.tif $BOOK_OUT_FOLDER/
			fi

		else 
			echo "  no '.work/' folder with metadata, thus doing plain copy of scans"
			cp $SCANS_FOLDER/*.tif $BOOK_OUT_FOLDER/
		fi
	else # END processing as Monograph
		echo "  unknown doc type: '"$DOC_TYPES"', thus doing plain copy of scans"
		cp $SCANS_FOLDER/*.tif $BOOK_OUT_FOLDER/
	fi 
	cd ..
done

cd ..

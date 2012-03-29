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

# prepare
#
rm -rf $OUT_FOLDER
mkdir -p $OUT_FOLDER
cd $IN_FOLDER

dosome() {
	VALUE="OK"
}

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

	# read doc-types
	DOC_TYPES=(`xmlstarlet sel $NAMESPACES_OAIDC -t -m "//dc:type[contains(text(), 'doc-type:')]" -v "text()" -n  metadata.oai_dc`)
	
	if [ {`echo $DOC_TYPES | grep "doc-type:book" -`} ]; then
		echo "  processing as Monograph"

		if [[ -d work ]]; then
			# if a txt metadata file exists
			METADATAFILE=(`find -name "*.txt" | xargs grep -rl -e "-- STRUCTURE --" work/`)
			if [[ $METADATAFILE ]]; then
				echo "  using structure from txt metadata file $METADATAFILE"
				
				# extract structure (and sequence)
				#awk '{if (index($0, "STRUCTURE")) doPrint=1} 
				#	 {if (index($0, "SEQUENCE")) doPrint=0} 
				#	 {if (doPrint==1 && ! match($0,"^--.*") && length($0) > 0 ) print $0}' $METADATAFILE > structure.txt

				#awk '{if (index($0, "SEQUENCE")) doPrint=1} 
				#	 {if (doPrint==1 && ! match($0,"^--.*") && length($0) > 0 ) print $0}' $METADATAFILE > sequence.txt
				#

				awk -v 	targetFolder=$BOOK_OUT_FOLDER -v scansFolder=$SCANS_FOLDER -f $WORKDIR/create-structure.awk $METADATAFILE

					 #cat  ._filenames
					 
					 #dosome
					 #echo $VALUE


			fi


		fi


	fi
	cd ..
done


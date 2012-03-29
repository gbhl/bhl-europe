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
	
	# create top level folder
	mkdir ${OUT_FOLDER}/$DIR
	
	# copy metadata for top level
	cp *.oai_* ${OUT_FOLDER}/$DIR/

	# read doc-types
	DOC_TYPES=(`xmlstarlet sel $NAMESPACES_OAIDC -t -m "//dc:type[contains(text(), 'doc-type:')]" -v "text()" -n  metadata.oai_dc`)
	
	if [ {`echo $DOC_TYPES | grep "doc-type:book" -`} ]; then
		echo "  processing as Monograph"

		if [[ -d work ]]; then
			# if a txt metadata file exists
			METADATAFILE=(`grep -rl -e "-- STRUCTURE --" work/`)
			if [[ $METADATAFILE ]]; then
				echo "  using structure from txt metadata file $METADATAFILE"
				
				# extract structure (and sequence)
				#awk '{if (index($0, "STRUCTURE")) doPrint=1} 
				#	 {if (index($0, "SEQUENCE")) doPrint=0} 
				#	 {if (doPrint==1 && ! match($0,"^--.*") && length($0) > 0 ) print $0}' $METADATAFILE > structure.txt

				#awk '{if (index($0, "SEQUENCE")) doPrint=1} 
				#	 {if (doPrint==1 && ! match($0,"^--.*") && length($0) > 0 ) print $0}' $METADATAFILE > sequence.txt
				#

				rm -f ._* # remove all awk generated files
				
				awk '{if (index($0, "STRUCTURE")) doPrint=1} 
					 {if (index($0, "SEQUENCE")) doPrint=0} 
					 {if ($1 == "ID_Inventar:") {id=$2; print id >> "._id"} }
					 {if (doPrint==1 && ! match($0,"^--.*") && length($2) > 0 ) { 

					 		n=split($2, pages, "-"); 
					 		if(n == 2){
					 			fromPage=pages[1];
					 			toPage=pages[2];
					 		} else {
					 			fromPage=$2;
					 			toPage=$2;
					 		}

					 		# one line per chapter subfolder
					 		i=3;
					 		chapterLine = "";
					 		while($i != "") {
					 			isPageNumber = match($i,"[0-9]+\x2E.*")
					 			if( lastToken == $i || !isPageNumber ) {
					 				if(isPageNumber) {
					 					chapterLine = chapterLine $i;
						 			} else {
						 				chapterLine = chapterLine " " $i;
						 			}
					 			}	
					 			lastToken = $i;
					 			i++;
					 		}
					    	print p, chapterLine >> "._chapters";
					 		
					 		# one image filename per line
					 		for(p=fromPage; p<=toPage; p++) {
					    		print id "_" p  "_" $1 >> "._filenames";
					 		}

					 	}
					  }' $METADATAFILE

					 cat  ._filenames
					 
					 #dosome
					 #echo $VALUE


			fi


		fi


	fi
	cd ..
done


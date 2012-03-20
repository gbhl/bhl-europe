#!/bin/bash 

NEW_FOLDER="uber-fixed"

AA="oc.hu"
BB="rlin.de"
CC="${AA}-be${BB}"

WORKDIR=(`pwd`)

CURL_OPTIONS="--progress-bar"
WGET_OPTIONS="--no-directories --continue --no-parent --progress=dot:mega -r"


mkdir $NEW_FOLDER

find -type d -regex "^./[0-9]*$" | egrep -o "[0-9]*" | while read DIR; do
	echo "processing $DIR ..."

	if [ ! -d $NEW_FOLDER/$DIR ]; then
		echo "cloning $DIR to $NEW_FOLDER/$DIR"
		cp -r $DIR $NEW_FOLDER/$DIR
	else 
		echo "$NEW_FOLDER/$DIR exists"
	fi

	cd $NEW_FOLDER/$DIR
	curl $CURL_OPTIONS -o metadata.oai_dc https://ed${CC}/OAI-2.0\?verb\=GetRecord\&identifier\=oai:HUBerlin.de:${DIR}\&metadataPrefix\=oai_dc
	curl $CURL_OPTIONS -o metadata.oai_ems https://ed${CC}/OAI-2.0\?verb\=GetRecord\&identifier\=oai:HUBerlin.de:${DIR}\&metadataPrefix\=oai_ems

	#
	# get base url
	#
	BASE_URL=(`egrep -o "http://ed${CC}/ebind/mfn/[^/]*/XML/" metadata.oai_dc | head -n 1 | sed  "s,/XML/,/,"`)

	# get last path element of BASE_URL
	#ID_STRING=(`echo  $BASE_URL | sed -e "s,/$,," -e "s,/,\n,g"  | tail -n 1`)
	#echo $ID_STRING
	
	#
	# download additional metadata
	#
	# get the index page of the book viewer (contains an TOC)
	curl  $CURL_OPTIONS -o index.tmp "${BASE_URL}XML/index.xml"
	tidy --quiet yes --show-warnings no -xml index.xml | xmllint --format - > index.xml
	rm index.tmp


	# try the work directory
	wget --progress=dot:mega -O work-index.xml "${BASE_URL}work/"
	# only download if it is an http server directory index, this avoids 
	is_dir_index=(`grep "Index of /ebind/mfn/"  work-index.xml`)
	rm work-index.xml
	echo $is_dir_index
	if [ $is_dir_index ]; then
		mkdir work
		cd work
		wget $WGET_OPTIONS "${BASE_URL}work/"
		# clean up
		rm -f index.*
		rm -f robots.txt
		cd ..
	fi

	#
	# download tiffs
	#
	mkdir tiff
	cd tiff
	wget $WGET_OPTIONS "${BASE_URL}tif/"
	# clean up
	rm -f index.*  
	rm -f robots.txt

	cd $WORKDIR
done
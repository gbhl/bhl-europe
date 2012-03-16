#!/bin/bash -e

NEW_FOLDER="uber-fixed"

AA="oc.hu"
BB="rlin.de"
CC="${AA}-be${BB}"

WORKDIR=(`pwd`)

echo "clearing folder $NEW_FOLDER"
rm -rf $NEW_FOLDER
mkdir $NEW_FOLDER

find -type d -regex "^./[0-9]*$" | egrep -o "[0-9]*" | while read DIR; do
	echo "processing $DIR ..."
	cp -r $DIR $NEW_FOLDER/$DIR

	cd $NEW_FOLDER/$DIR
	curl --progress-bar -o metadata.oai_dc https://ed${CC}/OAI-2.0\?verb\=GetRecord\&identifier\=oai:HUBerlin.de:${DIR}\&metadataPrefix\=oai_dc
	curl --progress-bar -o metadata.oai_ems https://ed${CC}/OAI-2.0\?verb\=GetRecord\&identifier\=oai:HUBerlin.de:${DIR}\&metadataPrefix\=oai_ems

	#
	# get base url
	#
	BASE_URL=(`egrep -o "http://ed${CC}/ebind/mfn/[^/]*/XML/" metadata.oai_dc | head -n 1 | sed  "s,/XML/,/,"`)
	
	#
	# download addisional metadata
	#
	wget --no-directories --no-parent --progress=dot:mega -r "${BASE_URL}work/"
	# clean up
	rm -f index.*
	rm -f robots.txt

	#
	# download tiffs
	#
	wget --no-directories --no-parent --progress=dot:mega -r "${BASE_URL}tif/"
	# clean up
	rm -f index.*  
	rm -f robots.txt
	
	cd $WORKDIR
done
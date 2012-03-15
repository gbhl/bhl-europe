#!/bin/bash

find -type d -regex "^./[0-9]*$" | egrep -o "[0-9]*" | while read DIR; do
	echo $DIR
	cd $DIR
	curl -o metadata.oai_dc https://edoc.hu-berlin.de/OAI-2.0\?verb\=GetRecord\&identifier\=oai:HUBerlin.de:${DIR}\&metadataPrefix\=oai_dc
	curl -o metadata.oai_ems https://edoc.hu-berlin.de/OAI-2.0\?verb\=GetRecord\&identifier\=oai:HUBerlin.de:${DIR}\&metadataPrefix\=oai_ems

	#
	# get base url
	#
	BASE_URL=(`egrep -o "http://edoc.hu-berlin.de/ebind/mfn/[^/]*/XML/" metadata.oai_dc | head -n 1 | sed  "s,/XML/,/,"`)
	
	#
	# download addisional metadata
	#
	wget --no-directories --no-parent -r "${BASE_URL}work/"
	# clean up
	rm index.*
	rm robots.txt

	#
	# download tiffs
	#
	wget --no-directories --no-parent -r "${BASE_URL}tif/"
	# clean up
	rm index.*
	rm robots.txt

done
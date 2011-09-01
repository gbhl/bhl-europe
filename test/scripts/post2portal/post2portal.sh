#!/bin/bash

if [ "$1" == "" -o "$2" == "" ]; then
	cat <<EOT
Usage: $0 <basedir> <portal-base-url>

e.g. $0 /tmp/files/ http://test111.ait.co.at


EOT
	exit 1
fi

cd "$1"
echo "Posting files under $(pwd)"


URI="$1/?q=file_push"

# collection ids (you find them in the URL when hovering over 
# the collection in the sidebar of the portal

CSIC=6251
LANDOE=6241
NBN=6245
RBGE=6243
RMCA=6244
UBBI=6246
UBER=6247
UCPH=6250
UHVIIKKI=6249

# FOR ALL XML FILES
for i in $(find -type f | grep xml$) 
do
	echo Uploading $i ..
	# collection is the name of the directory
	COLLECTION="$(echo $i | cut -f2 -d/)"
	eval COLLECTION_ID=\$$COLLECTION
	# extract title from first dc:title
	TITLE="$(grep -o '<dc:title>[^>]*</dc:title>' $i | sed 's,<[^>]*>,,g' | sed -e 's,&amp;,&,g' -e 's,&lt;,<,g' -e 's,&gt;,>,g' | head -n1)"


	# id is filename
	# a unique ID! when using the same the node in the portal is overwritten.
	ID="$(echo $i | cut -f3 -d/)"

	echo "  collection_id=$COLLECTION_ID ($COLLECTION)"
	echo "  title=$TITLE"

	# post id,data,title,collection_id to <portal-base-url>/?q=file_push
	curl "$URI" --data-urlencode "id=$ID" --data-urlencode "node@$i" --data "collection_id=$COLLECTION_ID" --data-urlencode "title=$TITLE"

	echo
done


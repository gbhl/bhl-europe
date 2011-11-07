#!/bin/bash -e

JAVA=/usr/bin/java
FGSEARCH_BASE=/home/bhladmin/dev/opt/archival-storage/fedora/tomcat/webapps/fedoragsearch/
SOLR_POST=/home/bhladmin/dev/apache-solr-3.3.0-orginal/example/exampledocs/post.jar
ULR=http://127.0.0.1:8983/solr/core/update
PRE_POCESSOR=/home/bhladmin/dev/opt/test/scripts/post2solrByGsearch/remove-mods-ns.sh

#
# load custom configuration from HOME/.bhl/post2solr.conf it this filed is present
#
if [ -f "$HOME/.bhl/post2solr.conf" ]
then
	. "$HOME/.bhl/post2solr.conf"
fi




if [ "$1" == "" -o "$2" == "" ]; then
	cat <<EOT
Usage: $0 <foxml-source-folder> <xsl-stylesheet>

e.g. $0 ~/dev/opt/test/foxml/ ~/dev/opt/data-management/gsearch/foxmlToSolrDemo.xslt


EOT
	exit 1
fi
if [ ! -d "$1" ]; then
	echo "$1 is no directory"
	exit 2
fi

cd $1
echo "Posting foxml files from $1"


#for i in $(find $FGSEARCH_BASE/WEB-INF/lib -type f | grep "jar$")

OUT_PRE=/tmp/post2solr.out-pre.xml
OUT=/tmp/post2solr.out.xml

# FOR ALL FILES excluding those ending with *.0 (=fedora datastream files)
for i in $(find -type f | grep -v "~$"| grep -v ".0$")
do

  IN=${i/.\//}

	#
	# execute the script defined in $PRE_POCESSOR
	#
	if [ -f "$PRE_POCESSOR" ]
	then
  echo "pre processing $IN .."
		. $PRE_POCESSOR $IN > $OUT_PRE
	else
		cp $IN $OUT_PRE
	fi
	echo "processing $IN ($OUT_PRE).."
  $JAVA -classpath $FGSEARCH_BASE/WEB-INF/classes:$FGSEARCH_BASE/WEB-INF/lib/serializer.jar:$FGSEARCH_BASE/WEB-INF/lib/xml-apis.jar:$FGSEARCH_BASE/WEB-INF/lib/xercesImpl.jar:$FGSEARCH_BASE/WEB-INF/lib/xalan.jar:$FGSEARCH_BASE/WEB-INF/lib/log4j-1.2.15.jar org.apache.xalan.xslt.Process -in $OUT_PRE -xsl $2 -out $OUT

  echo "posting $IN .."
  $JAVA -Durl=$ULR -jar $SOLR_POST $OUT

	echo
done


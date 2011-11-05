#!/bin/bash -e

JAVA=/usr/bin/java
XALAN_BASE=/opt/xalan-j_2_7_1
FGSEARCH_BASE=/home/andreas/workspaces/bhle/opt/fedoragsearch


if [ "$1" == "" -o "$2" == "" ]; then
	cat <<EOT
Usage: $0 <xsl-stylesheet> <foxml-file>

e.g. $0 ~/dev/opt/data-management/gsearch/foxmlToSolrDemo.xslt ~/dev/opt/test/foxml/test.xml


EOT
	exit 1
fi
if [ ! -e "$2" ]; then
	echo "$2 is not a file"
	exit 2
fi

echo "transforming foxml file  $2"


#for i in $(find $FGSEARCH_BASE/WEB-INF/lib -type f | grep "jar$")

OUT=/tmp/post2solr.out.xml


IN=$2
echo "processing $IN .."
$JAVA -classpath $FGSEARCH_BASE/WEB-INF/classes:$FGSEARCH_BASE/WEB-INF/lib/serializer.jar:$FGSEARCH_BASE/WEB-INF/lib/xml-apis.jar:$FGSEARCH_BASE/WEB-INF/lib/xercesImpl.jar:$FGSEARCH_BASE/WEB-INF/lib/xalan.jar:$FGSEARCH_BASE/WEB-INF/lib/log4j-1.2.15.jar org.apache.xalan.xslt.Process -in $IN -xsl $1 -out $OUT

/bin/less $OUT

echo


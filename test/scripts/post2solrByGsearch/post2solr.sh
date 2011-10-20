#!/bin/bash -x

JAVA=/usr/bin/java
XALAN_BASE=/opt/xalan-j_2_7_1
FGSEARCH_BASE=/home/andreas/workspaces/bhle/opt/fedoragsearch
SOLR_POST=/opt/apache-solr-3.3.0/example/exampledocs/post.jar
ULR=http://127.0.0.1:8983/solr/core3/update

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

OUT=/tmp/post2solr.out.xml

# FOR ALL FILES excluding those ending with *.0 (=fedora datastream files)
for i in $(find -type f | grep -v "~$"| grep -v ".0$")
do

  IN=${i/.\//}
  echo "processing $IN .."
  $JAVA -classpath $FGSEARCH_BASE/WEB-INF/classes:$FGSEARCH_BASE/WEB-INF/lib/serializer.jar:$FGSEARCH_BASE/WEB-INF/lib/xml-apis.jar:$FGSEARCH_BASE/WEB-INF/lib/xercesImpl.jar:$FGSEARCH_BASE/WEB-INF/lib/xalan.jar:$FGSEARCH_BASE/WEB-INF/lib/log4j-1.2.15.jar org.apache.xalan.xslt.Process -in $IN -xsl $2 -out $OUT

  echo "posting $IN .."
  $JAVA -Durl=$ULR -jar $SOLR_POST $OUT

	echo
done


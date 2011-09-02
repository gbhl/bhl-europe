#!/bin/bash

if [ "$1" == "" -o "$2" == "" ]; then
        cat <<EOT
Usage: $0 <indir> <outdir>

e.g. $0 /tmp/files/ /tmp/dip


EOT
        exit 1

fi
if [ ! -d "$1" ]; then
        echo "$1 is no directory"
        exit 2
fi


cd "$(dirname "$0")"
PROGDIR="$(pwd)"
OUTDIR="$2"
cd -
cd "$1"
echo "Converting files under $(pwd) to $OUTDIR"

for i in $(find  -type d)
do
	mkdir -p "$OUTDIR/$i"
	java -jar "$PROGDIR/saxon9he.jar" -xsl:"$PROGDIR/metsFedora_to_metsAit.xsl" -s:"$i" -o:"$OUTDIR/$i"
done


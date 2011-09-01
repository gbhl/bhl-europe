#!/bin/sh

if [ "$1" == "" -o "$2" == "" ]; then
        cat <<EOT
Usage: $0 <indir> <outdir>

e.g. $0 /tmp/files/ /tmp/dip


EOT
        exit 1
fi

cd "$1"
echo "Converting files under $(pwd)"


cd "$1"
for i in $(find  -type f)
do
	java -jar "$(dirname "$0")/saxon9he.jar"
done


#!/bin/bash -x

SEARCH_1="xmlns=\"http:\/\/www.loc.gov\/mods\/v3\""

SEARCH_2="VALUE=\"Inactive\"\/>"
REPLACE_2="VALUE=\"Active\"\/>"

sed -e 's/'${SEARCH_1}'//' -e 's/'$SEARCH_2'/'$REPLACE_2'/' <$1




#!/bin/bash -x

# remove mods namespace
SEARCH_1="xmlns=\"http:\/\/www.loc.gov\/mods\/v3\""

# activate all inactive objects
SEARCH_2="VALUE=\"Inactive\"\/>"
REPLACE_2="VALUE=\"Active\"\/>"

sed -e 's/'${SEARCH_1}'//' -e 's/'$SEARCH_2'/'$REPLACE_2'/' <$1




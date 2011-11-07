#!/bin/bash -x

SEARCH="xmlns=\"http:\/\/www.loc.gov\/mods\/v3\""
sed 's/'${SEARCH}'//' <$1




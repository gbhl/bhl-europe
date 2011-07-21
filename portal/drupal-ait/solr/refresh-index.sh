#!/bin/bash

# Number of items left as as displayed in ?q=admin/config/search/settings below "Indexing status"
ITEMS_LEFT=8893

# Number of items to index per cron run as as displayed in ?q=admin/config/search/settings below "Indexing throttle"
ITEMS_PER_RUN=50

# ----------------------

TIMES=$(($ITEMS_LEFT/$ITEMS_PER_RUN))

echo "will execute 'drush cron' $TIMES times"

cd ../www/

while [ $TIMES -gt 0 ]
do
	TIMES=$(($TIMES-1))
	drush cron
done
echo "done"
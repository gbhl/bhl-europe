#!/bin/bash

echo "USAGE: execute this script in its own folder."
echo "HINT: if you encounter problems after skipping this script with ctrl+c"
echo "      you may want to execute the following commands\n:"
echo "      sudo /etc/init.d/apache2 restart"
echo "      drush -r ../www vset --yes cron_semaphore 0"

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
  echo -n "$TIMES left : "
	drush cron
done
echo "done"

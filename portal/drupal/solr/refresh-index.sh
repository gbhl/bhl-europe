#!/bin/bash 

#set -x # uncomment for debugging

echo "USAGE: execute this script in its own folder."
echo "HINT: After skipping this script with ctrl+c"
echo "      you may encounter problems with the drupal cache."
echo "      Execute the following commands in case:"
echo ""
echo "      sudo /etc/init.d/apache2 restart"
echo "      drush -r ../www vset --yes cron_semaphore 0"
echo ""

# ------- CONFIGURATION -------- #

DRUSH=/usr/bin/drush
DRUPAL_HOME=../www/

# ---- END OF CONFIGURATION ---- #

cd $DRUPAL_HOME

lines=($($DRUSH sql-query "select count(*) from node where type='bhle';"))
ITEMS_LEFT=${lines[1]}

echo "Number of nodes to index: $ITEMS_LEFT"

# Number of items to index per cron run
ITEMS_PER_RUN=($($DRUSH vget --pipe apachesolr_cron_limit | egrep -o "[0-9]*"))

echo "Nodes to index per cron run: $ITEMS_PER_RUN"
echo ""

TIMES=$(($ITEMS_LEFT/$ITEMS_PER_RUN))

echo "Deleting the content from all cores"
# we need witch the default core manually since only the default core1 is affected by solr-delete-index
$DRUSH solr-delete-index # delete core 1
$DRUSH vset -yes apachesolr_default_environment metadata_core # switch to core2
$DRUSH solr-delete-index # delete core 2
$DRUSH vset -yes apachesolr_default_environment solr # switch back to core1

echo "Marking content for indexing"
$DRUSH solr-reindex

echo "Reindexing content marked for indexing."
$DRUSH solr-index

echo "Executing 'drush cron' $TIMES times ..."

while [ $TIMES -gt 0 ]
do
	TIMES=$(($TIMES-1))
  echo -n "$TIMES left : "
	$DRUSH cron
  echo "  "
done
echo "done"

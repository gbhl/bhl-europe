Setup:

Copy solr.xml, core1, core2 to your solr home directory.

Create the index:
In drupal go to administration, configuration, search and 
metadata, search settings. Click on Re-index site. Now each time 
the cron job is executed, 50 BHL-E nodes are indexed. Therefore
best is to use a script to call it several times. Currenty there
are around 9000 nodes in the database, so cron needs to be called
around 180 times.

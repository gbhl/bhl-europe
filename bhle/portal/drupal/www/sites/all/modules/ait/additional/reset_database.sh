#!/bin/sh
echo "start reset database"
mysql -u root --password="root" -e "DROP DATABASE drupal700;"
mysql -u root --password="root" -e "CREATE DATABASE drupal700;"
mysql -u root --password="root" -e "GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON drupal700.* TO 'root'@'localhost' IDENTIFIED BY 'root';"
mysql -u root --password="root" drupal700 < "/home/vmplanet/mysqldump/drupal700_ait182_2011-06-03.sql"
echo "finished reset database"
echo "press any key"
read dummy

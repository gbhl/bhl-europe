<?php
// $Id: destinations.db.mysql.inc,v 1.1.4.4 2010/12/07 06:22:59 ronan Exp $

backup_migrate_include('destinations.db');

/**
 * @file
 * Functions to handle the direct to database destination.
 */

/**
 * A destination type for saving to a database server.
 *
 * @ingroup backup_migrate_destinations
 */

class backup_migrate_destination_db_mysql extends backup_migrate_destination_db {
  function type_name() {
    return t("MySQL Database");
  }

  /**
   * Return a list of backup filetypes.
   */
  function file_types() {
    return array(
      "sql" => array(
        "extension" => "sql",
        "filemime" => "text/x-sql",
        "backup" => TRUE,
        "restore" => TRUE,
      ),
      "mysql" => array(
        "extension" => "mysql",
        "filemime" => "text/x-sql",
        "backup" => TRUE,
        "restore" => TRUE,
      ),
    );
  }

  /**
   * Declare any mysql databases defined in the settings.php file as a possible destination.
   */
  function destinations() {
    $out = array();
    global $databases;
    foreach ((array)$databases as $db_key => $target) {
      foreach ((array)$target as $tgt_key => $info) {
        // Only mysql/mysqli supported by this destination.
        $key = $db_key . ':' . $tgt_key;
        if ($info['driver'] === 'mysql') {
          $url = $info['driver'] . '://' . $info['username'] . ':' . $info['password'] . '@' . $info['host'] . (isset($info['port']) ? ':' . $info['port'] : '') . '/' . $info['database'];
          if ($destination = backup_migrate_create_destination('mysql', array('url' => $url))) {
            // Treat the default database differently because it is probably the only one available.
            if ($key == 'default:default') {
              $destination->set_id('db');
              $destination->set_name(t('Default Database'));
              // Dissalow backing up to the default database because that's confusing and potentially dangerous.
              $destination->remove_op('scheduled backup');
              $destination->remove_op('manual backup');
            }
            else {
              $destination->set_id('db:'. $key);
              $destination->set_name($key .": ". $destination->get_display_location());
            }
            $out[$destination->get_id()] = $destination;
          }
        }
      }
    }
    return $out;
  }

  /**
   * Get the file type for to backup this destination to.
   */
  function get_file_type_id() {
    return 'mysql';
  }

  /**
   * Backup the databases to a file.
   *
   *  Returns a list of sql commands, one command per line.
   *  That makes it easier to import without loading the whole file into memory.
   *  The files are a little harder to read, but human-readability is not a priority
   */
  function _backup_db_to_file($file, $settings) {
    $lines = 0;
    $exclude = !empty($settings->filters['exclude_tables']) ? $settings->filters['exclude_tables'] : array();
    $nodata = !empty($settings->filters['nodata_tables']) ? $settings->filters['nodata_tables'] : array();
    if ($file->open(TRUE)) {
      $file->write($this->_get_sql_file_header());
      $alltables = $this->_get_tables();
      foreach ($alltables as $table) {
        if (_backup_migrate_check_timeout()) {
          return FALSE;
        }
        if ($table['name'] && !isset($exclude[$table['name']])) {
          $file->write($this->_get_table_structure_sql($table));
          $lines++;
          if (!in_array($table['name'], $nodata)) {
            $lines += $this->_dump_table_data_sql_to_file($file, $table);
          }
        }
      }
      $file->write($this->_get_sql_file_footer());
      $file->close();
      return $lines;
    }
    else {
      return FALSE;
    }
  }

  /**
   * Backup the databases to a file.
   */
  function _restore_db_from_file($file, $settings) {
    $num = 0;

    if ($file->open() && $conn = $this->_get_db_connection()) {
      // Read one line at a time and run the query.
      while ($line = $file->read()) {
        if (_backup_migrate_check_timeout()) {
          return FALSE;
        }
  
        $line = trim($line);
        if ($line) {
          // Prepeare and exexute the statement instead of the api function to avoid substitution of '{' etc.
          $stmt = $conn->prepare($line);
          $stmt->execute();
          $num++;
        }
      }
      // Close the file with fclose/gzclose.
      $file->close();
    }
    else {
      drupal_set_message(t("Unable to open file %file to restore database", array("%file" => $file->filepath())), 'error');
      $num = FALSE;
    }
    return $num;
  }

  /**
   * Get a list of tables in the database.
   */
  function get_table_names() {
    $out = array();
    foreach ($this->_get_tables() as $table) {
      $out[$table['name']] = $table['name'];
    }
    return $out;
  }

  /**
   * Lock the list of given tables in the database.
   */
  function _lock_tables($tables) {
    if ($tables) {
      $tables_escaped = array();
      foreach ($tables as $table) {
        $tables_escaped[] = '`'. db_escape_table($table) .'`  WRITE';
      }
      $this->query('LOCK TABLES '. implode(', ', $tables_escaped));
    }
  }

  /**
   * Unlock all tables in the database.
   */
  function _unlock_tables($settings) {
    $this->query('UNLOCK TABLES');
  }

  /**
   * Get a list of tables in the db.
   */
  function _get_tables() {
    $out = array();
    // get auto_increment values and names of all tables
    $tables = $this->query("show table status", array(), array('fetch' => PDO::FETCH_ASSOC));
    foreach ($tables as $table) {
      $out[$table['name']] = $table;
    }
    return $out;
  }

  /**
   * Get the sql for the structure of the given table.
   */
  function _get_table_structure_sql($table) {
    $out = "";
    $result = $this->query("SHOW CREATE TABLE `". $table['name'] ."`", array(), array('fetch' => PDO::FETCH_ASSOC));
    foreach ($result as $create) {
      $out .= "DROP TABLE IF EXISTS `". $table['name'] ."`;\n";
      // Remove newlines and convert " to ` because PDO seems to convert those for some reason.
      $out .= strtr($create['create table'], array("\n" => ' ', '"' => '`'));
      if ($table['auto_increment']) {
        $out .= " AUTO_INCREMENT=". $table['auto_increment'];
      }
      $out .= ";\n";
    }
    return $out;
  }
  
  /**
   *  Get the sql to insert the data for a given table
   */
  function _dump_table_data_sql_to_file($file, $table) {
    $rows_per_line = variable_get('backup_migrate_data_rows_per_line', 30);
    $bytes_per_line = variable_get('backup_migrate_data_bytes_per_line', 2000);
  
    $lines = 0;
    $data = $this->query("SELECT * FROM `". $table['name'] ."`", array(), array('fetch' => PDO::FETCH_ASSOC));
    $rows = $bytes = 0;

    // Escape backslashes, PHP code, special chars
    $search = array('\\', "'", "\x00", "\x0a", "\x0d", "\x1a");
    $replace = array('\\\\', "''", '\0', '\n', '\r', '\Z');
  
    $line = array();
    foreach ($data as $row) {
      // DB Escape the values.
      $items = array();
      foreach ($row as $key => $value) {
        $items[] = is_null($value) ? "null" : "'". str_replace($search, $replace, $value) ."'";
      }
  
      // If there is a row to be added.
      if ($items) {
        // Start a new line if we need to.
        if ($rows == 0) {
          $file->write("INSERT INTO `". $table['name'] ."` VALUES ");
          $bytes = $rows = 0;
        }
        // Otherwise add a comma to end the previous entry.
        else {
          $file->write(",");
        }
  
        // Write the data itself.
        $sql = implode(',', $items);
        $file->write('('. $sql .')');
        $bytes += strlen($sql);
        $rows++;
  
        // Finish the last line if we've added enough items
        if ($rows >= $rows_per_line || $bytes >= $bytes_per_line) {
          $file->write(";\n");
          $lines++;
          $bytes = $rows = 0;
        }
      }
    }
    // Finish any unfinished insert statements.
    if ($rows > 0) {
      $file->write(";\n");
      $lines++;
    }
  
    return $lines;
  }

  /**
   * Run a db query on this destination's db.
   */
  function query($query, $args = array(), $options = array()) {
    if ($conn = $this->_get_db_connection()) {
      return $conn->query($query, $args, $options);
    }
  }

  /**
   * The header for the top of the sql dump file. These commands set the connection
   *  character encoding to help prevent encoding conversion issues.
   */
  function _get_sql_file_header() {
    return "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE=NO_AUTO_VALUE_ON_ZERO */;

SET NAMES utf8;

";
  }
  
  /**
   * The footer of the sql dump file.
   */
  function _get_sql_file_footer() {
    return "
  
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
";
  }
}


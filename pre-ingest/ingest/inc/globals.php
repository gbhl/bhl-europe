<?php
// ********************************************
// ** FILE:    GLOBALS.PHP                   **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    10.02.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

define("_APP_VERSION",       "1.75 beta (serials 1.2)");

define("_APP_NAME",          "BHL-E Content Portal & Ingestion");

// GLOBALS REPLACEMENT
define("_USR_ABS",          _ABS."img/");            // because we have no user dirs
define("_USR_URL",          _HOME."img/");           // used for images


define("_SHARED",           _ABS."shared/");
define("_SHARED_URL",       _HOME."shared/");


define("_SYSTEM",           _HOME."index.php");


define("_TRENNER",          "|");

define("_QUEUE_PREFIX",     "content_queue_");
define("_QUEUE_SUFFIX",     ".sh");


include_once(_ABS."shared/common_globals.php");

        
?>

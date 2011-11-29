<?php
// ********************************************
// ** FILE:    GLOBALS.PHP                   **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

define("_APP_VERSION",       "0.4beta 3");

define("_APP_NAME",          "BHL-E Content Portal & Ingestion");

// GLOBALS REPLACEMENT
define("_USR_ABS",          _ABS."img/");            // because we have no user dirs
define("_USR_URL",          _HOME."img/");           // used for images


define("_SHARED",           _ABS."shared/");
define("_SHARED_URL",       _HOME."shared/");


define("_SYSTEM",           _HOME."index.php");


define("_TRENNER",          "|");


include_once(_ABS."shared/common_globals.php");


   
        
?>

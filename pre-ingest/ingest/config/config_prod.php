<?php
// ***************************************
// ** FILE:    CONFIG.PHP               **
// ** PURPOSE: CUSTOM CONFIGURATION     **
// ** DATE:    21.11.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************


// ABSOLUTE PATH TO THE INGEST APPLICATION ROOT (WHERE INGEST.PHP RESIDE!)
define("_ABS",                      str_replace("//","/",dirname($_SERVER['SCRIPT_FILENAME'])."/"));

// URL TO THE INGEST APPLICATION (bhl-int.nhm.ac.uk)
define("_HOME",                     "http://".$_SERVER['HTTP_HOST']."/preingest/");

// TECHNICAL CONTACT
define("_CONTACT_EMAIL",            "heimo.rainer@nhm-wien.ac.at");

// WORKING DIRECTORY
// IF NECESSARY MAKE IT TO A LINK TO ANOTHER FILESYSTEM FOLDER INSTEAD OF CHANGING THIS
// SPECIFY A DIRECTORY OUTSIDE THE APP DEPLOYMENT
// DIRECTORY SO THIS APP DIRECTORY STRUCTURE CAN
// BE DROPPED BY AN UPGRADE

define("_WORK_DIR",                 _ABS."temp/");
define("_CONTENT_ROOT",             "/mnt/nfs/upload/providers/");

// REVERSE LOOKUP URL MUST POINT TO THE _CONTENT_ROOT AND END WITH /
// USED 4 CLIENT SIDE ACCESS TO CONTENT AND .AIP FILES
//                                  "http://bhl-celsus.nhm.ac.uk/uploads/");   
define("_REVERSE_LOOKUP_URL",       "http://bhl-int.nhm.ac.uk/preingest/assets/");

// GUI (ms)
define("_GUI_REFRESH",		    "120000");


define("_AIP_DIR",		    ".aip");
define("_AIP_OLEF_FN",		    "olef.xml");
define("_SELF_ADMIN",               false);        // SELF ADMIN OF CP SETTINGS ON|OFF

define("_THUMB_FN",                 "thumb.png");
define("_THUMB_SIZE",               "45x60");
define("_THUMB_BGRD",               "thumb_frame_55_70.png");


// GENERAL WEBSERVICE PARAMETER
define("_WEBSERVICE_TIMEOUT",        10);


// DATABASE DETAILS
define("_DB_SERVER",                "bhl-db1");     
define("_DB_USER",                  "${DB_USERNAME}");     
define("_DB_PWD",                   "${DB_PASSWORD}");
define("_DB_MAIN",                  "int_pi_pi");     
define("_DB_DEBUG",                 true);


// --------------------
// ---- FILE TYPES ----
// --------------------
define("_PAGEDATA_EXT",             ".tif,.tiff,.jpeg,.jpg,.ppm,.gif,.TIF,.TIFF,.JPEG,.JPG,.PPM,.GIF");
define("_BOOKDATA_EXT",             ".pdf,.PDF");
define("_METADATA_EXT",             ".xml,.marc,.mrc,.XML,.MARC,.MRC");
define("_OCRDATA_EXT",              ".txt,.ocr,.TXT,.OCR");
define("_TAXON_EXT",                ".tax");        // DEFINE HERE ONLY 1 EXTENSION

define("_ANALYZE_MAX_DEPTH",        20);            // MAX DIRECTORY STRUCTURE DEPTH


// --------------
// ---- NOID ----
// --------------
define("_NOID",                     "/usr/local/bin/noid -f /var/www/nd/kt5/ mint 1");
define("_NOID_PREFIX",              "10706/a0");


// -----------------------------
// ---- SCHEMA MAPPING TOOL ----
// -----------------------------
define("_JAVA_BIN",                 "/mnt/nfs/dev/jdk1.6.0_24/bin/java");  // C:/Program Files/Java/jre7/bin/
define("_SCHEMA_MAPPER",            "/opt/pre-ingest/smt-cli/SMT-cli.jar");
define("_SMT",                      _JAVA_BIN." -jar "._SCHEMA_MAPPER." ");


// -------------------------------------
// ---- IMAGE MANIPULATION SPECIFIC ----
// -------------------------------------
define("_IMG_MAGICK_CONVERT",       "/usr/bin/convert");
define("_IMG_BITPERPIXEL",          8);

define("_IMG_ORI_WIDTH",            2048);
define("_IMG_ORI_HEIGHT",           2048);

define("_IMG_CONVERT_PARAMS",       "");  // -compress lzw 


// -----------------
// ---- QUEUING ----
// -----------------
define("_QUEUE_RUNDIR",             _ABS."queuing/running/");
define("_QUEUE_ARCHIVE",            _ABS."queuing/archive/");
define("_LOG",                      _ABS."log/");


// ----------------------------
// ---- PDF 2 TXT SPECIFIC ----
// ----------------------------
define("_XPDF_ABS",                 "/usr/bin/");
define("_PDFTOTEXT",                _XPDF_ABS."pdftotext -nopgbrk -eol unix -f FFFF -l LLLL \"SSSS\" \"OOOO\"");
define("_PDFTOPPM",                 _XPDF_ABS."pdftoppm -r 80 ");


// ----------------------
// ---- OCR SPECIFIC ----
// ----------------------
define("_OCR_ABS",                  "/usr/local/bin/");
define("_OCR_DAT",                  _OCR_ABS."tessdata/");
define("_TESSERACT",                _OCR_ABS."tesseract");
define("_OCR_TIMEOUT",              20);


// -------------------------------
// ---- TAXON SPECIFIC (UBIO) ----
// -------------------------------
define("_TAXON_FINDER",             "http://www.ubio.org/webservices/service.php?function=taxonFinder&includeLinks=0&url=");
define("_TAXON_FINDIT",             "http://www.ubio.org/webservices/service.php?function=findIT&strict=1&url=");
define("_TAXON_TESTFILE",           "http://www.ubio.org/tools/Small.txt");


// -------------------------
// ---- FEDORA SPECIFIC ----
// -------------------------
define ("_FEDORA_ADMIN_GUI",        "http://bhl-int.nhm.ac.uk/fedora/admin/#"); // http://bhl-int.nhm.ac.uk/fedora/

// USED TO INVOKE INGEST OVER WEBSERVICE
// INVOKATION ONLY IF NOT EMPTY
// INGEST STATUS FILES ARE WRITTEN NEVERTHELESS
define ("_FEDORA_INGEST_URI",       "");

define ("_FEDORA_CF_READY",         "ingest_ready.txt");

define ("_FEDORA_CF_FINISHED",      "ingest_done.txt");     // GENERATED BY BATCH INGEST

define ("_FEDORA_CF_RUNNING",       "ingest_running.txt");  // GENERATED BY BATCH INGEST

?>
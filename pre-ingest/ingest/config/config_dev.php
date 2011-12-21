<?php
// ***************************************
// ** FILE:    CONFIG.PHP               **
// ** PURPOSE: CUSTOM CONFIGURATION     **
// ** DATE:    21.11.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************


// ABSOLUTE PATH TO THE INGEST APPLICATION ROOT (WHERE INGEST.PHP RESIDE!)
define("_ABS",                      str_replace("//","/",$_SERVER['DOCUMENT_ROOT']."/"));

// URL TO THE INGEST APPLICATION (bhl-int.nhm.ac.uk)
define("_HOME",                     "http://".$_SERVER['HTTP_HOST']."/");

define("_UPLOADS_HOME",             "http://bhl-celsus.nhm.ac.uk/uploads/");

// TECHNICAL CONTACT
define("_CONTACT_EMAIL",            "heimo.rainer@nhm-wien.ac.at");

// WORKING DIRECTORY
// IF NECESSARY MAKE IT TO A LINK TO ANOTHER FILESYSTEM FOLDER INSTEAD OF CHANGING THIS
// SPECIFY A DIRECTORY OUTSIDE THE APP DEPLOYMENT
// DIRECTORY SO THIS APP DIRECTORY STRUCTURE CAN
// BE DROPPED BY AN UPGRADE

define("_WORK_DIR",                 _ABS."temp/");
define("_CONTENT_ROOT",             "G:/dev/nhm/bhl/ingest/testdata/");
define("_REVERSE_LOOKUP_URL",       "http://bhl-celsus.nhm.ac.uk/uploads/");
define("_AIP_DIR",		    ".aip");
define("_AIP_OLEF_FN",		    "olef.xml");
define("_SELF_ADMIN",               false);        // SELF ADMIN OF CP SETTINGS ON|OFF



// DATABASE DETAILS
define("_DB_SERVER",                "127.0.0.1");     
define("_DB_USER",                  "root");     
define("_DB_PWD",                   "root");
define("_DB_MAIN",                  "int_pi_pi");     
define("_DB_DEBUG",                 true);



// CONTENT SPECIFIC
// --------------------
// ---- FILE TYPES ----
// --------------------
define("_PAGEDATA_EXT",             ".tif,.tiff,.jpeg,.jpg,.ppm,.gif,.TIF,.TIFF,.JPEG,.JPG,.PPM,.GIF");
define("_BOOKDATA_EXT",             ".pdf,.PDF");
define("_METADATA_EXT",             ".xml,.marc,.XML,.MARC");
define("_OCRDATA_EXT",              ".txt,.ocr,.TXT,.OCR");
define("_ANALYZE_MAX_DEPTH",        20);            // MAX DIRECTORY STRUCTURE DEPTH


// NOID
// ---------------------
define("_NOID",                     "G:/dev/nhm/bhl/ingest/bin/noid/noid -f G:/dev/nhm/bhl/ingest/bin/noid/noid/ mint 1");
define("_NOID_PREFIX",              "10706/a0");


// -----------------------------
// ---- SCHEMA MAPPING TOOL ----
// -----------------------------
define("_JAVA_BIN",                 "java.exe");
define("_SCHEMA_MAPPER",            "G:/dev/nhm/bhl/ingest/bin/smt/SMT-cli.jar");
define("_SMT",                      _JAVA_BIN." -jar "._SCHEMA_MAPPER." ");


// -------------------------------------
// ---- IMAGE MANIPULATION SPECIFIC ----
// -------------------------------------
define("_IMG_MAGICK_CONVERT",       "G:/dev/nhm/bhl/ingest/bin/imagemagick/convert.exe");
define("_IMG_BITPERPIXEL",          8);

define("_IMG_ORI_WIDTH",            2048);
define("_IMG_ORI_HEIGHT",           2048);

define("_IMG_CONVERT_PARAMS",       "");


// ----------------------------
// ---- PDF 2 TXT SPECIFIC ----
// ----------------------------
define("_XPDF_ABS",                 "G:/dev/nhm/bhl/ingest/bin/xpdf/bin32/");
define("_PDFTOTEXT",                _XPDF_ABS."pdftotext.exe");
define("_PDFTOPPM",                 _XPDF_ABS."pdftoppm.exe");


// ----------------------
// ---- OCR SPECIFIC ----
// ----------------------
define("_OCR_ABS",                  "G:/dev/nhm/bhl/ingest/bin/tesseract/");
define("_OCR_DAT",                  _OCR_ABS."tessdata/");
define("_TESSERACT",                _OCR_ABS."tesseract.exe");

define("_OCR_TIMEOUT",              120);


// -------------------------------
// ---- TAXON SPECIFIC (UBIO) ----
// -------------------------------
define("_TAXON_FINDER",             "http://www.ubio.org/webservices/service.php?function=taxonFinder&includeLinks=0&url=");
define("_TAXON_FINDIT",             "http://www.ubio.org/webservices/service.php?function=findIT&strict=1&url=");
define("_TAXON_TESTFILE",           "http://www.ubio.org/tools/Small.txt");

define("_TAXON_WEB_TIMEOUT",        19);


?>
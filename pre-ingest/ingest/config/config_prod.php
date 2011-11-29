<?php
// ***************************************
// ** FILE:    CONFIG.PHP               **
// ** PURPOSE: CUSTOM CONFIGURATION     **
// ** DATE:    21.11.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************

// -----------------------
// ---- CUSTOM CONFIG ----
// -----------------------

// ABSOLUTE PATH TO THE INGEST APPLICATION ROOT (WHERE INGEST.PHP RESIDE!)
define("_ABS",                      str_replace("//","/",$_SERVER['DOCUMENT_ROOT']."/ingest/"));

// URL TO THE INGEST APPLICATION
define("_HOME",                     "http://".$_SERVER['HTTP_HOST']."/ingest/");

// TECHNICAL CONTACT
define("_CONTACT_EMAIL",            "heimo.rainer@nhm-wien.ac.at");

// WORKING DIRECTORY
// IF NECESSARY MAKE IT TO A LINK TO ANOTHER FILESYSTEM FOLDER INSTEAD OF CHANGING THIS
// SPECIFY A DIRECTORY OUTSIDE THE APP DEPLOYMENT
// DIRECTORY SO THIS APP DIRECTORY STRUCTURE CAN
// BE DROPPED BY AN UPGRADE

define("_WORK_DIR",                 _ABS."temp/");
define("_CONTENT_ROOT",             "/mnt/nfs/upload/providers/");   // /mnt/upload/providers/
define("_AIP_DIR",					".aip");
define("_AIP_OLEF_FN",				"olef.xml");


// DATABASE DETAILS
define("_DB_SERVER",                "bhl-db1.nhm.ac.uk");     
define("_DB_USER",                  "bhletech");     
define("_DB_PWD",                   "bh!3t3ch");     
define("_DB_MAIN",                  "int_pi_pi");     
define("_DB_DEBUG",                 true);



// MAX DIRECTORY STRUCTURE DEPTH
define("_ANALYZE_MAX_DEPTH",        "10");


// --------------------
// ---- FILE TYPES ----
// --------------------
define("_PAGEDATA_EXT",             ".tif,.tiff,.jpeg,.jpg,.ppm,.gif,.TIF,.TIFF,.JPEG,.JPG,.PPM,.GIF");
define("_BOOKDATA_EXT",             ".pdf,.PDF");
define("_METADATA_EXT",             ".xml,.marc,.XML,.MARC");
define("_OCRDATA_EXT",              ".txt,.ocr,.TXT,.OCR");


// -----------------------------
// ---- SCHEMA MAPPING TOOL ----
// -----------------------------
define("_JAVA_BIN",                 "/mnt/nfs-demeter/dev/jdk1.6.0_24/bin/java");
define("_SCHEMA_MAPPER",            "/opt/pre-ingest/smt-cli/SMT-cli.jar");
define("_SMT",                      _JAVA_BIN." -jar '"._SCHEMA_MAPPER."'");


// -------------------------------------
// ---- IMAGE MANIPULATION SPECIFIC ----
// -------------------------------------
define("_IMG_MAGICK_CONVERT",       "/usr/bin/convert");
define("_IMG_BITPERPIXEL",          8);

define("_IMG_ORI_WIDTH",            2048);
define("_IMG_ORI_HEIGHT",           2048);

define("_IMG_CONVERT_PARAMS",       "");


// ----------------------------
// ---- PDF 2 TXT SPECIFIC ----
// ----------------------------
define("_XPDF_ABS",                 "/usr/bin/");
define("_PDFTOTEXT",                _XPDF_ABS."pdftotext");
define("_PDFTOPPM",                 _XPDF_ABS."pdftoppm");


// ----------------------
// ---- OCR SPECIFIC ----
// ----------------------
define("_OCR_ABS",                  "/usr/local/bin/");
define("_OCR_DAT",                  "/usr/local/share/tessdata/");
define("_TESSERACT",                _OCR_ABS."tesseract");


// -------------------------------
// ---- TAXON SPECIFIC (UBIO) ----
// -------------------------------
define("_TAXON_FINDER",             "http://www.ubio.org/webservices/service.php?function=taxonFinder&includeLinks=0&url=");
define("_TAXON_FINDIT",             "http://www.ubio.org/webservices/service.php?function=findIT&strict=1&url=")

        
?>
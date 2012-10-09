<?php
// ***************************************
// ** FILE:    CONFIG.PHP               **
// ** PURPOSE: CUSTOM CONFIGURATION     **
// ** DATE:    21.11.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ** AUTHOR:  WOLFGANG KOLLER          **
// ***************************************

// RUNTIME MODE (development|production)
define("_MODE",                     "production");

// ABSOLUTE PATH TO THE INGEST APPLICATION ROOT (WHERE index.php RESIDE!)
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
define("_AIP_GUID_FN",              "aip.guid");
define("_SELF_ADMIN",               false);        // SELF ADMIN OF CP SETTINGS ON|OFF

define("_THUMB_FN",                 "thumb.png");
define("_THUMB_SIZE",               "45x60");
define("_THUMB_BGRD",               "thumb_frame_55_70.png");


// MAX LENGTH OF SELECTION LISTS PER PAGE (E.G. CONTENT ANALYZER LIST LENGTH)
define("_MAX_LISTLEN",              "2000");


// GENERAL WEBSERVICE PARAMETER
define("_WEBSERVICE_TIMEOUT",        25);


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

define("_ANALYZE_MAX_DEPTH",        6);            // MAX DIRECTORY STRUCTURE DEPTH


// --------------
// ---- NOID ----
// --------------
define("_NOID",                     "/usr/local/bin/noid -f /var/www/nd/kt5/ mint 1");
define("_NOID_PREFIX",              "10706/a0");
define("_MINTER_WS",                "http://bhl-mandible.nhm.ac.uk:3000/mint");


// -----------------------------
// ---- SCHEMA MAPPING TOOL ----
// -----------------------------
define("_JAVA_BIN",                 "/mnt/nfs/dev/jdk1.6.0_24/bin/java");
define("_SCHEMA_MAPPER",            realpath(_ABS."../schema-mapping-tool/cli/dist/smt-cli.jar"));
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
define("_PDFTOPPM",                 _XPDF_ABS."pdftoppm -r 300 ");
define("_PDFINFO",                  _XPDF_ABS.'pdfinfo');
define("_PDFFONTS",                 _XPDF_ABS.'pdffonts');

/**
 * PDFTK Paths & Parameters 
 */
define("_PDFTK",                    '/usr/bin/pdftk');
define("_PDFTK_DATA",               'dump_data');

// ----------------------
// ---- OCR SPECIFIC ----
// ----------------------
define("_OCR_ABS",                  "/usr/local/bin/");     // /usr/local/share/tessdata/
define("_OCR_DAT",                  "/usr/local/share/tessdata/");
define("_TESSERACT",                _OCR_ABS."tesseract");
define("_OCR_TIMEOUT",              25);


// -------------------------------
// ---- TAXON SPECIFIC (UBIO) ----
// -------------------------------
define("_TAXON_FINDER",             "http://www.ubio.org/webservices/service.php?function=taxonFinder&includeLinks=0&url=");
define("_TAXON_FINDIT",             "http://www.ubio.org/webservices/service.php?function=findIT&strict=1&url=");
define("_TAXON_TESTFILE",           "http://www.ubio.org/tools/Small.txt");


// -----------------------------
// ---- METS / OLEF SPECIFIC ---
// -----------------------------
define ("_DEFAULT_PAGETYPE",        "PAGE");


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


// ---
// IPR
// ---
$arrIPR = array(
"http://creativecommons.org/publicdomain/mark/1.0/" =>   "Public Domain Mark 1.0",
"http://creativecommons.org/publicdomain/zero/1.0/"=>    "Public Domain Dedication",
"http://creativecommons.org/licenses/by/3.0/"=>          "Attribution 3.0 Unported (CC BY 3.0)",
"http://creativecommons.org/licenses/by-sa/3.0/"=>       "Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)",
"http://creativecommons.org/licenses/by-nc/3.0"=>        "Attribution-NonCommercial 3.0 Unported (CC BY-NC 3.0)",
"http://creativecommons.org/licenses/by-nc-sa/3.0/"=>    "Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0)",
"http://creativecommons.org/licenses/by-nd/3.0/"=>       "Attribution-NoDerivs 3.0 Unported (CC BY-ND 3.0)",
"http://creativecommons.org/licenses/by-nc-nd/3.0/"=>    "Attribution-NonCommercial-NoDerivs 3.0 Unported (CC BY-NC-ND 3.0)",
"http://www.europeana.eu/rights/rr-f/"=>                 "Europeana: Rights Reserved - Free Access", 
"http://www.europeana.eu/rights/rr-p/"=>                 "Europeana: Rights Reserved - Paid Access",
"http://www.europeana.eu/rights/rr-r/"=>                 "Europeana: Rights Reserved - Restricted Access",
"http://www.europeana.eu/rights/unknown/"=>              "Europeana: Unknown copyright status"
);

define ("_DEFAULT_IPR",               0);                // REFERENCES TO THE LIST ABOVE (0...N)



// ----------------------
// --- MESSAGE BROKER ---
// ----------------------
/*  There are three ActiveMQ serving different environment:
    tcp://bhl-mandible.nhm.ac.uk:61613 for integration
    tcp://bhl-mandible.nhm.ac.uk:61614 for production
    tcp://bhl-mandible.nhm.ac.uk:61615 for test
*/
define ("_MB_ABS",                   _ABS."stomp-client/");
define ("_MB_URL",                   "tcp://bhl-mandible.nhm.ac.uk:61613");

// -----------------------------
// --- Namespace definitions ---
// -----------------------------

define( '_NAMESPACE_MODS',             'http://www.loc.gov/mods/v3' );
define( '_NAMESPACE_DC',               'http://purl.org/dc/elements/1.1/' );
define( '_NAMESPACE_METS',             'http://www.loc.gov/METS/' );
define( '_NAMESPACE_RDF_SYNTAX',       'http://www.w3.org/1999/02/22-rdf-syntax-ns#' );
define( '_NAMESPACE_FEDORA_RELATIONS', 'info:fedora/fedora-system:def/relations-external#');
define( '_NAMESPACE_XLINK',            'http://www.w3.org/1999/xlink');
define( '_NAMESPACE_DWC',              'http://rs.tdwg.org/dwc/terms/');
define( '_NAMESPACE_OLEF',             'http://www.bhl-europe.eu/bhl-schema/v1/' );

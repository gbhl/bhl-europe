<?php
// ***************************************
// ** FILE:    COMMON_GLOBALS.PHP       **
// ** PURPOSE: GLOBAL VALUES            **
// ** DATE:    08.05.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************

// http://at2.php.net/reserved.variables
// http://php.net/manual/de/language.constants.predefined.php


// ************************************
// *** REGISTRIERTE FEHLERMELDUNGEN ***
// ************************************
define("_ERR",				"\n<br><b>Error:</b> ");

define("_DB_ERROR_CONNECT",		_ERR."Could not connect to database server.");
define("_DB_ERROR_SELECTDB",            _ERR."Could not select specified database.");
define("_DB_ERROR_QUERY",		_ERR."Could not execute database query correctly.");
define("_DB_PARSING",			_ERR."Could not parse sql correctly.");
define("_WRONG_USERID",			_ERR."Wrong System Call, user ID may be wrong.");
define("_SESSION_FAILED",		_ERR."Could not establish the session correctly, may Cookies were not accepted.");
define("_LOGIN_FAILED",			_ERR."Login data not accepted !\\n\\nPlease take care of upper/lowercase letters (CAPS lock).");
define("_WRONG_REQUEST",		_ERR."Wrong request, required information is not available. Access details logged!");
define("_ERR_COOKIE",			_ERR."Cannot set a cookie, please check your browser configuration.");
define("_ERR_ORA_LOGIN",		_ERR."Because of missing login data the database could not be connected.");
define("_ERR_ORA_ENV",                  _ERR."The Oracle Environment is not valid. (ORACLE_HOME, etc.)");
define("_DEPRECATED",                   _ERR."This function is deprecated, ask your support.");


// ***********************************
// *** REGISTER_GLOBALS BEHANDLUNG ***
// ***********************************
if (!get_cfg_var("register_globals"))
{
 extract($_REQUEST);
 extract($_COOKIE);
 if ((isset($action)) and ($action=="upload"))
 {
 	extract($_FILES);
 	if( !isset( $HTTP_POST_FILES ) )	$HTTP_POST_FILES =& $_FILES;
 }
}


// *** COMMAND LINE INVOKE ***
if (PHP_SAPI == "cli")   define("_CLI_EXECUTION", true);
else                     define("_CLI_EXECUTION", false);



// *** INITIALISIERUNGEN ***

if (!isset($action))	$action = NULL;
if (!isset($user))   	$user   = NULL;
if (!isset($button))  	$button = NULL;
if (!isset($login))   	$login  = NULL;
if (!isset($endmsg))  	$endmsg = NULL;




// *** SYSTEMSPRACHE (AKTUELL MAX 2 SPRACHENUNTERSTUETZUNG AM BACKEND)

if (defined("_INT_NAME")&&(_INT_NAME=="cms")) {
    if ( (!isset($lang))||(!is_numeric($lang))||($lang<1)||($lang>4) )
    $lang = 1;
}
else {
    if ( (!isset($lang))||( ($lang != 1)&&($lang != 2) ) )
    $lang = 1;
}



// *** BEGINN ERZEUGUNG GLOBALER VARIABLEN ***

define("_ACTIVE_SCRIPT",	$_SERVER["SCRIPT_NAME"]);

if (array_key_exists("REQUEST_URI",$_SERVER)) define("_REQUEST_URI",$_SERVER['REQUEST_URI']);
else                                          define("_REQUEST_URI","");

if      (getenv("HOSTNAME")!="")      { define("_HOSTNAME", getenv("HOSTNAME")); }
else if (getenv("COMPUTERNAME")!="")  { define("_HOSTNAME", getenv("COMPUTERNAME")); }
else if (getenv("SERVER_NAME")!="")   { define("_HOSTNAME", getenv("SERVER_NAME")); }


// ********************
// *** ZEIT / DATUM ***
// ********************

define("_NOW",				date("Y-m-d H:i:s"));

// UNTERSCHIED SERVERZEIT - LOKALER(APPLIKATIONS)ZEIT
define("_SERVERTIME_2_LOCALTIME_OFFSET",	0);


// UNTERSCHIED AKTUELLER SERVERZEIT ZU UTC (UNIVERSAL TIME, COORDINATED) IN STUNDEN
// BEEINFLUSST DURCH SOMMER/WINTERZEIT ETC. - MITTLES ZEITZONE ERMITTELT

$utc_offset = ((int) date("Z"))/60/60;      // UTC+1 (Vienna) Wintertime, UTC+2 (Vienna) Sommertime

define("_SERVERTIME_2_UTC_OFFSET",          $utc_offset);           // ABWEICHUNG ZU UTC IN STUNDEN
unset($utc_offset);




// *****************************
// *** REMOTE CLIENT BROWSER ***
// *****************************
if (array_key_exists("HTTP_USER_AGENT",$_SERVER)) define("_BROWSER", $_SERVER["HTTP_USER_AGENT"]);
else                                              define("_BROWSER", "");

// *** REMOTE CLIENT RESOLUTION ***
// IN ENTRY POINT SCRIPT(S) PASTE:
// SetCookie('resx',window.screen.width,1);   // EXPIRES IMMEDIATELY
// SetCookie('resy',window.screen.height,1);

if (isset($_COOKIE["resx"]))    $resx    = explode(";",$_COOKIE["resx"]);    else $resx[0]=1024;
if (isset($_COOKIE["resy"]))    $resy    = explode(";",$_COOKIE["resy"]);    else $resy[0]=768;
if (isset($_COOKIE["browser"])) $browser = explode(";",$_COOKIE["browser"]); else $browser[0] = 'ie';


define("_CLIENT_RESOLUTION_X",		(int) $resx[0]);	unset($resx);
define("_CLIENT_RESOLUTION_Y",		(int) $resy[0]);	unset($resy);
define("_CLIENT_BROWSER",           $browser[0]);	    unset($browser);



// *****************************
// *** CLIENT INFO DETECTION ***
// *****************************
include("common_client_info.php");



// ****************************************
// *** APPLICATION SERVER CONFIGURATION ***
// ****************************************

if (array_key_exists("SERVER_ADDR",$_SERVER))  define("_SERVER",$_SERVER["SERVER_ADDR"]);
else                                           define("_SERVER","");

if (defined("_DOC_ROOT"))                      define("_WIN_DRV",substr(_DOC_ROOT,0,2));   // => z.B. C:


$development = false;

// ***********************************
// *** SERVERSPEZIFISCHE PARAMETER ***
// ***********************************
// ALSO USEABLE: echo __DIR__;
switch (trim(_SERVER))
{
    // ES SOLLTE NICHTS PER LOCALHOST AUGERUFEN WERDEN 127.0.0.1
    case '':              // CLI & PROD
    case '10.1.84.9':     
        if (file_exists(dirname(__FILE__)."/common_ta_vm007.php")) 
        include_once("common_ta_vm007.php");           
    break;
    case '10.1.48.161':   
        if (file_exists(dirname(__FILE__)."/common_ta_p1apexdb01.php")) 
        include_once("common_ta_p1apexdb01.php"); 
    break;  
//  case '10.1.50.215':   
//      if (file_exists(dirname(__FILE__)."/common_ta_wrzsora1.php")) 
//      include_once("common_ta_wrzsora1.php");  
//  break; 
    default:              
        if (file_exists(dirname(__FILE__)."/common_ta_lokal.php")) 
        include_once("common_ta_lokal.php");           
    break;
}



// *********************************
// *** WEITERE ABGELEITETE PFADE ***
// *********************************
if (defined("_INT_NAME"))   define("_HOME_ABS",		_ABS."app/"._INT_NAME."/");
if (defined("_INT_NAME"))   define("_HOME_URL",		_HOME."app/"._INT_NAME."/");

if (!defined("_SHARED"))    define("_SHARED",		_ABS."app/shared/");
if (!defined("_SHARED_URL"))define("_SHARED_URL",	_HOME."app/shared/");
if (!defined("_SHARED_IMG"))define("_SHARED_IMG",       _SHARED."img/");
if (!defined("_CACHE_URL")) define("_CACHE_URL",	_HOME."cache/");

if (defined("_DOC_ROOT"))   define("_CACHE_DIR",	_DOC_ROOT."cache/");

if (defined("_HOME_ABS"))   define ("_IMG_ABS",     _HOME_ABS."img/");
if (defined("_HOME_URL"))   define ("_IMG_URL",     _HOME_URL."img/");

if (defined("_HOME_ABS"))   define ("_APP_INC",     _HOME_ABS."inc/");
if (defined("_HOME_ABS"))   define ("_SCRIPTS",     _HOME_ABS."scripts/");
if (defined("_HOME_URL"))   define ("_SCRIPTS_URL", _HOME_URL."scripts/");


define ("_DEVELOPMENT", $development); unset($development);


// *******************
// *** TABLE PARTS ***
// *******************
define("_TD",		"</td><td>");
define("_TR",		"</td></tr>\n<tr><td>");
define("_TAB",		"</td></tr></table>\n");
define("_TABLE",	"\n<TABLE BORDER=0 class=noborder><TR><TD valign=top>\n");
define("_TH",		"</th><th>");



// *******************
// *** MISC COMMON ***
// *******************
$arrImageExtensions = array("image","bmp","jpeg","jpg","jpe","gif","png","tif","tiff","mdi","pic","psp","mp4","mpeg","mpg");



$globals_read = true;

?>

<?php
// ***************************************
// ** FILE:    FILE_DOWNLOAD.PHP        **
// ** PURPOSE: SENDET FILE AN BROWSER   **
// ** DATE:    25.07.2008               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************

// MIME Types:

// 	http://community.roxen.com/developers/idocs/rfc/rfc2046.html
// 	http://www.php4-forum.de/http_header_content_type.htm
// 	http://de.selfhtml.org/diverses/mimetypen.htm
// 	http://www.spinnaker.de/mutt/mime.types
//      http://www.iana.org/assignments/media-types/application/


/* needs:    $srcFile........... absoluter path of file to send
optional:    $destFile ......... dateiname beim speichern
             $mode ............. 1 ... send to client, 2 ... download link page

alternative:
$fp = fopen($srcFile, "r");
fpassthru($fp);
fclose($fp);


updates: added ";\r\n" after some headers			13-12-2004
added no_cache_header();


weitere headers:
header("Content-Transfer-Encoding: binary");

*/



// *** GLOBAL GLOBALS ***
if (file_exists($_SERVER["DOCUMENT_ROOT"]."/app/shared/common_globals.php"))
include_once($_SERVER["DOCUMENT_ROOT"]."/app/shared/common_globals.php");



// **********************************
if (!get_cfg_var("register_globals"))
{
    extract($_REQUEST);
}
// **********************************


if (!defined("_BROWSER"))  define("_BROWSER",	$_SERVER["HTTP_USER_AGENT"]);


// $srcFile
if      ( (!isset($srcFile)) and (isset($file_name)) )  $srcFile = $file_name;
else if ( (!isset($srcFile)) and (isset($filename))  )  $srcFile = $filename;

$srcFile    = 	urldecode($srcFile);


// $mode
if      ( (!isset($mode)) or (!is_numeric($mode)) )     $mode=1;


// URL ANSTATT ABSOLUTPFAD MITGEGEBEN - VERSUCH DER UEBERSETZUNG AUF LOKALPFAD
if (substr($srcFile,0,4)=="http")
{
    $srcFile = str_replace(array("http://","https://"),"",$srcFile);
    $srcFile = $_SERVER["DOCUMENT_ROOT"]."/".substr($srcFile,strpos($srcFile,"/")+1);
}

$srcFile = clean_path($srcFile);


// DOWNLOAD STARTEN ($mode=1) bzw. ANBIETEN ($mode 2) NUR FALLS FILE EXISTIERT
if (file_exists($srcFile))
{

    if ($mode==1)
    {
        // $destFile - FILENAME F�R CLIENT
        if (strlen($destFile)<5)		$destFile = basename($srcFile);
        else 					$destFile = urldecode($destFile);

        $ext = strtolower( substr($srcFile, strrpos($srcFile,".")+1, strlen($srcFile )-strrpos($srcFile,".")-1));

        switch( $ext )
        {
            case "exe":  $ctype="application/octet-stream";				break;
            case "dll":  $ctype="application/octet-stream";				break;
            case "bin":  $ctype="application/octet-stream";				break;
            case "com":  $ctype="application/octet-stream";				break;
            case "class":$ctype="application/octet-stream";				break;

            case "chm":  $ctype="application/mshelp";					break;
            case "pdf":  $ctype="application/pdf";						break;
            case "zip":  $ctype="application/zip";						break;
            case "doc":  $ctype="application/msword";					break;
            case "docx": $ctype="application/msword";					break;
            case "dot":  $ctype="application/msword";					break;
            case "xls":  $ctype="application/vnd.ms-excel";				break;		// msexcel oder vnd.ms-excel
            case "xlt":  $ctype="application/vnd.ms-excel";				break;
            case "xlsx": $ctype="application/vnd.ms-excel";			    break;
            case "ppt":  $ctype="application/vnd.ms-powerpoint";	    break;      // mspowerpoint
            case "pps":  $ctype="application/vnd.ms-powerpoint";		break;
            case "ps":   $ctype="application/postscript";				break;
            case "php":	 $ctype="application/x-httpd-php";				break;
            case "rtf":  $ctype="application/rtf";						break;
            case "tar":  $ctype="application/x-tar tar";				break;
            case "rar":	 $ctype="application/x-rar";					break;

            case "htm":  $ctype="text/html";							break;		// $ctype="text/plain; charset=utf-8"
            case "html": $ctype="text/html";							break;
            case "xml":  $ctype="application/xhtml+xml";				break;

            case "txt":  $ctype="text/plain";							break;
            case "csv":  $ctype="text/plain";							break;
            case "css":  $ctype="text/css";								break;
            case "tsv":  $ctype="text/tab-separated-values";			break;

            case "gif":  $ctype="image/gif";							break;
            case "png":  $ctype="image/png";							break;
            case "jpg":  $ctype="image/jpg";							break;

            case "mp3":  $ctype="audio/x-mpeg";							break;
            case "mid":  $ctype="audio/x-midi";							break;

            case "mpg":	$ctype="video/mpeg";							break;
            case "avi":	$ctype="video/x-msvideo";						break;
            case "qt":	$ctype="video/quicktime";						break;
            case "mov":	$ctype="video/quicktime";						break;

            default:    $ctype="application/force-download";
        }


        // *** WRITE HEADERS ***

        // WICHTIG TELEKOM PROXIES KOENNEN NUR 1.0 !
        header('HTTP/1.0 200 OK');          

        // header("Pragma: no-cache");
        header("Pragma: public");

        // *** NO-CACHE HEADERS ***

        header("Cache-Control: no-store, no-cache, must-revalidate");

        header("Cache-Control: private, post-check=0, pre-check=0", false);

        header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");		// immer ge�ndert

        header("Expires: 0");

        // *** UEBERTRAGUNGS HINTS   ***

        header("Content-Type: ".$ctype);

        header("Content-Transfer-Encoding: binary");

        header('Content-Disposition: attachment; filename="'.$destFile.'"');

        header("Content-Length: ".filesize($srcFile));

        ob_clean();

        flush();

        // *** SEND FILE ***

        set_time_limit(0);          // BIG FILE COMPATIBILITY

        @readfile($srcFile);

        exit(0);

    }
    if ($mode==2)
    echo "<html><body><a href=\"".str_replace(_ABS,_HOME,$srcFile)."\">Download Datei hier...</a></body></html>";
}
else
echo "<br>Error: File \"".basename($srcFile)."\" not found ...";


?>
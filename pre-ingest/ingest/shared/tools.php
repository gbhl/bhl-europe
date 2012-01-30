<?php
// ***************************************
// ** FILE:    TOOLS.PHP                **
// ** PURPOSE: DIV. GENERAL TOOLS       **
// ** DATE:    09.05.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************

// REMEMBER VARIOUS PHP PRE-DEFINED FUNCTIONS
// ------------------------------------------
// number_format, str_replace (4array), ...
// function blenk() deprecated: replace with lz(anz,echome," ")

if (!defined("_SHARED"))     define ("_SHARED", dirname(__FILE__)."/");         // SHARED = CUR DIRECTORY

if (!defined("_SHARED_IMG"))
{
    if (is_dir(_SHARED."img/"))     define ("_SHARED_IMG",  _SHARED."img/");    // SHARED_IMG = ROOT IMG
    else if (defined("_IMG_ABS"))   define ("_SHARED_IMG",  _IMG_ABS);          // SHARED_IMG = IMG ABS
    else if (defined("_IMG"))       define ("_SHARED_IMG",  _IMG);              // SHARED_IMG = IMG ABS
    else if (defined("_HOME_ABS"))  define ("_SHARED_IMG",  _HOME_ABS."img/");  // SHARED_IMG = HOME IMG
}


if (is_readable(_SHARED."strings.php"))  include_once(_SHARED."strings.php");
else                                    @include_once("strings.php");



// *************
// *** S0006 ***
// *************
function lz($anz=1,$echome=true,$lzStr="&nbsp;")
{
 if ($echome) echo   str_repeat($lzStr,$anz);
 else         return str_repeat($lzStr,$anz);
}


// *************
// *** S0002 ***
// *************
function nl($anz = 1)
{
 for ($i=0; $i<$anz; $i++) echo "<br>\n";
}



// *************
// *** S0001 ***
// *************
function is_ie()
{
 if (instr(_BROWSER,'compatible; MSIE')) return true;
 else									 return false;
}



// *************************
function css_embed($css_url)
// *************************
{
    echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"".$css_url."\" />\n";
}



// *************
// *** J0004 ***
// *************
function js_command($cmd)
{
 echo "\n<SCRIPT type=\"text/javascript\">\n<!--\n".$cmd."\n// -->\n</SCRIPT>\n";
}



/**
 * PRINT OR RETURN A JAVASCRIPT ALERT COMMAND
 *
 * @param   string  $msg
 * @param   int     $mode  0... PRINT UNDECODED NATIVE JS ALERT
 *                         1... PRINT COMPLETE JS BLOCK
 *                         2... RETURN ONLY ALERT COMMAND
 *                         3... RETURN CLEAN $msg ONLY
  * @return  string
 */
function js_alert($msg,$mode="1")
{
	if ($mode=="") $mode="1";

	if ($msg!="")
	{
		$retVal = str_replace("'","\\'",urldecode($msg));

		if ($mode=="0")         js_command("alert(".$msg.");");
 		else if ($mode=="1") 	js_command("alert(html_entity_decode('".$retVal."'));");
 		else if ($mode=="2")   	return "alert(html_entity_decode('".$retVal."'));";
		else 					return $retVal;
	}
	else
		return false;

	return true;
}



// *************
// *** J0003 ***
// *************
function js_goto($url)
{
 js_command("document.location='".$url."';");
}



// *************
// *** J0003 ***
// *************
function js_embed($js_url)
{
 echo "\n<SCRIPT src=\"".$js_url."\" type=\"text/javascript\"></SCRIPT>\n";
}



// **************
function js_begin()
// **************
{
 echo "\n<SCRIPT type=\"text/javascript\">\n<!--\n\n";
}


// **************
function js_end()
// **************
{
 echo "\n\n// -->\n</SCRIPT>\n";
}



// *************
// *** S0007 ***
// *************
function trh($hoover = "#ffffff", $oldcolor = "#eeeeee")
{
 echo "\n<tr bgColor=\"".$oldcolor."\" onMouseOver=\"this.bgColor='".$hoover."';\" onMouseOut=\"this.bgColor='".$oldcolor."';\">\n";
}



// *************
// *** S0005 ***
// *************
function dummycol($mode = 0)
{
 $dummycol = "<td>&nbsp;</td>";

 if ($mode == 1) $dummycol .= "</tr>\n";

 echo $dummycol;
}



// *************
// mouse pointer
// *************
function hand()  { echo "<a href='#' style='cursor:pointer;'>"; }



// ***********************************************************
// ABSATZ RECHTS
// ***********************************************************
function abs_r($marginTop=-12,$marginBottom=0,$paddingRight=0,$zIndex=1)
{
    if (($marginTop!=0) and ($marginTop==""))     $marginTop = -12;
    if ($marginBottom=="")  $marginBottom= 0;
    if ($paddingRight=="")  $paddingRight= 0;

	echo "<p align=\"right\" style=\"text-align: right; margin-top: ".$marginTop.
	"px; margin-bottom: ".$marginBottom."px; padding-right: ".$paddingRight.
	"px; z-index: ".$zIndex.";\">";
}



// ABSATZ LINKS
// *******************************************
function abs_l($marginTop=-12,$marginBottom=0) {
// *******************************************
    if ($marginTop=="")     $marginTop = -12;
    if ($marginBottom=="")  $marginBottom= 0;
	echo "<p align='left' style='text-align: left;  margin-top: ".$marginTop."px; margin-bottom: ".$marginBottom."px;'>";
}



// ABSATZ BLOCKTEXT
// **********************************************************
function abs_b($width=250,$marginTop=-12,$marginBottom=0) {
// **********************************************************
    if ($marginTop=="")     $marginTop = -12;
    if ($marginBottom=="")  $marginBottom= 0;
	echo "<p align='justify' style='width: ".$width."px; margin-top: ".$marginTop."px; margin-bottom: ".$marginBottom."px;'>";
}



function abs_e() { echo "</p>\n"; }



// ****************************************************
// * !!! NEUE ULTIMATIVE ABSATZ FORM ERSETZT ALLE abs_... *
// ****************************************************
function absatz($marginLeft="", $marginRight="", $marginTop="", $marginBottom="", $width="", $blocktext="")
{
    // JE NACH MARGINS SOLLE RECHTS ODER LINKSBUENDIGER ABSATZ ERZEUGT 
    // WERDEN MIT DEN JEW. MARGINS, BREITE,...
    if ($marginLeft=="") $i=1;
}




function h1_left($str) {
	echo "<h1 style='text-align: left; margin-left: 30px; margin-right: 30px;'>".$str."</h1>\n";   }
function h2_left($str) {
	echo "<h2 style='text-align: left; margin-left: 30px; margin-right: 30px;'>".$str."</h2>\n";   }
function h3_left($str) {
	echo "<h3 style='text-align: left; margin-left: 30px; margin-right: 30px;'>".$str."</h3>\n";   }





// **************************************************
// SCHREIBT ZENTRIERTEN DIALOGTEXT
// Z.B. FUER FEHLERMELDUNGEN, ABMELDUNGSHINWEIS ETC.
// **************************************************
function centerDlg($myText="",$myTitle="",$myIcon = "info2.gif", $bgColor="",$fgColor="",$back_button=true)
{
 if ($bgColor=="")	$bgColor = "#9999CC";
 if ($fgColor=="")	$fgColor = "#EEEEEE";

 if ($myTitle=="")	$myTitle = "Information...";

 $myIcon = _SHARED_IMG."".$myIcon;


 echo "<table width=100% height=90% border=0 style=\"border: none;\"><tr><td valign=middle align=center>\n";

 echo "<table align=center cellspacing=1 cellpadding=10 border=0 style=\"border-right: 2px solid #aaaaaa; border-bottom: 2px solid #aaaaaa; border-left: 2px solid #dddddd; border-top: 2px solid #dddddd;\">\n";

 if ($myTitle != "") 	{

 	echo "<tr><td bgcolor=\"".$bgColor."\" style=\"color: ".$fgColor."; font: 14px arial; font-weight: bold;\">";

 	if (file_exists($myIcon))
 	{
 		if (!function_exists('icon')) include_once(_SHARED."imagelib.php");
 		icon($myIcon);
 	}

 	echo " ".$myTitle."</td></tr>\n";
 }

 if ($myText != "")	echo "<tr><td>".$myText;


 if ($back_button)  { nl(2); go_back(); }

 echo _TAB;

 echo _TAB;
}



// *****************************************************************
function print_icon($image="",$additional_js="",$writeFunction=true)
// *****************************************************************
{
	if ($image=="") $image =  _SHARED_IMG."print.gif";

	if ($writeFunction)
	{
	  js_begin();

?>

// ***************
function printme()
// ***************
{
 document.body.style.backgroundColor='#ffffff';
 document.body.style.color='#000000';

 <?php echo $additional_js; ?>

 window.print();
}

<?php

	  js_end();
	}

	echo "<a name=printpos></a>";
	echo "<a href='#printpos' onClick=\"printme();\">";

	if (!function_exists('icon')) include_once(_SHARED."imagelib.php");

	icon($image);  echo "</a>";
}



// ***********************
function no_cache_header()
// ***********************
{
 header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");						// Datum aus Vergangenheit
 header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");			// immer geaendert
 header("Cache-Control: no-store, no-cache, must-revalidate");			// HTTP/1.1
 header("Cache-Control: post-check=0, pre-check=0", false);				// HTTP/1.1
 header("Pragma: no-cache"); 											// HTTP/1.0
}



// **************************
function echo_pre($myText="")
// **************************
{
	echo "\n<PRE>";

	if (is_array($myText))  print_r($myText);
	else					echo ($myText);

	echo "</PRE>\n";
}



// *************************************
function echo_posted_vars($printit=true)
// *************************************
{
	$retVal = "";

	$post_value = each ($_POST);

	while ($post_value)
	{
	    list ($key, $val) = $post_value;

		$retVal .= "\n\n".$key." => ".$val;
		if ($printit) echo_pre($retVal);

		$post_value = each ($_POST);
	}

	reset($_POST);

	return ($retVal);
}



// ************************************************************************
function go_back($text="zur&uuml;ck...",$myUrl="", $back_img="ar_back.gif")
// ************************************************************************
{
    if (!instr($back_img,"/"))  $back_img = _SHARED_IMG."".$back_img;

    if ($myUrl=="")     	    echo "<a href='#' onClick=\"history.back();\" >";
    else                        echo "<a href='".$myUrl."'>";

    if (file_exists($back_img))	{
        if (!function_exists('icon')) include_once(_SHARED."imagelib.php");
        icon($back_img);
    }

	if ($text!="")              echo " <font style='font: 10px verdana;'>".$text."</font>";

	echo " </a> ";
}



// ******************************************************************************************************************
function logit($title="Info",$desc="",$ts = "", $sep="\t",$row_sep="\n",$send_header=true,$maxlen=120)
// ******************************************************************************************************************
{
	$title .= "                                                                                           ";
	$desc  .= "                                                                                           ";

	if ($send_header)	send_text_header();
	if ($ts=="") 		$ts = date("Y-m-d H:i:s");

	return $ts.$sep.substr($title,0,25).$sep.substr($desc,0,$maxlen).$row_sep;
}



// ************************
function send_text_header()
// ************************
{
 if (!headers_sent())	header("Content-type: text/plain;\r\n");
}



// *****************
function numf($wert)
// *****************
// funktioniert nur mit eingangswerten, die entweder kein dezimalzeichen, oder das richtige
// systemvordef. dezimaltrennzeichen besitzen
// 12312.938 --> 12.312,938
// 1233      --> 1.233
// 1123.     --> 1.123
// .234      --> geht nicht (keine zahl)
// 1233,23   --> geht nicht wenn . systemvordef.  dezimaltrennzeichen ist
{
 $wert = trim($wert);
 $nachkommastellen = 0;

 $len_wert   = strlen($wert);								//											e.g. 9
 $last_index = $len_wert-1;									// $i = index of last letter 				e.g. 8

 while (is_numeric($wert[$last_index]))	$last_index--;		// returns index of last non numeric letter e.g. 5

 if (($last_index>0) and ($last_index != ($len_wert-1)))
 $nachkommastellen = $len_wert-($last_index+1);				// 								e.g. 9 - (5+1) = 3

 return format_number($wert,$nachkommastellen);
}



// ******************************************************************
// *** ZAEHLT bestimmte NUMERISCHE SPALTE IN EINEM ARRAY ZUSAMMEN ***
// *** $col KANN AUCH FELDBEZEICHNER SEIN z.b. "ANZ"              ***
// ******************************************************************
function count_col($arr,$col)
{
 $nrows = count($arr);

 $sum = 0;

 for ($i=0;$i<$nrows;$i++)
 {
  if (is_numeric($arr[$i][$col]))	$sum += $arr[$i][$col];
 }

 return $sum;
}



// *************************************************************
function excel_icon($file_name,$margin_top=20,$margin_right=20)
// *************************************************************
{
    if (file_exists($file_name))
    {
        // EXPORT NACH EXCEL
        echo "<p style='position: absolute; top:".$margin_top."px; right: ".$margin_right."px; text-align: right;'>";
        echo " <a href='"._SHARED_URL."file_download.php?srcFile=".urlencode($file_name)."'>";

        if (!function_exists('icon')) include_once(_SHARED."imagelib.php");

        icon(_SHARED_IMG."excel","Excel Export...");
        echo "</a>";
        abs_e();
    }
}



// ***************************************************************************************************
function download_icon($abs_file_name,$orientation="right",$margin_top=10,$margin_right=10,$myText="")
// ***************************************************************************************************
{
    if (file_exists($abs_file_name))
    {
        $file_name = basename($abs_file_name);

        if ($orientation=="right") abs_r($margin_top,0,$margin_right);

        if ($orientation=="top")   echo "<p style='position: absolute; top:".$margin_top."px; right: ".$margin_right."px; text-align: right;'>";

        if ($orientation=="left")  echo "<p style='position: absolute; top:".$margin_top."px; left: ".$margin_right."px; text-align: right;'>";

        $js_action = "javascript: document.location.href='"._SHARED_URL."file_download.php?srcFile=".urlencode($abs_file_name)."';";

        $file_extension = strtolower(substr($file_name,strripos($file_name,".")+1));

        $myImage = _SHARED_IMG.$file_extension.".gif";

        if (!file_exists($myImage)) $myImage = _SHARED_IMG."download.gif";

        if (!function_exists('icon')) include_once(_SHARED."imagelib.php");

        icon($myImage,"Download ".substr($file_name,0,15)." ...","onClick=\"".$js_action."\"");

        if ($myText!="") echo $myText;

        if (($orientation=="right") or ($orientation=="top") or ($orientation=="left")) abs_e();
    }
    else return false;
}



// ****************************************************************************
function close_popup($die=0,$marginTop=5,$marginBottom=0,$myTxt="schliessen")
// ****************************************************************************
{
 echo "<p style='margin-top: ".$marginTop."px; margin-bottom: ".$marginBottom."px; margin-right:10px;' align=right>";
 echo "<a href='#' onClick=\"closeMe(0);\" >";

 if (!function_exists('icon')) include_once(_SHARED."imagelib.php");

 icon(_SHARED_IMG."del");

 if ($myTxt!="") echo "<font class=standard>".$myTxt."</font>";

 if ($die==0)  die("</a></p></body></html>");
 if ($die==-1) echo "</a></p>";
}



/**
 * ECHO INPUT BUTTON WITH JS CLOSE CODE
 *
 * @param unknown_type $JSmode
 * @param unknown_type $width
 * @param unknown_type $extraJS
 * @param unknown_type $confirm_txt
 * @param unknown_type $title
 */
function close_button($JSmode="",$width="",$extraJS="",$confirm_txt="",$title="close")
// ****************************************************************************************
{
    if ($confirm_txt!="")
    button($title,"if (confirm('".$confirm_txt."')) { closeMe(".$JSmode."); } ".$extraJS,$width);
    else
    {
        // KEIN CONFIRM UND MODE 0 (KEIN TOOLS.JS NOETIG DA NATIVER AUFRUF)
        if ($JSmode==0) button($title,"if (opener.document) { opener.document.body.focus(); } window.close();",$width,-1);
        else
        button($title,"closeMe(".$JSmode."); ".$extraJS,$width);
    }
}



// ************************************************************
function arr_count_values($myArr,$key=0,$value=1,$mode="count")
// ************************************************************
{
    $nrows = count($myArr);
    $retVal= 0;

    for ($i=0;$i<$nrows;$i++)
    {
        if ($mode=="count") { if ($myArr[$i][$key]==$value) $retVal++; }
        if ($mode=="sum")
        {
            $curVal = $myArr[$i][$key];
            if ((!is_numeric($curVal)) or ($curVal=="")) $curVal = 0;
            $retVal += $curVal;
        }
    }

    return $retVal;
}



// **************************
function eliminate_js($myVal)
// **************************
{
	// JAVASCRIPT ELIMINIEREN
	$found = true;

	while ($found)
	{
		$pos1 = strpos(strtoupper($myVal),"<SCRIPT");

		if ($pos1 === false) $found = false;
		else
		{
			// SUCHE ENDPOSITION
			$pos2 = strpos(strtoupper($myVal),"</SCRIPT",$pos1);

			if ($pos2 === false) $found = false;
			else
			{
				js_alert("Javascript gefunden und eliminiert...\nJavascript found and removed...");
				$myVal = substr($myVal,0,$pos1).substr($myVal,$pos2+9,strlen($myVal)-($pos2+9));
			}
			unset($pos1);
			unset($pos2);
		}
	}
	return $myVal;
}



// ******************************************
// href zwingend, name optional
// sonst wird nur icon mit link ausgegeben
// ******************************************
function hilink($href,$name="",$alt_text="")
{
 $retVal = "";

 $retVal .= "<a href='".$href."' target='_blank'>".$name;

 if (!function_exists('icon')) include_once(_SHARED."imagelib.php");

 $retVal .= icon(_SHARED_IMG."hilink.gif",$alt_text,"","","",false);

 $retVal .= "</a>";

 return $retVal;
}



// **************************
// SALZ FUER ZUFALLSGENERATOR
// **************************
// srand(make_seed());
// $randval = rand();
function make_seed()
{
  list($usec, $sec) = explode(' ', microtime());
  return (float) $sec + ((float) $usec * 100000);
}



// **********************************************************************
// NIMMT IM FALLE EINES BLENKS ALLES NACH DEM LETZTEN BLENK UND TRIMMT ES
function last_word($phrase)
// **********************************************************************
{
    $phrase = trim($phrase);
    if ($phrase=="")         return false;
    if (instr($phrase," "))  return trim(substr($phrase,strrpos($phrase," ")+1,1024));
    else                     return $phrase;
}



// ******************************
function nTimes($phrase,$times=2)
// ******************************
// REPEAT STRING/CHAR nTimes
{
    $retVal = "";

    for($i=0;$i<$times;$i++) $retVal .= $phrase;

    return $retVal;
}



// ************************************
function get_table_from_html($content)
// ************************************
// PREMISSE: NUR 1 TABLE IM HTML, SONST ALLES VON ERSTEN BIS LETZTEN TABLE ABSCHLUSS
{
    $pos1    = strpos(strtolower($content),"<table");
    $pos2    = strrpos(strtolower($content),"</table>");
    return substr($content,$pos1,$pos2-$pos1+strlen("</table>"));
}



// *************************************************
function format_number($zahl=0,$nachkommastellen=0)
// *************************************************
{
    return number_format($zahl,$nachkommastellen,",",".");
}



// ************************
function cleantxt($myText)
// ************************
{
    $arr_not_allowed = array("'",'"','�','�',"\"","<",">");

    return str_replace($arr_not_allowed," ",$myText);
}



/**
 * Rectangle CSS Design Layer
 *
 * @param String $width (Breite)
 * @param String $style (ohne with, optional ohne border)
 * @param String $id    (optional)
 * @return HTML DIV Tag (String)
 */
function rect_start($width="100%", $style="border: 2px outset #f9f9f9;", $id="")
{
    $retVal  = "<div ";

    // JEDENFALLS BORDERANGABEN (FALLS NICHT MITGEKOMMEN STANDARD)
    if (!instr($style,"border:")) $style .= " border: 2px outset #f9f9f9;";

    if ($id!="")    $retVal .= " id=\"".$id."\" name=\"".$id."\" ";

    $retVal .= " style=\"";

    // DIV ELEMENT HAT KEIN WIDTH ATTRIBUT NUR MIT CSS STEUERBAR
    if ($width!="") $retVal .= "width: ".$width;

    if (!instr($width,array("%","px","em","pt"))) $retVal .= "px";

    $retVal .= "; ".$style."\" >";

    return $retVal;
}



// *** LAYER RECTANGLE END ***
/**
 * End of a Rectangle CSS Design Layer
 *
 * @return DIV Close Tag (String)
 */
function rect_end()
{
    return "</div>";
}



/**
 * STOP CURRENT SCRIPT WRITING HTML ENDINGS ...
 *
 * @param integer $mode
 *
 *                      1... CLOSE HTML PAGE
 *                      2... MODE 1 & WINDOW CLOSE
 *                      3... WINDOW CLOSE & MODE 2
 *                      9... NO OUTPUT JUST EXIT
 * @param integer $error_code
 *
 *                      99.. DEFAULT ERROR CODE
 *                      0 .. NO ERROR
 *
 * @param boolean $reload_opener
 *
 *                      RELOAD OPENER.DOCUMENT Y|N
*/
function halt($mode=1,$error_code=99,$reload_opener=false)
{
    $strEnd="";

    if ($error_code=="") $error_code=99;

    if ($reload_opener) js_command("opener.document.location.reload();");

    switch($mode)
    {
        case 2:
            $strEnd="\n</FORM>\n</BODY>\n</HTML>\n";
            break;

        case 3:
            js_command("window.close();");
            $strEnd="\n</FORM>\n</BODY>\n</HTML>\n";
            break;

        case 9:
            $strEnd="";
            break;

        default:
            $strEnd="\n</BODY>\n</HTML>\n";
            break;
    }

    if ($strEnd!="") echo $strEnd;

    exit($error_code);
}




// ***************************************************
function show_error($error_text,$prefix=true,$width=0)
// ***************************************************
{
    echo "<p ";

    if ($width!=0) echo " style=\"width:".$width."px;\" ";

    echo ">\n<font class=error>";

    if ($prefix) echo _ERR;

    echo $error_text."</font></p>\n";
}


// ******************************************************************
function hline($height=1,$color='black',$width='100%',$border='none')
// ******************************************************************
{
	echo "<hr style=\"height: ".$height."px; background-color: ".$color."; ";
	
	if ($width!="100%") echo " width: ".$width."px; ";
	
	echo "border: ".$border."; margin: 1px 1px 1px 1px; text-align: left;\">";
}


/**
 * MICROSOFT OUTLOOK PROPRIETAERES MAILTO: HREF TAG
 *
 * @param string $recipients
 * @param string $subject
 * @param string $body    (IMPORTANT: mask new line with \n)
 * @return full qualified href, ... &lt;a href='mailto:....'&gt;
 */
function mailto_href($recipients="",$cc="",$subject="",$body="",$echome=false)
{
    // OUTLOOK VERLANGT PARAMETER MIT UNMASKIERTEM & VERKNUEPFT (STANDARD WAERE &amp;)

    $retVal = "<a href='mailto:".$recipients."?";

    if ($cc!="")     $retVal .= $cc."&";

    if ($subject!="")$retVal .= "subject=".$subject."&";

    if ($body!="")
    {
        $body = str_replace(array("\n"," ","&","\"","'"),array("%0D%0A","%20","%26","%22","%27"),$body);
        $retVal .= "body=".$body;
    }

    $retVal .= "'>";
    
    if ($echome) echo $retVal;
    else         return $retVal;
}


/**
 * CREATES $BYTES OF INVISIBLE HTML CODE (E.G. FILLS OUTPUT BUFFERS)
 *
 * @param int $bytes
 */
function invisible_html($bytes)
{
    $retVal="";
    $bytes = (int) round(($bytes/8),0);
    for($i=0;$i<$bytes;$i++) $retVal .= "<b> </b>"; // 8 bytes content
    return $retVal;   
}


/**
 * CREATES A CLEAN PATH WITHOUT X-TIMES /
 *
 * @param int $bytes
 */
function clean_path($myDir,$letter="/")
{
    // 2,3 UND 4-FACHE VORKOMMEN BEREINIGEN
    return str_replace(array($letter.$letter.$letter.$letter,$letter.$letter.$letter,$letter.$letter),$letter,$myDir);
}


/**
 * CORRECTS SLASHS TO BAKSLASH IN WINDOWS ENVIRONMENTS
 *
 * @param int $bytes
 */
function exec_prepare($myCmd)
{
    // KORREKTUR VON PFADEN IN WINDOWS UMGEBUNGEN
    if (instr($myCmd, ":/")) $myCmd = str_replace("/", "\\", $myCmd);
    
    return $myCmd;
}


?>
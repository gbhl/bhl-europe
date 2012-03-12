<?php
// ***************************************
// ** FILE:    STRINGS.PHP              **
// ** PURPOSE: DIV. GENERAL TOOLS       **
// ** DATE:    18.02.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************



/**
  * RETURN SUBSTRING DEFINED BY TWO POSITIONS (NOT ITS LENGTH)
  * INCLUSIVE THE LAST KEY. IF $POS2=0,NULL IT RETURNS STRING TILL END
  * EXAMPLE: OPERATION FROM INDEX 0 TO 0 DELIVERS FIRST ITEM.
  *
  * @param string   $myString
  * @param integer  $pos1
  * @param integer  $pos2
  */
function substr_pos($myString,$pos1=0,$pos2=0)
{
	if ($pos2==0) $pos2 = strlen($myString)-1;

	if (($myString!="")&&($pos1>=0)&&($pos2>=$pos1))
	return substr($myString,$pos1,($pos2-$pos1)+1);

	return "";
}



/**
  * RETURNS TRUE IF STRING CONTAINS THE NEEDLE, OR ALL NEEDLES IF THEY ARE AN ARRAY.
  * BE AWARE: IN ARRAY COMPARISION IT RETURNS TRUE IF "A PART" OF EACH OR ONE ELEMENT
  *
  * @param  string    $haystack
  * @param  mixed     $needle
  * @param  boolean   $ignoreCase
  * @param  boolean   $oneMatchOnly
  * @return boolean   true|false 
  * @desc   IMPORTANT: ONEMATCHONLY MEANS THAT ONLY ONE NEEDLE (IF NEEDLE IS AN ARRAY) 
  *         MUST BE FOUND TO RETURN TRUE.
  */
function instr($haystack,$needle,$ignoreCase=false,$oneMatchOnly=false)
{
    
    // 1. MULTIVERGLEICH STRING MIT ARRAY
    if (is_array($needle))
    {
        $anz = count($needle);
        
        for ($i=0;$i<$anz;$i++)
        {
            // EVERY ITEM NECESSARY
            if (!$oneMatchOnly) { 
                if (!(instr($haystack,$needle[$i],$ignoreCase,$oneMatchOnly))) return false; }
            
            // ONE PARTLY MATCH ENOUGH
            else { 
                if ((instr($haystack,$needle[$i],$ignoreCase,$oneMatchOnly))) return true;   } 
        }
        
        if (!$oneMatchOnly) return true;   // HAETTE SCHON FALSE ZURUECKGEGEBEN
        else                return false;  // WENN EINE MATCH GEFUNDEN WAERE, HATTE SCHON TRUE ZURUECKGEGEBEN
    }
    else
    {
        // 2. EINZELVERGLEICH STRING MIT STRING
        
        if ( (strlen($haystack)>0) and (strlen($needle)>0) )
        {
            if ($ignoreCase)	$pos = stripos($haystack, $needle);
            else		$pos = strpos($haystack, $needle);

            if ($pos === false)  return false;
            else                 return true;
        }

        return false;
    }
    
}




/**
 * @return integer
 * @desc Gibt Anzahl numerischer Zeichen im Uebergabestring zurueck.
*/
function anz_numeric($myString)
// ****************************
{
    // URSPRUNGSLAENGE ABZUEGLICH LAENGE OHNE NUMERISCHE ZEICHEN IST ANZAHL DIESER
    $len1 = strlen($myString);
    $len2 = strlen(str_replace(array("0","1","2","3","4","5","6","7","8","9"),"",$myString));
    return ($len1-$len2);
}





/**
 * RETURN ONLY NUMERIC VALUES OF A VARIOUS STRING
 *
 * @param  String $myVal
 * @return (numeric) String
 */
function onlyNumeric($myVal)
{
    $retVal   = "";

    $myValLen = strlen($myVal);

    for($i=0;$i<$myValLen;$i++) if (is_numeric($myVal[$i]))  $retVal .= $myVal[$i];

    return $retVal;
}




/**
 * RETURNS ALL ASCII NUMBERS OF ALL CHARS IN AN ARRAY
 * FOR ANALYSIS & TEST ONLY
 *
 * @param string    $myStr
 * @param int       $limit
 * @return          string
 */
function string_char_codes($myStr,$limit=0)
// ***************************************************
{
    $retVal = "";

    if (($limit==0)||($limit>strlen($myStr)))  $limit = strlen($myStr);

    for ($i=0;$i<$limit;$i++)
    {
        $retVal .= "Char[".$i."]=".ord($myStr[$i]).", ";
    }

    if ($retVal!="") return substr($retVal,0,-2);
    else             return false;
}





/*
* STRING FUNCTION - SUBSTITUTE PLACEHOLDERS
* REPLACE MULTIPLE OCCURENCES OF SAME PLACEHOLDER WITH DIFFERENT SUBSTITUTES IN ORDER
*
* @param string  $subject
* @param mixed   $replace
* @param string  $placeholder
* @param int     $limit
* @return unknown

EXAMPLE: echo sts("Der ### ist ### und ###.",array("Baer Bruno","braun","schwer"),"",3);
*/
function sts($subject,$replace="",$placeholder="###",$limit=0)
// ************************************************************************************
{
    $retVal = "";

    if ($placeholder=="") $placeholder = "###";

    if ($subject!="")
    {
        $retVal = $subject;

        if ((!is_array($replace))&&($replace!="")) $replace = array($replace);

        if (is_array($replace))
        {
            $cur_pos = strpos($retVal,$placeholder);
            $i = 0;

            while ((!($cur_pos===false))&&(array_key_exists($i,$replace))&&(($i<$limit)||($limit==0)))
            {
                $retVal = preg_replace("/".$placeholder."/",$replace[$i],$retVal,1);  // IMMER 1 PLACEHOLDER ERSETZEN
                $cur_pos = strpos($retVal,$placeholder);
                $i++;
            }
        }
    }

    return $retVal;
}




/**
* RETURNS FIRST LINK IN A STRING IF EXISTS
*
* @param string $myText
*/
function getURL($myText,$urlPrefix="http",$last=false)
// ************************************************************************************
{
    // WICHTIG FALLS VOR ODER NACH URL EIN STEUERZEICHEN UND KEIN BLENK ALLE MIT BLENK ERSETZEN
    $myText = str_replace(array("\n","\t","\r","\v","\f",chr(9),chr(10),chr(12),chr(13))," ",$myText);

    if (instr($myText,$urlPrefix))
    {
        if (!$last) $pos  =  strpos($myText, $urlPrefix);
        else        $pos  = strrpos($myText, $urlPrefix);

        $pos2 = strpos($myText," ",($pos+strlen($urlPrefix)));

        if ($pos2===false)  return substr($myText,$pos);                // BIS ZUM ENDE
        else                return substr($myText,$pos,($pos2-$pos));   // BIS ZUM ERSTEN BLENK
    }

    return false;
}





/**
  * REPLACE ALL URLS IN A TEXT WITH LINK SHORTCUTS OR ICONS ETC.
  *
  * @param unknown_type $myText
  * @param unknown_type $substitute
  */
function substitueURLs($myText,$substitute="Link...",$urlPrefix="http")
{
    $retVal     = $myText;
    $amEnde     = false;
    $tempMarker = "|+++++|";
    $durchlauf  = 1;

    // echo_pre(string_char_codes($myText));

    while ((!$amEnde)&&($durchlauf<9999))
    {
        // URLS UMSETZEN IN WEB LINK ICONS
        $urlFound = getURL($retVal,$urlPrefix);

        if ($urlFound)
        {
            // IN RUECKGABE DEN LINKSUBSTITUTE EINBAUEN UND SUBSTITUTE UNKENNTLICH MACHEN IN DIESEM TEIL
            // DAMIT DIESER NICHT MEHR REPLACED WIRD, GILT AUCH FUER SUBSTITUTE DER EIN BILD MIT HTTP TAG SEIN KANN
            $retVal = str_replace($urlFound,"<a href='".
            str_replace($urlPrefix,$tempMarker,$urlFound)."' target=_blank>".
            str_replace($urlPrefix,$tempMarker,$substitute)."</a>",$retVal);
        }
        else
        $amEnde = true;

        $durchlauf++;
    }

    return str_replace($tempMarker,$urlPrefix,$retVal);
}



?>
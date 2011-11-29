<?php
// ***************************************
// ** FILE:    PARSER_TOOLS.PHP         **
// ** PURPOSE: TA ORACLE SYSTEM VIEW    **
// ** DATE:    25.02.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************





// ************************************************************************************************
/*
* UNIVERSAL ROUTINE TO FIND VALUES NEAR PATTERNS IN TEXT FILES, BY A. MEHRRATH
*
* @param unknown_type $pattern
* @param unknown_type $haystack
* @param unknown_type $reverse
* @param unknown_type $limit
* @param unknown_type $mode        below|following|...
* @return array|false
*/
function find_values_near($pattern,$haystack,$reverse=false,$limit=0,$mode="below")
// ************************************************************************************************
{
    global $debug;

    // FILEfORMAT (DOS,UNIX) BEACHTEN

    $pos = strpos($haystack,chr(13).chr(10));

    if ($pos === false) $arrLines = explode(chr(10),$haystack);
    else                $arrLines = explode(chr(13).chr(10),$haystack);

    if ($reverse)    $arrLines = array_reverse($arrLines);             // INHALTS-ARRAY UMKEHREN

    if ($limit>0)    $arrLines = array_splice($arrLines,$limit);

    $nLines    = count($arrLines);

    $durchlauf = 0;

    // if ($debug) echo string_char_codes($arrLines[0]);               // zeichen in zeile testevern

    if ($debug) echo logit("Info",$nLines." lines are analyzed...");

    while ($durchlauf<$nLines)
    {
        // ANALYSE CURRENT ROW
        $cur_line = trim($arrLines[$durchlauf]);

        $pos      = strpos($cur_line,$pattern);     // if (preg_match("/".$pattern."/i",$arrLines[$durchlauf]))

        if ($pos === false)
        {
            // KEINE AKTION
        }
        else // PATTERN FOUND
        {
            if ($debug) echo logit("Info","Pattern :: ".$pattern." :: found at line ".$durchlauf);


            // BELOW: SUCHE DER WERTE IN DEN NAECHSTEN ZEILEN BIS ZUR NAECHSTEN LEERZEILE
            if ($mode=="below")
            {
                // 2 ZEILEN VORSPRINGEN UND

                $durchlauf += 3;

                // NUN BIS ZUR NAECHSTEN LEERZEILE VALUES HOLEN

                $arrReturn = array();

                while ($cur_line!="")
                {
                    $cur_line = trim($arrLines[$durchlauf]);   //  VERMUTLICHE ERSTE VALUE ZEILE

                    if ($cur_line!="")  $arrReturn[] = $cur_line;

                    $durchlauf++;
                }

                if (count($arrReturn)>0) return $arrReturn;
                else                     return false;
            }

            // FOLLOWING: WERTE IN DER GLEICHEN ZEILE EVTL. BESTIMMTER SPALTE
            if ($mode=="following")
            {
                return array(trim(str_replace($pattern,"",$cur_line)));
            }
        }

        $durchlauf++;
    }

    return false;
}



?>
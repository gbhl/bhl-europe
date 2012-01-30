<?php
// ***************************************
// ** FILE:    ARRAY_TOOLS.PHP          **
// ** PURPOSE: TA ORACLE SYSTEM VIEW    **
// ** DATE:    31.03.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************




/**
 * GET A PART OF AN ARRAY
 *
 * @param array       $myArray
 * @param int         $start          Start Index
 * @param int         $end            End Index
 * @param bool        $preserveKeys   original key values or not
 * @param string      $endPattern     part ends before occurence of $endPattern
 * @return unknown
 *
 * $endPattern with % uses eregi comparison, without it compares full content, if $endPattern
 * not found returns everything to the end.
*/
function array_part($myArray,$start=0,$end=0,$preserveKeys=false,$endPattern="")
// *****************************************************************************
{
    $rArr  = array();                      // arr,1,2              arr,0,0, > 4
    $myLen = count($myArray);

    if ($end==0)   $len = $myLen-$start;   // 0   1   2   3
    else           $len = 1+$end-$start;

    if (($len>0)&&($endPattern==""))
    {
        $end = $start+$len;

        for ($i=$start;$i<$end;$i++)
        {
            if ($preserveKeys)      $rArr[$i] = $myArray[$i];
            else                    $rArr[]   = $myArray[$i];
        }
    }
    else
    {
        for ($i=$start;$i<$myLen;$i++)
        {
            // EREGI VERGLEICH ODER FULL VERGLEICH
            if ((instr($endPattern,"%"))&&(eregi(str_replace("%","",$endPattern),$myArray[$i])))
                break;
            else if ($myArray[$i]==$endPattern)
                break;

            if ($preserveKeys)      $rArr[$i] = $myArray[$i];
            else                    $rArr[]   = $myArray[$i];
        }
    }

    return $rArr;
}




/**
 * SEARCH FOR CONTENTS BEFORE AND AFTER A PATTERN IN A STRING
 * WHEREBY THE STRING IS IN AN ARRAY OF STRINGS
 *
 * @param unknown_type $myArray
 * @param unknown_type $pattern
 * @return IF PATTERN IS FOUND IN AN ELEMENT OF THE ARRAY IT RETURNS THE ELEMENT
 *         CONTENT BEFORE AND AFTER THAT PATTERN, IF NOT FOUND IT RETURNS FALSE
 */
function array_find_value_near($myArray,$pattern)
// **********************************************
{
    $rVal  = "";
    $myLen = count($myArray);

    $newArray = array_part($myArray,0,0,true,$pattern);

    $newArray = array_keys($newArray);

    $foundKey = 1+array_pop($newArray);

    $rVal = $myArray[($foundKey)];

    $pattern = str_replace("%","",$pattern);

    if (instr($rVal,$pattern))  return trim(str_replace($pattern,"",$rVal));
    else                        return false;
}


?>
<?php
// ********************************************
// ** FILE:    EVAL_TOOLS.PHP                **
// ** PURPOSE: EVAL TERM PREPARATION         **
// ** DATE:    07.06.2010                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// PHP EVAL TERM PREPARATION

// !!! DIESE FUNKTIONEN KONSOLIDIEREN UND VERWENDUNGSORTE DAZU MITZIEHEN


// ****************************
function prepare2eval($evalStr)
// ****************************
{
    return html_entity_decode($evalStr);
}




// ************************************
function prepareString2eval($evalStr)
// ************************************
{
    $evalStr = str_replace("\'","#quot#",$evalStr);
    $evalStr = str_replace("'","\'",$evalStr);

    return str_replace("#quot#","\'",$evalStr);
}




// *************************************************************************
// ALLGEMEINE VORBEREITUNG USER SEITIG EINGEGEBENEN, ZU EVALUIERENDEN CODES
// ! ... RUFZEICHEN WERDEN ZU DOPPELHOCHKOMMA  "
// # ... RAUTEN WERDEN ZU HOCHKOMMA            '
// ~ ... RUND WIRD ZU RAUTE                    #
// *************************************************************************
function prepare_eval($phrase)
{
    $retVal = "";
    $retVal = str_replace("!", "\"",$phrase);
    $retVal = str_replace("#", "'", $retVal);
    return    str_replace("~", "#", $retVal);
}





/**
 * EVAL INLINE PHP CODE EMBEDDED, E.G. IN HTML
 *
 * @param String $myContent
 * @return static + evaluated output string

 DEPRECATED???

 */
// USE $GLOBALS['***'] VARIABLE TO ACCESS RUNTIME VARS IN EVAL CODE!
function eval_inline_php($myContent,$onlyEval=false)
// *************************************************
{
	$rVal = "";

	// PHP INCLUDES
	$pos1 = strpos($myContent,"<?php");

	if (!($pos1===false))
	{
		$pos2 = strpos($myContent,"?>",$pos1);

		$rVal .= substr($myContent,0,$pos1);

		// BLOCK AUSWERTEN
		$evalTxt = substr($myContent,$pos1+5,($pos2)-($pos1+5));
		eval($evalTxt);

		// RUECKGABE BLOCK DAVOR UND DANACH
		if (!$onlyEval) return $rVal.substr($myContent,$pos2+2);
	}
	else
		return $myContent;

	return "";
}


?>
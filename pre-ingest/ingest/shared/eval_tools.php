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




?>
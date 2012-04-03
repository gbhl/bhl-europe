<?php
// ********************************************
// ** FILE:    IPR_INFO.PHP                  **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    25.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// IPR EXTRAHIEREN
$content_ipr = "";


if (instr($olef, "accessCondition",true,true))
{
    $pos1        = stripos($olef,"accessCondition");
    $pos2        = stripos($olef,">",$pos1+14);
    $pos3        = stripos($olef,"<",$pos2+1);
    $content_ipr = str_replace("\n","",trim(substr($olef,$pos2+1,$pos3-($pos2+1))));

    echo "\nContent IPR: ".$content_ipr." .... found in OLEF.\n\n";
}

// NUR EIN GUELTIGER EINTRAG WIRD UEBERNOMMEN
if (($content_ipr != "")&&(!instr($content_ipr,array_keys($arrIPR),true,true)))
{
    $content_ipr = "";
}

// KEINE GUELTIGEN DATEN VORHANDEN
if ($content_ipr == "") 
{
    $content_ipr = $arrProvider['default_ipr'];

    // KEIN GUELTIGER DEFAULT WERT BISLANG
    if (!instr($content_ipr,array_keys($arrIPR),true,true))  
    {
        // USER DEFAULT EINTRAGEN UND ZU AKTUELLEM IPR MACHEN
        mysql_select("update "._USER_TAB." set default_ipr='".$arrIPR[(_DEFAULT_IPR)]."' where user_id=".$user_id,$db);

        echo "\nContent IPR default auto set in your preferences. Please check them.\n\n";

        $content_ipr = $arrIPR[(_DEFAULT_IPR)];
    }
}

// CONTENT IPR UPDATE
$query = "update content set content_ipr = '".$content_ipr."' where content_id=".$content_id;
mysql_select($query,$db);

echo "\nContent IPR: ".$content_ipr." confirmed.\n\n";

unset($content_ipr);



?>

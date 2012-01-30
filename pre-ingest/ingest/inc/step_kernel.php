<?php
// ********************************************
// ** FILE:    STEP_KERNEL.PHP               **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    20.12.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// REQUIRES: $content_id

include_once(_SHARED . "formlib.php");
include_once(_SHARED . "imagelib.php");

$ingestReady = false;       // SET TO TRUE IF LAST STEP SUCCEEDS

?>

<style>
    body { margin: 10px 10px 10px 10px; }
    pre  { width: 5000px; font: 10px arial; color: white; background-color: black; }
</style>

<?php

if ((isset($content_id))&&(is_numeric($content_id))) 
{
    // VORAB INFOS UEBER CONTENT UND BEREITS ERSTELLTES HOLEN
    $arrQueueCommands = array();
    
    if (!instr($_SERVER['HTTP_HOST'],"localhost"))
    progressBar("Please wait operation in progress...", "processing.gif", 
            "margin-top: 75px; left: 350px;", "visible", 2);

    // INVOKE TARGET SCRIPT
    include_once("inc/" . $menu_nav.".php");

    if (!instr($_SERVER['HTTP_HOST'],"localhost"))
    close_progressBar();    

    if ($menu_nav=='get_mets')
    {
        // WRITE INGEST READY FILE 
        if ($ingestReady) 
        {
            $fText = "This AIP is marked as ready for ingest for Fedora.";
            
            file_put_contents($destDir._FEDORA_CF_READY,date("Y-m-d H:i:s")."\t".$fText);

            $endmsg .= $fText;

            unset($fText);
        }
    }
    else if ((_QUEUE_MODE)&&($menu_nav!='get_metadata'))
    {
        echo queue_add($curQueueFile, $arrQueueCommands);
    }
}
else
    echo _ERR . " Content ID is missing...";

nl(2);

// CLOSE & REFRESH PARENT
close_ingest_popup("CLOSE here to auto refresh your management list behind ...");

nl(2);


@ob_end_flush();
@ob_flush();
@flush();

?>

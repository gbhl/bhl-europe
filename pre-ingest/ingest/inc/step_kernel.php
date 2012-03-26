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

$ingestReady = true;   // SET TO TRUE IF LAST STEP SUCCEEDS

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
    
    if (_MODE=='production') progressBar("Please wait operation in progress...", "processing.gif",
            "margin-top: 75px; left: 350px;", "visible", 2);

    // INVOKE TARGET SCRIPT
    include_once($menu_nav.".php");

    if (_MODE=='production') close_progressBar();    

    if ($menu_nav=='get_mets')
    {
        // WRITE INGEST READY FILE 
        if ($ingestReady) 
        {
            $fText = "This AIP is marked as ready for ingest for Fedora. ";

            // CONTROLFILE
            @file_put_contents($destDir._FEDORA_CF_READY,date("Y-m-d H:i:s")."\t".$fText);

            // ACTIVE MQ MESSAGE (STOMP)
            include_once("message_broker.php");

            if ((_MODE=='production') && (mb_send_ready($cGUID,$destDir)))
                $fText .= "ActiveMQ ingest message sent.";

            $endmsg .= $fText;

            unset($fText);
        }
        else  $endmsg .= "Due some missing information the package is not ready for ingest.";
    }
    else if ((_QUEUE_MODE)&&($menu_nav!='get_metadata'))
    {
        echo queue_add($curQueueFile, $arrQueueCommands);
    }
}
else
    echo _ERR . " Content ID is missing...";

nl();

// CLOSE & REFRESH PARENT
close_ingest_popup("CLOSE here to Auto-Refresh your management list behind...");

nl(2);

@ob_end_flush();
@ob_flush();
@flush();

?>

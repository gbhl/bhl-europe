<?php
// ********************************************
// ** FILE:    STEP_KERNEL.PHP               **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    20.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

$arrQueueCommands = array();

if (_MODE=='production') 
progressBar("Please wait operation in progress...", "processing.gif","margin-top: 75px; left: 350px;", "visible", 2);


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
        //@file_put_contents($destDir._FEDORA_CF_READY,date("Y-m-d H:i:s")."\t".$fText);

        // ACTIVE MQ MESSAGE (STOMP)
        /*include_once("message_broker.php");
        if ((_MODE=='production') && (mb_send_ready($cGUID,$destDir)))
            $fText .= "ActiveMQ ingest message sent.";*/
        // add entry to ingest queue
        mysql_select("INSERT INTO `ingest_queue`
            (`content_id`, `guid`, `sip_path`)
            values
            ('" . $content_id . "', '" . mysql_escape_string($cGUID) . "', '" . mysql_escape_string($destDir) . "')"
        );

        $endmsg .= $fText;

        unset($fText);
    }
    else  $endmsg .= "Due some missing information the package is not ready for ingest.";
}
else if ((_QUEUE_MODE)&&($menu_nav!='get_metadata'))
{
    echo queue_add($curQueueFile, $arrQueueCommands);
    echo queue_done($curQueueFile);
}

unset($arrQueueCommands);

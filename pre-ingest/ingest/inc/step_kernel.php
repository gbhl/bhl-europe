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


?>

<style>
    pre { font: 10px arial; color: white; background-color: black; }
</style>

<?php

if ((isset($content_id))&&(is_numeric($content_id))) 
{
    // VORAB INFOS UEBER CONTENT UND BEREITS ERSTELLTES HOLEN
    $arrQueueCommands = array();
    
    progressBar("Please wait operation in progress...", "processing.gif", "margin-top: 55px; left: 300px;", "visible", 2);

    // INVOKE TARGET SCRIPT
    include_once("inc/" . $menu_nav.".php");

    close_progressBar();    

    if (_QUEUE_MODE) {
        echo queue_add($curQueueFile, $arrQueueCommands);
    }
    
    
}
else
    echo _ERR . " Content ID is missing...";

nl();

close_button(2, 900, "", "", "CLOSE here to auto refresh your management list behind ...");

nl(2);


@ob_end_flush();
@ob_flush();
@flush();

?>

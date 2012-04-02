<?php
// ********************************************
// ** FILE:    KERNEL.PHP                    **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    20.03.2012                    **
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
    if (($cType=='serial')||(!$isPDF))  include_once("inc/serial_kernel.php");
    else                                include_once("inc/step_kernel.php");
}
else
    echo _ERR . " Content ID is missing or wrong ...";

nl();

// CLOSE & REFRESH PARENT
close_ingest_popup("CLOSE here to Auto-Refresh your management list behind...");

nl(2);

@ob_end_flush();
@ob_flush();
@flush();

?>

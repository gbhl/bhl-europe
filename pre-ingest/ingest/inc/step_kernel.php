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
    $contentDir  = abfrage("select content_root from content where content_id=" . $content_id);
    $contentName = abfrage("select content_name from content where content_id=" . $content_id);
    $destDir     = clean_path($contentDir . "/" . _AIP_DIR . "/");
    $workDir     = clean_path(_WORK_DIR . $arrProvider['user_content_id'] . "/");

    // GENERATE WORK DIR
    if (!is_dir($workDir))      @mkdir($workDir);
    if (!is_dir($destDir))      @mkdir($destDir);     

    // INVOKE TARGET SCRIPT
    include_once("inc/" . $menu_nav.".php");
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

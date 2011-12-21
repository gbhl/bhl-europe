<?php
// ********************************************
// ** FILE:    GET_IMAGES.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

echo "<h1 style='margin-top: 3px;'>Preparing/Generating TIFF files from foreign page format</h1>";

// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?
$arrTiffs = getPageTIFFiles($user_id, $contentDir);    
$nTiffs   = count($arrTiffs);

if ($nTiffs == 0)    {
    $arrTiffs = getPageTIFFiles($user_id, $destDir);    
    $nTiffs   = count($arrTiffs);    
}

if ($nTiffs > 0)    echo "Tiff files present - no convert initiated!\n";
else 
{   
    if (isPDF($contentName)) include("inc/pdf2tiff.php");     // $arrTiffs am ende setzten auf die generierten !!!
    else                     include("inc/images2tiff.php");  // KEIN PDF
}


// NEU ZAEHLEN
$nTiffs   = count($arrTiffs);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTiffs > 0) {
    // IF SUCCESSFUL SET STATE TO 2
    setContentSteps($content_id, 2);

    $csvTiffs = implode(_TRENNER, $arrTiffs);
    $csvTiffs = str_replace(_CONTENT_ROOT, "", $csvTiffs);
    mysql_select("update content set content_pages_tiff='" . $csvTiffs . "' where content_id=" . $content_id);

    $endmsg .= $nTiffs . " files generated and database updated successfully.";
} else if (_QUEUE_MODE) {
    echo queue_add($curQueueFile, $arrQueueCommands);
}
else
    echo _ERR . " The necessary files could not be prepared!";
?>

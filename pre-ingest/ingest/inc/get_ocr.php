<?php
// ********************************************
// ** FILE:    GET_OCR.PHP                   **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

echo "<h1 style='margin-top: 3px;'>Preparing/Generating OCR Text files from TIFF files</h1>";

// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?
$arrTextFiles = getPageTextFiles($user_id, $contentDir);
$nTextFiles   = count($arrTextFiles);

if ($nTextFiles == 0)    {
    $arrTextFiles = getPageTextFiles($user_id, $destDir);    
    $nTextFiles   = count($arrTextFiles);    
}

if ($nTextFiles > 0)    echo "Text files present - no OCR initiated!\n";
else 
{   
    if (isPDF($contentName)) include("inc/ocr_pdf.php");    // PDF TO TEXT CONVERT  
    else                     include("inc/ocr_tiff.php");   // TESSERACT OCR
}


// NEU ZAEHLEN
$nTextFiles   = count($arrTextFiles);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTextFiles > 0) {
    // IF SUCCESSFUL SET STATE TO 3
    setContentSteps($content_id, 3);

    $csvTextfiles = implode(_TRENNER, $arrTextFiles);
    $csvTextfiles = str_replace(_CONTENT_ROOT, "", $csvTextfiles);
    mysql_select("update content set content_pages_text='" . $csvTextfiles . "' where content_id=" . $content_id);

    $endmsg .= $nTextFiles . " files generated and database updated successfully.";
} else if (_QUEUE_MODE) {
    echo queue_add($curQueueFile, $arrQueueCommands);
}
else
    echo _ERR . " The necessary files could not be prepared!";
?>

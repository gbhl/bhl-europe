<?php
// ********************************************
// ** FILE:    GET_OCR.PHP                   **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
$curStep = 3;

include_once(_SHARED."pdf_tools.php");

echo "<h1 style='margin-top: 3px;'>Preparing/Extracting Plain Texts ";

if ($cType == 'serial')
    echo "<br><font size=1 color=blue>For: .".str_replace(_USER_CONTENT_ROOT,"",$contentDir)."</font>";

echo "</h1>";



// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?

$arrTextFiles = getContentFiles($contentDir, 'ocrdata',true);
$nTextFiles   = count($arrTextFiles);


if ($nTextFiles >= $cPages)    echo "All text files present - nothing to do!\n";
else 
{   
    $nTextFiles = 0;
    if ($isPDF && pdfInfo::read($sourcePDF)->hasText() ) {
        include("inc/ocr_pdf.php");   // PDF TO TEXT CONVERT  
    }
    else {
        include("inc/ocr_tiff.php");   // TESSERACT OCR
    }
}

// NEU ZAEHLEN
$nTextFiles = count($arrTextFiles);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTextFiles >= $cPages) 
{
    // IF SUCCESSFUL SET STATE TO 3
    if ($cType == 'monograph') {
        if (getContentSteps($content_id)<$curStep) setContentSteps($content_id, $curStep);
    }

    $csvTextfiles = implode(_TRENNER, $arrTextFiles);
    $csvTextfiles = str_replace(_CONTENT_ROOT, "", $csvTextfiles);
    mysql_select("update content set content_pages_text='" . $csvTextfiles . "' where content_id=" . $content_id);

    $endmsg .= $nTextFiles . " files generated and database updated successfully.";

    $stepFinished = true;
}
else if (!_QUEUE_MODE) 
{
    echo _ERR . "Not all necessary text files could be prepared!";
    $stepErrors = true;
}


?>
